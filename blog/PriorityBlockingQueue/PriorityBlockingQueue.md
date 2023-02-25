# PriorityBlockingQueue&DelayQueue&PriorityQueue&DelayedWorkQueue

## 简介

今天本来只是想分析一下PriorityBlockingQueue的基本实现，发现他有个非线程安全的类PriorityQueue，与此同时DelayQueue的底层就是PriorityQueue实现的，无非是在此基础上加了一把锁，支持线程安全了。然后与之类似的被用于ScheduledThreadPoolExecutor线程池中作为队列的DelayedWorkQueue。这四个完全可以关联在一起分析。


## PriorityBlockingQueue

PriorityBlockingQueue是一个具有优先级的无界阻塞队列，正因逻辑上是无界的，所以当资源耗尽时，尝试添加会OOM。那么优先级其实就是排序，是由内部使用堆算法进行保证的。

## 使用场景

- PriorityBlockingQueue与普通阻塞队列的不同之处就是在于其支持对队列中的元素进行比较，而决定出队的顺序，所以可以使用 PriorityBlockingQueue实现高优先级的线程优先执行

- PriorityQueue与PriorityBlockingQueue类似，在不需要线程安全的情况下可以使用PriorityQueue。

- DelayQueue由于底层是PriorityQueue实现的，支持延时的优先级队列。比如TimerQueue就是使用这个实现的一个定时器，关于定时方面的就可以使用这个队列来完成。

- DelayedWorkQueue是ScheduledThreadPoolExecutor的静态内部类，用于保存定时任务的任务队列。

### 源码分析

#### 构造方法

分析构造方法前先来简单看一看类中几个重要的属性

```java
// 默认初始容量大小
private static final int DEFAULT_INITIAL_CAPACITY = 11;
// 这里是为了给虚拟机的数组对象头部保留的一点空间
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
// 作为队列存储元素的数组
private transient Object[] queue;
// 元素个数
private transient int size;
// 比较器，优先级得遵循一个排序规则
private transient Comparator<? super E> comparator;
// 锁，支持并发操作的
private final ReentrantLock lock;
private final Condition notEmpty;
// 自旋锁，数组的扩容使用
private transient volatile int allocationSpinLock;
// 用于序列化的，用于网络传输保持有序等，实际在使用中没有用
private PriorityQueue<E> q;
```

我们的构造函数可以指定容量，那为什么是一个无界的优先级阻塞队列呢？因为数组创建出来就需要指定大小，在使用中再进行扩容，和List类似，没有设定容量就可以"无限"的增加容量(最大也就Integer.MAX_VALUE)。那么有没有办法突破数组的最大限度呢？有，使用Unsafe构建一个数组类，在机器内存支持的情况下可以无线申请容量。

```java
public PriorityBlockingQueue() {
    this(DEFAULT_INITIAL_CAPACITY, null);
}
public PriorityBlockingQueue(int initialCapacity) {
    this(initialCapacity, null);
}
public PriorityBlockingQueue(int initialCapacity, Comparator<? super E> comparator) {
    ...
}
// 主要看一下通过集合构造一个优先级阻塞队列
public PriorityBlockingQueue(Collection<? extends E> c) {
    this.lock = new ReentrantLock();
    this.notEmpty = lock.newCondition();
    boolean heapify = true; // 不知道堆顺序排序，则为true
    boolean screen = true;  // 是否需要筛选null，因为集合不能保证没有null
    if (c instanceof SortedSet<?>) {
        SortedSet<? extends E> ss = (SortedSet<? extends E>) c;
        this.comparator = (Comparator<? super E>) ss.comparator();
        heapify = false; // 有比较器，就知道如何排序，置为false
    }
    else if (c instanceof PriorityBlockingQueue<?>) { 
        PriorityBlockingQueue<? extends E> pq =
            (PriorityBlockingQueue<? extends E>) c;
        this.comparator = (Comparator<? super E>) pq.comparator();
        screen = false;// PriorityBlockingQueue类型的内部机制保证了没有null值
        if (pq.getClass() == PriorityBlockingQueue.class)
            heapify = false;
    }
    Object[] a = c.toArray();
    int n = a.length;
    // 强行使用Object对象数组存储，因为咱们队列中就是使用的Object数组，保持一致
    if (a.getClass() != Object[].class)
        a = Arrays.copyOf(a, n, Object[].class);
    if (screen && (n == 1 || this.comparator != null)) { // 筛选null
        for (int i = 0; i < n; ++i)
            if (a[i] == null)
                throw new NullPointerException();
    }
    this.queue = a;
    this.size = n;
    if (heapify) // 若不知道如何排序，则需要进行默认排序
        heapify();
}
```


#### 核心方法之offer(E e)

我们看到真正执行存储元素的就是有offer(E e)方法，其他方法都是通过调用该方法进行操作。就连offer(E e, long timeout, TimeUnit unit)超时方法都直接调用offer(E e)，因为这是个无界队列，所以无需阻塞。所以重点分析offer(E e)方法便可

具体的流程如下：
1. 入队的元素不能为null，否则直接抛空指针异常，严格控制队列元素不为null
2. 加锁
3. 判断容量是否足够，如果容量不足，尝试扩容
4. 对加入的元素进行堆调整排序
5. size+1
6. 解锁，返回true，入队完成

```java
public boolean add(E e) {
    return offer(e);
}
public void put(E e) {
    offer(e);
}
public boolean offer(E e, long timeout, TimeUnit unit) {
    return offer(e);
}

public boolean offer(E e) {
    if (e == null) // 控制元素不为null
        throw new NullPointerException();
    lock.lock(); // 加锁
    while ((n = size) >= (cap = (array = queue).length)) // 判断容量是否足够
        tryGrow(array, cap); // 尝试扩容
    try {
        Comparator<? super E> cmp = comparator;
        if (cmp == null)
            siftUpComparable(n, e, array); // 
        else
            siftUpUsingComparator(n, e, array, cmp); // 使用自定义的比较器进行堆排序
        size = n + 1; // size+1
        notEmpty.signal(); // 唤醒出队阻塞的线程
    } finally {
        lock.unlock(); // 解锁
    }
    return true; // 因为是无界队列，所以永远返回true
}
```

这里面比较重要的几个方法，使用了自旋锁的tryGrow扩容方法，进行堆排序的siftUpComparable和siftUpUsingComparator方法。

#### tryGrow

tryGrow在这里设计的还不错，对锁运用的淋淋尽致。首先是由offer方法调用tryGrow方法的，势必进行了加锁，所以这里需要释放锁。然后再加自旋锁，我对此理解有以下几点

1. 虽然源码中明确这里需要释放锁，但是我觉得也可以不释放。在高并发的情况下，扩容期间是需要耗时的，所以在这期间如果不释放锁，那么所有堆队列的操作包括出队都会阻塞。如果释放了，那么入队出队将不会收到影响可以正常操作，至少不会阻塞住，提高了并发量。

2. allocationSpinLock在这里是用来CAS变量的自旋锁，只能有一个线程获取到，然后创建新的扩容好的数组，赋值给变量newArray。开始我这里有个疑问，就是当一个线程在执行赋值操作完后，还没有释放自旋锁，那么另外一个线程就在判断newArray是否为null，我开始认为另外一个线程可以判断newArray不为null的情况，在我的多线程测试后，实际上这里newArray没有加volatile关键字，第二个线程是看不到第一个线程对newArray变量赋值的。而且这里newArray千万不能加volatile，不然会出现重复进行数组的值拷贝工作，可能会出现问题。

3. 即然容量不够，那么所有的入队操作都会来到这个tryGrow方法进行扩容，如果获取不到自旋锁，势必会调用Thread.yield()短暂的让出CPU时间片。由于自旋锁是锁住对新数组的创建，是有可能一个线程创建好新的数组后释放自旋锁后另一个线程也创建了新的扩容数组，这里是不会重复的进行数组拷贝工作的，因为他会在加互斥锁后判断queue是否还是原来的数组，如果是才进行拷贝复制工作，否则就跳过了，说明在这之前已经完成了数组扩容工作。

所以在这小结一下，扩容数组时需要释放独占锁，以提高并发量。通过自旋锁来进行扩容数组的构造，然后重新加锁将值拷贝到新的数组中去，完成扩容。

```java
private void tryGrow(Object[] array, int oldCap) {
    lock.unlock(); // 由于调用该方法时已经加了独占锁，这里需要释放
    Object[] newArray = null;
    if (allocationSpinLock == 0 &&
        UNSAFE.compareAndSwapInt(this, allocationSpinLockOffset, 0, 1)) { // CAS尝试获取自旋锁，就是在allocationSpinLock值为0的情况下赋值为1，成功则获取自旋锁成功，失败则获取自旋锁失败
        try {
            int newCap = oldCap + ((oldCap < 64) ? // 根据目前容量库容的程度也不一样
                                    (oldCap + 2) : // 容量小于64，扩容大概一倍
                                    (oldCap >> 1)); // 容量大于64，扩容大概0.5倍
            if (newCap - MAX_ARRAY_SIZE > 0) {    // 可能超过最大容量
                int minCap = oldCap + 1;
                if (minCap < 0 || minCap > MAX_ARRAY_SIZE)
                    throw new OutOfMemoryError();
                newCap = MAX_ARRAY_SIZE;
            }
            if (newCap > oldCap && queue == array)
                newArray = new Object[newCap]; // 构造新的数组
        } finally {
            allocationSpinLock = 0; // 释放自旋锁
        }
    }
    if (newArray == null) // 说明有线程已经在分配了，主动让开CPU一会
        Thread.yield();
    lock.lock(); // 操作queue需要加锁
    if (newArray != null && queue == array) { // 必须判断queue == array，避免多个线程都进行扩容
        queue = newArray;
        System.arraycopy(array, 0, newArray, 0, oldCap); // 将原来的数组中的值拷贝到新的扩容数组中去
    }
}
```

#### siftUpComparable&siftUpUsingComparator

这两个方法其实是一样的逻辑，唯一的区别就是他们使用的比较器不一样，如果在构造队列的时候提供了比较器，那么使用siftUpUsingComparator进行存放元素，反之使用元素自身的比较特性去进行比较。

分析siftUpComparable的执行流程

1. 将元素x向上转型为Comparable类型，使其可以调用compareTo方法。注意如果不指定比较器，元素必须是直接或间接的实现Comparable接口，不然会报错java.lang.ClassCastException

2. 找到父元素，如果判断即将入队的元素"大于"父元素，则直接入队，否则交换父元素和入队元素的位置，继续向上找，直到满足条件为止。

小结：所有的元素入队都放到最后面，然后与父元素进行判断大小看是否需要交换，有点类似冒泡排序，冒泡是每个元素进行比较，这里逻辑上是二叉树冒泡的方式，将优先级最小的冒泡到数组的0号位置。便于取元素时可以取到优先级最小的元素。siftUpUsingComparator雷同。

```java
private static <T> void siftUpComparable(int k, T x, Object[] array) {
    Comparable<? super T> key = (Comparable<? super T>) x;
    while (k > 0) {
        int parent = (k - 1) >>> 1;
        Object e = array[parent];
        if (key.compareTo((T) e) >= 0)
            break;
        array[k] = e;
        k = parent;
    }
    array[k] = key;
}
```

#### 核心方法之dequeue()

我们看到所有的获取元素方法都会先加锁然后调用dequeue()方法来获取0号位的元素(即优先级最小的元素)，这里的take和poll(timeout,unit)是阻塞的获取元素，后者有超时时间。peek时查看第一个元素，即便是查看第一个元素在获取的时候也要加锁。

核心方法就是dequeue()，将数组的第0号为元素返回，当然返回前需要调整目前数组中的元素堆顺序。我们将0号元素取出，然后将最后一个元素放到首位置，进行堆排序，排序后第一个元素就是优先级最小的一个。

```java
public E poll() {
    ...
    dequeue();
    ...
}
// 条件阻塞等待有获取的元素
public E take() throws InterruptedException {
    ...
    while ( (result = dequeue()) == null)
        notEmpty.await();
    ...
}
// 条件超时阻塞
public E poll(long timeout, TimeUnit unit) throws InterruptedException {
    ...
    while ( (result = dequeue()) == null && nanos > 0)
        nanos = notEmpty.awaitNanos(nanos);
    ...
}
public E peek() {
    ...
        return (size == 0) ? null : (E) queue[0];
    ...
}
private E dequeue() {
    int n = size - 1;
    if (n < 0)
        return null;
    else {
        Object[] array = queue;
        E result = (E) array[0];
        E x = (E) array[n];
        array[n] = null;
        Comparator<? super E> cmp = comparator;
        if (cmp == null)
            siftDownComparable(0, x, array, n);
        else
            siftDownUsingComparator(0, x, array, n, cmp);
        size = n;
        return result;
    }
}
```

#### siftDownComparable&siftDownUsingComparator

逻辑很简单，就是将第k位置的元素x，找到合适优先级的位置。从元素k位置，找其两个左右子孩子的优先级最小的元素进行比较，如果大于子孩子的优先级，则需要交换元素，然后将子元素作为父元素继续下一轮循环的判断。直到将其排好序。

```java
private static <T> void siftDownComparable(int k, T x, Object[] array, int n) {
    if (n > 0) {
        Comparable<? super T> key = (Comparable<? super T>)x;
        int half = n >>> 1;           // 循环的终止条件，因为是二叉树结构，无需遍历所有元素
        while (k < half) {
            int child = (k << 1) + 1; // 假定左孩子元素优先级最小
            Object c = array[child];
            int right = child + 1; // 取出右子元素
            if (right < n &&
                ((Comparable<? super T>) c).compareTo((T) array[right]) > 0) // 比较左右孩子大小
                c = array[child = right]; // 取优先级最小的元素赋值给c
            if (key.compareTo((T) c) <= 0) // 如果当前父元素比子元素小，则跳出循环
                break;
            array[k] = c; // 否则需要交换父子元素，然后进行下一轮循环判断子元素的子元素大小
            k = child;
        }
        array[k] = key;
    }
}
```

#### FIFOEntry

如果我们的元素不支持排序，那么可以定义使用辅助类来打破默认的排序规则，具体的实现如下，完全可以按照自己的需求来进行排序。
该实现于源码java.util.concurrent.PriorityBlockingQueue注释之中

```java
class FIFOEntry<E extends Comparable<? super E>> implements Comparable<FIFOEntry<E>> {
    static final AtomicLong seq = new AtomicLong(0);
    final long seqNum;
    final E entry;
    public FIFOEntry(E entry) {
        seqNum = seq.getAndIncrement();
        this.entry = entry;
    }
    public E getEntry() {
        return entry;
    }
    public int compareTo(FIFOEntry<E> other) {
        int res = entry.compareTo(other.entry);
        if (res == 0 && other.entry != this.entry)
            res = (seqNum < other.seqNum ? -1 : 1);
        return res;
    }
}
```


#### removeAt(i)&drainTo

和poll等方法一样，removeAt只是指定了删除某个元素，而poll等方法只删除第一个元素。具体实现逻辑一摸一样，只是除去的值位置不同罢了。

drainTo方法原理很简单，给定元素集合将循环给定次数，每次都调用dequeue方法将元素排出放到指定集合中去。核心方法就上面几个，其他方法都是在此基础之上进行封装调用。

## 小结

通过源码分析我们可以知道，PriorityBlockingQueue是一个阻塞式无界队列，底层是数组实现的，采用的堆排序算法，每次取元素都是从0号元素取，每次操作队列后要进行堆调整，将优先级最小的冒到0号位置。创建队列时如果不指定比较器，则插入的元素不可以为不可比较对象(即没有实现Comparable接口的对象)。如果队列元素没办法排序，或者排序没有达到自己想要的，可以自定一个包装类实现Comparable接口来完成自定义排序规则。

## PriorityQueue

PriorityQueue基于优先级堆的无界优先级队列，优先级队列的元素按照它们的自然顺序进行排序(当然元素必须直接或间接实现了Comparable接口)，或者由队列构造时提供的Comparator进行排序。该队列的头是相对于指定顺序的最小元素

PriorityQueue是java.util包下的一个类，自然就不能保证并发安全了，而PriorityBlockingQueue是java.util.concurrent包下的一个类，是具有并发安全的一个类。他们的区别就是一个是并发安全一个并发不安全的区别。其他的实现一摸一样，都是底层使用的数组，排序使用堆排序，取元素从队列的头(0号索引位置)开始取，每次操作完队列后需要进行堆调整排序。

## DelayQueue

DelayQueue是一个由延迟元素组成的无界阻塞队列，其中的元素只有在其延迟过期时才能被取走，队列头时通过比较器比较后优先级最大的元素(这得看如何定义的比较器，默认值最小)。如果队列头部元素没有延迟过期，则轮询将返回null。当元素的getDelay(TimeUnit.NANOSECONDS)方法返回一个小于等于零的值时，就会发生过期，获取时将会被返回。保存的元素必须都是Delayed类型的，最常用的DelayedTimer实现了Delayed，实现了里面的getDelay方法，并实现了排序规则，规定了队列头部就是延时最小的元素。

DelayQueue是JUC包下的，是一个线程安全的队列，使用了ReentrantLock互斥锁保证了并发安全，利用Condition条件作为线程间通信，保证队列元素能够及时的被处理。内部的实现和PriorityBlockingQueue大同小异，都是采用的堆排序算法保证了优先级顺序。

## DelayedWorkQueue

DelayedWorkQueue类是专用的延迟队列，它是ScheduledThreadPoolExecutor中的一个静态内部类，说明这个类只服务于调度线程池作为其队列使用，而且它只能保存RunnableScheduledFutures类型的元素。



> JDK 8 源码
> https://blog.csdn.net/b1303110335/article/details/112221278