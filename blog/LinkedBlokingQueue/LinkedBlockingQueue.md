# LinkedBlockingQueue

## 前言

要开始分析数据结构中的队列相关的源码了，今天来看看一个非常常用的阻塞队列数据结构LinkedBlockingQueue，它是JDK1.5开始出现的，由Doug Lea这位大佬写的，JUC并发包下都是这个大佬写的。

我们可以看到LinkedBlockingQueue继承了AbstractQueue抽象类，实现了BlockingQueue接口，那么可以得知该类具有队列阻塞的功能，具有队列的一些方法和行为。通过Doug Lea写的注释可以很清晰的认识这个类的特性，首先LinkedBlockingQueue是基于链表节点的有界队列，默认最大长度为Integer.MAX_VALUE。该队列遵循FIFO原则，采用的是尾插法，从队首进行取元素操作。这种链表队列通常要比数组队列具有更高的吞吐量，在高并发场景下情况都很难预测。

## LinkedBlockingQueue并发锁机制

因为LinkedBlockingQueue是一个并发安全的阻塞队列，必然要用到锁来解决多线程的竞争关系的。它是一个基于ReentrantLock锁机制，注意使用的是非公平的锁，不保证等待时间长的线程会先获得锁。对比ConcurrentLinkedQueue是非阻塞的无界队列，采用CAS+自旋操作解决多线程之间的竞争，所以在很多高并发场景下ConcurrentLinkedQueue性能要优于LinkedBlockingQueue队列的。

我们看看源码中的使用，类似读写锁，takeLock就是读锁，但凡设计到读取队列中的元素就会用takeLock进行上锁，然后访问，最后进行锁的释放。写锁putLock流程类似。

这里读写锁都创建了对应的Condition，这是用于消费者生产者模式下的。当队列为空，从队列中消费数据时就会调用notEmpty.wait()释放锁，然后进行阻塞等待生产者产生数据。每次当生产者生产数据后，会调用notFull.signal()唤醒等待的消费者进行数据消费，notFull原理一样，这就是条件锁在这里面的使用。

值的注意的是，put()元素时当元素达到容量时是会一直阻塞直到有足够容量入队成功，take()雷同，当队列中没有元素时是会一直阻塞直到有有元素后元素出队成功，他们都可以被中断阻塞的。这就是他们与offer()、poll()不同之处。offer()和poll()会立即返回，或者设置超时时间timeout。

锁的具体使用会在后面进行源码分析。

```java
private final ReentrantLock takeLock = new ReentrantLock();
private final Condition notEmpty = takeLock.newCondition();
private final ReentrantLock putLock = new ReentrantLock();
private final Condition notFull = putLock.newCondition();
```

## 元素迭代

LinkedBlockingQueue实现了Iterator，所以具有了迭代的功能，当调用iterator()方法，便返回一个迭代器。返回的迭代器是弱一致性的。什么是弱一致性后面迭代器类分析的时候提到了。他与ConcurrentLinkedQueue一样都是弱一致性的。

```java
public Iterator<E> iterator() {
    return new Itr();
}
```

## 解析源码

粘贴的代码由于篇幅原因，非必要的代码进行了删减，保留核心代码，完整代码请自行查看。

### 构造函数

分析LinkedBlockingQueue的构造函数，我们可以很清晰的看到默认队列的最大长度为Integer.MAX_VALUE，也可以自定义长度，当给定集合来创建队列的时候，无法指定队列的长度，我们看到capacity是一个final修饰的字段，一旦赋值就无法再做更改。队列的头节点为一个item为null的哨兵节点。

提示：我们在分析线程池的时候，提到了如何动态调整线程池，由于capacity是final修饰的，那么如何调整阻塞队列的容量呢？copy一份LinkedBlockingQueue改个名，然后将capacity改成非final属性即可。参考美团自定义的ResizableCapacityLinkedBlockIngQueue队列。

```java
public LinkedBlockingQueue() {
    this(Integer.MAX_VALUE);
}
public LinkedBlockingQueue(int capacity) {
    if (capacity <= 0) throw new IllegalArgumentException();
    this.capacity = capacity;
    last = head = new Node<E>(null);
}
public LinkedBlockingQueue(Collection<? extends E> c) {
    this(Integer.MAX_VALUE);
    // add elements.
}
```

final修饰的capacity和count，capacity记录当前对立的容量，默认为Integer.MAX_VALUE也是最大容量。count维护的是队列中的元素个数。通过size()方法进行返回，通过remainingCapacity()还可以返回剩余容量。就是capacity-count。

```java
private final int capacity;
private final AtomicInteger count = new AtomicInteger();

public int size() { return count.get(); }
public int remainingCapacity() { return capacity - count.get(); }
```

### put(E)和take()

为什么将他们放一块进行分析？因为他们都是阻塞式的的操作，而且相互也有一些通过条件锁来进行交互的操作。比如当put时，队列已满则会阻塞。此时当take()后就会唤醒一个正在阻塞的take操作。当然其他的取数据操作也会唤醒，这里只是因为这两个方法都是可中断阻塞式的存取元素。

#### put(E)

put方法的基本原理：
1. 构建链表节点。
2. 加可中断写锁。
3. 判断队列是否达到最大容量，如果达到了最大容量，则不能进行元素的入队，需要等待有足够的容量被唤醒后继续后面的操作。
4. 将元素添加到阻塞队列中去。
5. 将元素计数器加1，c = count++。
6. 判断添加后的容量是否满了，如果没满则唤醒其余要入队元素的线程，避免被其他因容量不足而阻塞的入队线程造成饥饿等待
7. 释放写锁
8. 如果添加元素前队列为空，有可能所有的取元素的线程都阻塞了(因为队列为空而阻塞的取元素线程)，此时需要唤醒一个阻塞的取元素线程，避免取元素的线程都被阻塞，造成饥饿等待。

```java
public void put(E e) throws InterruptedException {
    int c = -1; // 约定了负数表示失败
    Node<E> node = new Node<E>(e); // 构建节点
    putLock.lockInterruptibly(); // 加可中断写锁
    try {
        while (count.get() == capacity) {
            // 队列已满，无法再入队，进行阻塞释放锁，等待被唤醒，注意使用while
            notFull.await();
        }
        enqueue(node); // 入队
        c = count.getAndIncrement(); // c = count++;
        if (c + 1 < capacity)
            // 只要容量没满，就唤醒保存元素的线程，避免put元素的线程都被阻塞了
            notFull.signal();
    } finally {
        putLock.unlock(); // 释放写锁
    }
    if (c == 0)
        // 唤醒取元素的线程，这里是判断是避免取元素的线程都被阻塞，造成死锁。
        signalNotEmpty();
}

// 入队方法再简单不过了，因为是加过锁操作的
// 所以直接将尾节点的next指向插入节点，然后last引用指向插入节点。
private void enqueue(Node<E> node) {
    last = last.next = node;
}
```

#### take()

take方法的基本原理：
1. 加可被中断的读锁。
2. 判断是否有元素，如果没有元素就进行阻塞并释放锁资源，直到有元素才进行后面的操作。
3. 从队列中取出元素。
4. 元素计数器-1，c = count--。
5. 有多余的元素会唤醒其他正在取数据而阻塞的线程。
6. 释放读锁。
7. 如果判断此时元素已经存满了，说明添加元素的线程可能基本都阻塞，需要唤醒一个添加元素的线程。

```java
public E take() throws InterruptedException {
    // 中断加锁，阻塞是可以被中断的
    takeLock.lockInterruptibly();
    try {
        // while判断队列是否有元素，如果没有就阻塞释放锁
        // 等待往队列添加元素时通知唤醒线程，当然还是需要判断是否存在元素，直到有元素时才处理
        // 每添加一个元素时只会唤醒一个取元素的线程
        while (count.get() == 0) {
            notEmpty.await();
        }
        x = dequeue(); // 从队列中获取一个元素
        c = count.getAndDecrement(); // c = count--;
        if (c > 1)
            // 如果队列中有跟多的元素，将唤醒正在等待取元素的线程
            notEmpty.signal();
    } finally {
        takeLock.unlock(); // 释放读锁
    }
    if (c == capacity)
        // 唤醒存元素的线程进行put等操作。判断是为了避免保存元素的线程都被阻塞
        signalNotFull();
    return x;
}

// head 指向的是一个哨兵节点，他有个特点，哨兵节点的item为null
// 出队的流程就是将哨兵节点的next指向自己，等待GC销毁，然后将下一个节点(即返回的节点)设置成哨兵节点
// 其实就是摘掉哨兵节点，将下一个节点设置成哨兵节点。
private E dequeue() {
    Node<E> h = head;
    Node<E> first = h.next;
    h.next = h; // help GC
    head = first;
    E x = first.item;
    first.item = null;
    return x;
}
```

### offer(E)和poll()

#### offer(E)&offer(E,long,TimeUnit)

很清楚的看到它和put(E)的区别，在下的(1)(2)处，添加元素不会阻塞，只会在容量充足的时候添加，没有足够的容量直接返回false表示添加失败。

```java
public boolean offer(E e) {
    if (count.get() == capacity) // (1) 判断容量不足，直接返回false，不阻塞
        return false;
    ...
    try {
        if (count.get() < capacity) { // (2) 加锁后，容量足够才添加
            enqueue(node);
            c = count.getAndIncrement();
            if (c + 1 < capacity)
                notFull.signal(); // 唤醒，避免因为调用put而阻塞线程
        }
    } finally {
        putLock.unlock();
    }
    if (c == 0)
        signalNotEmpty(); // 唤醒调用take被阻塞的线程，避免目前所有的因队列为空调用take而阻塞的线程 没有得到唤醒
    return c >= 0; // 添加元素了c才大于0，判断是否添加成功
}
```

offer(E,long,TimeUnit)就比put(E)和offer(E)多了一个超时等待，原理类似put(E)，具体不同之处在如下源码分析注释中。

```java
public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
    long nanos = unit.toNanos(timeout); // 超时时间
    ...
    try {
        while (count.get() == capacity) {
            if (nanos <= 0) // 判断是否已经超时，超时返回false
                return false;
            nanos = notFull.awaitNanos(nanos); // 进行超时阻塞，返回nanos是剩余可以阻塞的时间
        }
        enqueue(new Node<E>(e));
        c = count.getAndIncrement();
        if (c + 1 < capacity)
            notFull.signal();
    } finally {
        putLock.unlock();
    }
    if (c == 0)
        signalNotEmpty();
    return true;
}
```

#### poll(E)&poll(E,long,TimeUnit)

基本原理已经在put(E)和take()方法分析的足够详细，这里只需要分析出他们的不同之处，poll也是一个非阻塞的弹出队首的元素。poll(E,long,TimeUnit)也类似和上面的offer超时方法分析雷同，无非就是在take()上面加了一个超时判断而已。这里就不做过多分析。

```java
public E poll() {
    if (count.get() == 0) // 如果没有元素，直接返回null
        return null;
    E x = null; // 取失败也返回null
    try {
        if (count.get() > 0) { // 有元素则进行取元素操作
            x = dequeue();
            c = count.getAndDecrement();
            if (c > 1)
                notEmpty.signal();
        }
    } finally {
        takeLock.unlock();
    }
    if (c == capacity)
        signalNotFull();
    return x;
}
```

### peek()

在不弹出队首的元素前提下，获取队首的元素。该方法也会加读锁，因为涉及到读取链表的元素都会加读锁，否则就谈不上并发安全了，也可以通过CAS来获取，但这里面统一使用的锁机制保证的。

```java
public E peek() {
    if (count.get() == 0)
        return null;
    takeLock.lock();
    try {
        Node<E> first = head.next;
        if (first == null)
            return null;
        else
            return first.item;
    } finally {
        takeLock.unlock();
    }
}
```

### remove(E)

从链表中删除某个节点，返回是否删除成功。因为删除操作涉及到读和写，所以需要加读锁和写锁。在这里面需要注意的是他与迭代器之间产生的数据弱一致性问题。就是当删除某个元素，而迭代器又正好遍历到该元素，所以删除节点的时候这个节点的next保持不变，为了保证迭代器的正常遍历。

这里顺便提一点加锁的技巧，如我们这里加读写锁是先加写锁，再加读锁，如果代码中有先加读锁再加写锁的逻辑，一起使用时很可能会产生死锁现象。

```java
public boolean remove(Object o) {
    if (o == null) return false;
    fullyLock(); // 加读写锁
    try {
        for (Node<E> trail = head, p = trail.next;
                p != null;
                trail = p, p = p.next) { // 遍历每一个节点
            if (o.equals(p.item)) { // 查找对应的元素
                unlink(p, trail); // 查询到则断链
                return true;
            }
        }
        return false;
    } finally {
        fullyUnlock();
    }
}

// p.next没有改变，以允许遍历p的迭代器保持它们的弱一致性保证。
// 换句话说，就是迭代器刚好遍历到p，而p刚好被删除，为了保证迭代器的正确进行，避免迭代器在调用p.next()出现不正常的逻辑。
// 所以此处并没有将p.next()指向自己
// 唯一指向自己的是正确从队首弹出的元素，会讲哨兵节点的next指向自己，迭代器在遇到这种情况，直接指向head头节点开始遍历。
void unlink(Node<E> p, Node<E> trail) {
    // 进入该方法的条件是加了读写锁
    p.item = null;
    trail.next = p.next;
    if (last == p)
        last = trail;
    if (count.getAndDecrement() == capacity) // 判断断链前容量是否是满的，如果满的需要唤醒操作添加元素的线程
        notFull.signal();
}

// 加读写锁，这里需要注意的是，保证程序中没有先加takeLock锁再加putLock锁的逻辑，不然和这里的加锁会产生死锁情况
void fullyLock() {
    putLock.lock();
    takeLock.lock();
}
// 释放读写锁，与加锁流程相反
void fullyUnlock() {
    takeLock.unlock();
    putLock.unlock();
}
```

### signalNotEmpty()&signalNotFull()

这里有个细节，细心的朋友已经看到了，为什么这两个方法需要加锁来操作呢？

```java
private void signalNotEmpty() {
    takeLock.lock();
    try {
        notEmpty.signal();
    } finally {
        takeLock.unlock();
    }
}
private void signalNotFull() {
    putLock.lock();
    try {
        notFull.signal();
    } finally {
        putLock.unlock();
    }
}
```

在源码中这两个方法都是在操作结束，释放读锁或写锁之后，才进行的调用唤醒一个线程。那么如果这里不加锁，我们来分析以下这种情况
假设signalNotEmpty()方法不加读锁，会发生什么情况呢？
1. 当线程1进行take()操作执行到了1)处进行了阻塞
2. 线程2进行take()操作执行到了2)处
3. 线程3进行take()操作执行到了4)处
4. 那么现在如果4)处调用signalNotFull()方法会唤醒一个阻塞线程
5. 此时线程1被唤醒，但是因为线程2加锁了，线程1拿不到锁就会阻塞
6. 线程2执行3操作时就会抛出IllegalMonitorStateException异常，然后最终返回x为一个null，但是这里加锁了情况就不一样了

结论就是不加锁就调用该锁创建的condition的signal()方法就会抛出IllegalMonitorStateException异常

```java
// 注意，假设不加读锁
private void signalNotEmpty() {
    notEmpty.signal();
}

public E take() throws InterruptedException {
    E x;
    takeLock.lockInterruptibly();
    try {
        while (count.get() == 0) {
            notEmpty.await(); // 1) 阻塞，释放锁
        }
        ... // 2) 读取操作
        if (c > 1)
            notEmpty.signal(); // 3)唤醒take操作的线程
    } finally {
        takeLock.unlock();
    }
    if (c == capacity)
        signalNotFull(); // 4) 调用假设未加锁的signalNotFull()方法，进行唤醒一个take()操作的线程
    return x;
}
```

已验证如上逻辑代码如下：

```java
public class Test {
    static ReentrantLock writeLock = new ReentrantLock();
    static Condition writeCondition = writeLock.newCondition();
    public static void main(String[] args) {
        new Thread(()->{
            writeLock.lock();
            try {
                writeCondition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                writeLock.unlock();
            }
        }, "T1").start();

        new Thread(()->{
            writeLock.lock();
            writeCondition.signal(); // 正常解锁
            writeLock.unlock();
        }, "T2").start();

        new Thread(()->{
            writeCondition.signal(); // 抛出IllegalMonitorStateException异常
        }, "T3").start();
    }
}
```

我们来看看AQS下的Condition接口的await()方法是如何定义的吧，找到java.util.concurrent.locks.Condition#await()接口，看看注释就会发现，提到了这么一句话，大致意思是：假定调用这个await()方法时，线程持有该Condition关联的锁资源。取决于具体实现去决定是否是这种情况，如果调用await方法的线程没有持有该Condition关联的锁资源，则必须抛出异常(例如IllegalMonitorStateException)，并且必须按照这种规则去实现。
```text
The current thread is assumed to hold the lock associated with this Condition when this method is called. It is up to the implementation to determine if this is the case and if not, how to respond. Typically, an exception will be thrown (such as IllegalMonitorStateException) and the implementation must document that fact.
```

所以总的来说signalNotEmpty()和signalNotFull()必须加该Condition关联的锁资源才能调用signal()。具体的将在AQS进行详细的展开。

### drainTo()

就是将该队列的的元素从头节点开始删除掉指定数量的元素，并将删除的元素填充到给定的集合中。核心源码如下：

```java
public int drainTo(Collection<? super E> c) {
    return drainTo(c, Integer.MAX_VALUE);
}
public int drainTo(Collection<? super E> c, int maxElements) {
    ...
    while (i < n) { // 将节点值加入集合c中，然后将节点的next指向自己，等待被GC回收
        Node<E> p = h.next;
        c.add(p.item);
        p.item = null;
        h.next = h;
        h = p;
        ++i;
    }
    ...
}
```

它的使用场景之一是使用在线程池中，当调用线程池的shutdownNow()方法就会将剩余阻塞在队列的任务全部弹出返回不做任务处理了。源码如下

```java
// java.util.concurrent.ThreadPoolExecutor

public List<Runnable> shutdownNow() {
    ...
    advanceRunState(STOP);
    interruptWorkers();
    tasks = drainQueue();
    ...
}
private List<Runnable> drainQueue() {
    BlockingQueue<Runnable> q = workQueue;
    ArrayList<Runnable> taskList = new ArrayList<Runnable>();
    q.drainTo(taskList);
    ...
}
```

### 内部类

在LinkedBlockingQueue中有三个内部类，Node作为链表的节点而存在，Itr用于生成迭代器用于变量元素，LBQSpliterator作为并行迭代遍历而存在。

#### Node

Node是一个静态内部类，内容很简单，就是支撑链表的节点存在的，保存入队的元素E，链接下一个节点next。所以该队列LinkedBlockingQueue是一个底层用单链表实现的队列。我们在源码中看到Node静态内部类放到代码的最前面，而Itr、LBQSpliterator和一些序列化的方法放到最后面，其实代码的方法也是有一定的书写风格，利于阅读者更好的阅读源码，大佬写代码太细节了。

```java
static class Node<E> {
    E item;
    Node<E> next;
    Node(E x) { item = x; }
}
```

#### Itr

注意是private私有的内部类，类外是无法访问到这个类的。实现的方法也是Iterator接口基本的一些方法，支持元素的遍历，通过iterator()来构造一个迭代器使用，当然这个迭代器是弱一致性的，也就是说在遍历元素的时候会出现遍历到一个元素之后，该元素被删除了，还能通过next()来获取该元素的值，因为currentElement已经保存了该值，即便被删除也能正常返回。其次元素在被删除了其实是从列表中脱离出来，然后等待被GC回收。

```java

public Iterator<E> iterator() {
    return new Itr();
}

private class Itr implements Iterator<E> {
    private Node<E> current;
    private Node<E> lastRet;
    private E currentElement;
    Itr() {...}
    public boolean hasNext() {...}
    private Node<E> nextNode(Node<E> p) {...}
    public E next() {...}
    public void remove() {...}
}
```

#### LBQSpliterator

对于这个类其实大部分开发者都不用到，我刚开始看源码的时候压根儿都不知道这是个啥，查资料发现就是可以并行遍历的迭代器，感兴趣的可以了解下：https://blog.csdn.net/sl1992/article/details/100149187

## 总结

LinkedBlockingQueue是一个阻塞队列，采用的是链表的形式进行存储，内部由两个ReentrantLock锁来实现出入队列的线程安全，存取互不干扰。条件阻塞由各自的Condition对象的await和signal来实现等待和唤醒功能，出队【队列count=0，无元素可取时，阻塞在该条件(notEmpty)上】，入队【队列count=capacity，放不进元素时，阻塞在该条件(notFull)上】，这里也体现了生产者消费者的设计模型。

> JDK 8源码
> 《深入理解Java高并发编程》
> https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html
> https://blog.csdn.net/sl1992/article/details/100149187
> https://blog.csdn.net/qq_37050329/article/details/116295082
> https://www.cnblogs.com/chafry/p/16782730.html

每天进步一点点就够了