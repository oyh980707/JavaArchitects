# ArrayBlockingQueue

## 简介

ArrayBlockingQueue是基于数组实现的阻塞队列，当然初始化时会指定队列大小(即数组长度)，该阻塞队列依照FIFO的规则入队出队，是一个典型的有界"缓冲区"，容量一经创建就不可以改变了。利用ReentrantLock实现的线程安全，默认是一个非公平的锁策略，当然公平策略通常会降低吞吐量，但是会减少可变性，避免线程饥饿的现象。在线程池使用队列的场景中虽然常用的是LinkedBlockingQueue，但是JUC的作者明确在线程池ThreadPoolExecutor中给出了使用ArrayBlockingQueue有界队列，可以防止资源耗尽，但有可能很难调整和控制

## 源码分析

### 核心成员变量

这里的Object数组便是队列保存的元素了，lock用于并发控制的双条件锁(非常经典)，takeIndex表示出队的索引位置，putIndex表示入队的索引位置。由于items是final修饰的，所以可见其出实话后就不可变了，无法实现动态伸缩队列。

```java
final Object[] items;
int takeIndex;
int putIndex;
int count;
final ReentrantLock lock;
private final Condition notEmpty;
private final Condition notFull;
```

### 构造方法

一般分析源码我都会从构造方法开始分析，因为构造方法能够大致反映这个类的一些所必需的东西，通过ArrayBlockingQueue构造方法可以看出初始化一个ArrayBlockingQueue队列必须要指定容量，即队列大小。因为是数组实现的，切是一个固定容量的队列，必然需要指定大小。除此之外，初始化一些锁资源，这里采用的是非公平锁(也可以指定)。何为公平非公平，也就是在获取锁资源的时候尝不尝试插队，如果尝试插队获取，获取不到再去排队，则就是非公平锁，反之老老实实排队获取锁的则为公平锁。

通过第三个构造器我们知道，指定元素进行出实话，如果元素大于容量，那么多余的便直接抛弃丢弃。

```java
public ArrayBlockingQueue(int capacity) {
    this(capacity, false);
}
public ArrayBlockingQueue(int capacity, boolean fair) {
    if (capacity <= 0)
        throw new IllegalArgumentException();
    this.items = new Object[capacity];
    lock = new ReentrantLock(fair);
    notEmpty = lock.newCondition();
    notFull =  lock.newCondition();
}
 public ArrayBlockingQueue(int capacity, boolean fair,
                              Collection<? extends E> c) {
    this(capacity, fair);
    final ReentrantLock lock = this.lock;
    lock.lock(); // 加锁仅仅是为了可见行，非互斥
    try {
        int i = 0;
        try {
            for (E e : c) {
                checkNotNull(e);
                items[i++] = e;
            }
        } catch (ArrayIndexOutOfBoundsException ex) { 
            throw new IllegalArgumentException();// 通过捕获异常来判断队列已满
        }
        count = i;
        putIndex = (i == capacity) ? 0 : i;
    } finally {
        lock.unlock();
    }
}
```

### 入队原理

#### add&offer

add原理是调用父类方法，因为这是个队列，所以add一般会在内部转化为offer进行操作，在java.util.AbstractQueue#add中，直接调用this.offer()，即当前类的offer方法，这就是为什么将这两个方法放一起分析的原因。

重点分析offer的原理，这个方法是一个非阻塞的插入元素的方法，首先会对元素进行判空处理(由于数组)，先加锁，然后判断元素是否已满，如果已满则返回false入队失败，所以是非阻塞的。否则就进行enqueue操作，这个操作也很简单，就是将元素保存到putIndex位置，然后putIndex++(判断是否越界，若越界则取模回到0索引)，最后将记录元素的个数count++。

offer还有个超时方法，而且加锁的时候也是加可中断锁，在while中循环中阻塞timeout，当然在时间结束后会对再一次判断能否插入元素，若还是无法插入则返回false插入元素失败。

```java
// java.util.concurrent.ArrayBlockingQueue#add
public boolean add(E e) {
    return super.add(e);
}
// java.util.AbstractQueue#add
public boolean add(E e) {
    if (offer(e)) return true;
    else throw new IllegalStateException("Queue full");
}
// java.util.concurrent.ArrayBlockingQueue#offer
public boolean offer(E e) {
    checkNotNull(e);
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        if (count == items.length)
            return false;
        else {
            enqueue(e);
            return true;
        }
    } finally {
        lock.unlock();
    }
}
// 必须保证加锁的情况下调用该方法
private void enqueue(E x) {
    final Object[] items = this.items;
    items[putIndex] = x;
    if (++putIndex == items.length) // 放置越界，循环数组。putIndex标识下一个元素的存放位置
        putIndex = 0;
    count++;
    notEmpty.signal();
}
final int dec(int i) { // 特意提供了dec方法，自动从数组的头部转到尾部
    return ((i == 0) ? items.length : i) - 1;
}
public boolean offer(E e, long timeout, TimeUnit unit)
    throws InterruptedException {

    checkNotNull(e);
    long nanos = unit.toNanos(timeout);
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        // 多了这步操作，当元素已满，循环等待timeout时间后再判断
        while (count == items.length) {
            if (nanos <= 0)
                return false;
            nanos = notFull.awaitNanos(nanos);
        }
        enqueue(e);
        return true;
    } finally {
        lock.unlock();
    }
}
```

#### put

put操作也就简单几步，判空，加锁，判断是否满队，如果队列已满则要进行阻塞，否则直接入队。如果队列已满进入阻塞，需要一直等待队列元素被消费后唤醒，除非被中断就直接抛出InterruptedException。

```java
public void put(E e) throws InterruptedException {
    checkNotNull(e);
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        while (count == items.length)
            notFull.await();
        enqueue(e);
    } finally {
        lock.unlock();
    }
}
```

### 出队原理

#### poll&peek

poll很简单的操作，加锁，然后判断count是否等于零，来返回null或者出队。当然poll也有个超时的方法，和offer原理类似，源码不再列出。这里的peek则是查看第一个元素(这里的第一个不是数组中的第一个元素，而是第一个将要取出的元素)。使用itemAt获取takeIndex索引处的元素，当队列为空，自然返回的元素也是null，由于是数组，所以时间复杂度是O(1)。

```java

public E poll() {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        return (count == 0) ? null : dequeue();
    } finally {
        lock.unlock();
    }
}

public E peek() {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        return itemAt(takeIndex);
    } finally {
        lock.unlock();
    }
}
final E itemAt(int i) {
    return (E) items[i];
}
```

#### take

核心方法是take，其实原理很简单，也是加可中断锁，然后判断是否为空，不为空直接取出，为空就等待有元素了被唤醒后取元素。

```java
public E take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        while (count == 0)
            notEmpty.await();
        return dequeue();
    } finally {
        lock.unlock();
    }
}
```

### 元素的操作

#### removeAt()

该方法相对其他方法比较长，因为它需要删除数组中的某个元素。这个流程很简单，前提是要在加锁的前提但线程完成：
1. 是否为take元素，如果是直接将takeIndex++完事，然后count--
2. 否则的话需要将removeIndex的位置后面的所有元素往前挪，直到最后一个元素挪完。

#### clears

清空所有的元素，置为null后，等待GC处理。

#### drainTo

这个方法其实就是将队列中剩余的元素弹出指定大小，一般都是全部弹出放到指定的集合中，这个在LinkedBlockingQueue中使用到了，用于线程池shutdownNow时将剩余的所有任务全部排出到给定的集合，即不处理了剩下的任务了。

## 总结

1. 初始化需要指定容量，FIFO原则，
2. 底层采用不可变数组，不能扩容，使用循环环形缓冲机制，没有固定的起止位置，属于一种逻辑上的非线性结构。
3. 使用ReentrantLock锁保证互斥性，默认采用的非公平锁机制，也可指定公平策略，非公平策略比公平策略的吞吐量要好。
4. 入队，从putIndex位置添加，到了数组尾端则转向0索引，如果队列已满，则阻塞等待出队的线程来唤醒
5. 出队，从takeIndex位置取，到了数组尾端则转向0索引，如果队列为空，则阻塞，等待入队的线程来唤醒
6. 两个"指针"putIndex、takeIndex都是向同一个方向移动，利用指针和数组，形成环状结构，重复利用内存空间。








