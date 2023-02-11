# ThreadLocal

- Java 引用类型有哪几种？特点和场景？
- ThreadLocal是什么？原理？场景？
- ThreadLocal会产生的内存泄漏？


## Java 的引用类型

按照引用强度依次从强到弱分为：强引用、软引用（SoftReference）、弱引用(WeakReference)、虚引用(PhantomReference)四种

- 强引用

如果一个对象具有强引用，那垃圾回收器绝不会回收它。当内存空间不足，Java虚拟机宁愿抛OOM错误，使程序异常终止，也不会靠随意回收具有强引用的对象来解决内存不足的问题

强引用的引用方式:

```java
Object o = new Object();
```

特点：
1. 强引用可以直接访问目标对象
2. 强引用所指向的对象在任何时候都不会被系统回收，JVM宁愿抛出OOM异常，也不会回收强引用所指的对象
3. 强引用可能导致内存泄漏


- 软引用

如果一个对象只具有软引用，则内存空间足够，垃圾回收器就不会回收它；如果内存空间不足了，就会回收这些对象的内存。

软引用的创建方式：

```java
// 创建方式
Object o = new Object();
SoftReference<Object> softRef = new SoftReference<>(o);

// 使用作为缓存方式
Object o = new Object();
SoftReference<Object> srObj = new SoftReference<>(o);
if(null != srObj.get()) {
    o = srObj.get();
}
else {
    o = new Object();
    srObj = new SoftReference<>(o);
}
```

特点：
1. 软引用可以通过get()方法访问目标对象
2. 只有当JVM内存资源紧张时，只有软引用的对象会被回收
3. 软引用对象不会引起内存溢出


场景：
1. 软引用可用来实现内存敏感的高速缓存

- 弱引用

在垃圾回收器线程扫描它所管辖的内存区域的过程中，一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。也就是说，垃圾回收器再回收过程中，一旦遇到只有弱引用的对象直接回收掉。

弱引用的创建方式：

```java
Object o = new Object();
WeakReference<Object> wrObj = new WeakReference<>(o);
```

特点
1. 弱引用可以通过get()方法访问目标对象
2. 不管JVM堆空间使用情况如何，都会将只有弱引用的对象进行回收

场景：
1. 弱引用常用于Map数据结构中，防止一些关于Map的内存泄漏


- 虚引用

虚引用并不会决定对象的生命周期，如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾回收器回收。

虚引用的创建方式：
```java
// 该队列用于GC回收对象后将对象的保存到该队列中
ReferenceQueue<? super Object> queue = new ReferenceQueue<>();
PhantomReference<Object> prObj = new PhantomReference<>(new Object(), queue);
```

案例验证过程：
1. 弱引用被GC回收后会调用finalize()方法，有切仅调用一次
2. 当GC回收成功后，会将只有虚引用的对象加入到给定的ReferenceQueue中去

```java
public class DemoObj extends Thread{
    static DemoObj obj = new DemoObj();
    static ReferenceQueue queue = new ReferenceQueue<DemoObj>();
    //构造了TraceCanReliveObj对象的虚引用，并指定了引用队列，该队列用于对象被GC了将这个虚引用加入该引用队列
    static PhantomReference<DemoObj> objRef = new PhantomReference<DemoObj>(obj, queue);

    public static void main(String[] args) throws InterruptedException {
        DemoObj demoObj = objRef.get();
        System.out.println("通过虚引用获取实际对象无法获取："+demoObj);
        // 启动线程守护线程，
        Thread t = new DemoObj();
        t.setDaemon(true);
        t.start();

        // 将强引用去除
        obj = null;
        //第一次进行GC时会直接回收对象，回收前调用finalize()方法，这里我们让对象复活
        System.out.println("-------------first GC-------------");
        System.gc();
        Thread.sleep(1000);

        if(obj==null){
            System.out.println("obj 是 null");
        }else{
            System.out.println("obj 可用");
        }

        System.out.println("-------------second GC-------------");
        obj = null;
        // 第二次进行GC，由于finalize()只会被调用一次，因此第2次GC会回收对象，同时其引用队列应该也会捕获到对象的回收
        System.gc();
        Thread.sleep(1000);

        if(obj==null){
            System.out.println("obj 是 null");
        }else{
            System.out.println("obj 可用");
        }
    }
    @Override
    public void run() {
        while (true) {
            if (queue != null) {
                try {
                    // 如果被GC回收了，会加入到队列中去，可以从队列中获取
                    objRef = (PhantomReference<DemoObj>) queue.remove();
                } catch (InterruptedException e) {}
                if (objRef != null) {
                    System.out.println("DemoObj is delete by GC");
                }
            }
        }
    }
    @Override
    protected void finalize() throws Throwable {
        System.out.println("DemoObj call finalize()");
        super.finalize();
        //使得对象复活
        obj = this;
    }
}

// 结果
// 通过虚引用获取实际对象无法获取：null
// -------------first GC-------------
// DemoObj call finalize()
// obj 可用
// -------------second GC-------------
// DemoObj is delete by GC
// obj 是 null
```

特点：
1. 虚引用不可以通过get()方法访问目标对象
2. 虚引用必须和引用队列一起使用，它的作用主要是跟踪垃圾回收过程
3. 虚引用被GC回收后，会被加入到给定的ReferenceQueue队列中去

场景：
1. 对象回收跟踪
2. 虚引用管理堆外内存，即JVM用来管理直接内存


## ThreadLocal​

ThreadLocal是一个线程本地变量，用于解决多线程并发时访问共享变量的问题。


### ThreadLocal源码分析
​
#### set()

```java
​public void set(T value) {
    Thread t = Thread.currentThread();
    // 拿到当前线程对象，从线程对象中获取 ThreadLocalMap
    ThreadLocalMap map = getMap(t);
    if (map != null)
        // 以当前ThreadLocal对象为key 存放value
        map.set(this, value);
    else
        // 没有就创建，顺便插入值
        createMap(t, value);
}

// ThreadLocalMap

static class ThreadLocalMap {
    // 保存用户传入的value值，采用弱引用保存
    static class Entry extends WeakReference<ThreadLocal<?>> {
        Object value;
    }
    // 数组，存放每个Entry，也就是存放每个我们set进来的值
    private Entry[] table;
    
    // 根据给定的ThreadLocal对象计算索引下标
    // 然后将value封装成Entry，放到对应的下标位置
    private void set(ThreadLocal<?> key, Object value) {
        Entry[] tab = table;
        int len = tab.length;
        // 计算下标
        int i = key.threadLocalHashCode & (len-1);
    
        for (Entry e = tab[i];
             e != null;
             e = tab[i = nextIndex(i, len)]) {
             // 计算下标有冲突
             // 拿到对应的key，看是否同一个，如果是则替换
            ThreadLocal<?> k = e.get();
    
            if (k == key) {
                e.value = value;
                return;
            }
            // 如果拿到对应的value发现是空的
            if (k == null) {
                // 在设置期间清理哈希表为空的内容，保持哈希表的性质
                replaceStaleEntry(key, value, i);
                return;
            }
        }
        
        // 对应下标可以直接使用
        tab[i] = new Entry(key, value);
        int sz = ++size;
        // 扩容逻辑
        if (!cleanSomeSlots(i, sz) && sz >= threshold)
            rehash();
    }    
}
​
```
​
##### ThreadLocalMap的两种过期key数据清理方式
- 探测式清理:expungeStaleEntry()

- 启发式清理:cleanSomeSlots()
​
​#### get()
​
```java
​public T get() {
    Thread t = Thread.currentThread();
    // 拿到当前线程对象获取当前线程中的ThreadLocalMap
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        // 根据this作为key索引到 ThreadLocalMap.Entry
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            // 返回value
            T result = (T)e.value;
            return result;
        }
    }
    return setInitialValue();
}
    
ThreadLocalMap getMap(Thread t) {
    return t.threadLocals;
}
​
```
​
#### remove()
​
​​
```java
​public void remove() {
     ThreadLocalMap m = getMap(Thread.currentThread());
     if (m != null)
         m.remove(this);
}

// ThreadLocalMap
static class ThreadLocalMap {

    private void remove(ThreadLocal<?> key) {
        Entry[] tab = table;
        int len = tab.length;
        int i = key.threadLocalHashCode & (len-1);
        for (Entry e = tab[i];
             e != null;
             e = tab[i = nextIndex(i, len)]) {
            if (e.get() == key) {
                // 将Entry的referent置为null，虽说该弱引用会被回收，遵循编码规范
                e.clear();
                // 关键逻辑在于此
                // 将Entry中的value强引用到set的值，置为null，避免内存泄漏
                expungeStaleEntry(i);
                return;
            }
        }
    }
    
}
```
​
### ThreadLocal内存泄露问题

- 内存泄露

指程序中动态分配的堆内存由于某种原因没有被释放或者无法释放，造成系统内存的浪费，导致程序运行速度减慢或者系统奔溃等严重后果。内存泄露堆积将会导致内存溢出。

- 使用弱引用的好处
```java
ThreadLocal threadLocal = new ThreadLocal();
Object obj = new Object();
threadLocal.set(obj);
obj = null;
threadLocal = null;
```

![](./images/ThreadLocal%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F.png)

```text
正如代码所示，将obj对象放到ThreadLocalMap中后，将threadLocal和obj赋值为null，然后可以看到如图红色箭头Object对象被Entry对象中的value强引用，而ThreadLocal对象则被Entry对象的referent弱引用，当GC后该对象被回收，referent=null。如果不使用弱引用，这里永远无法被回收，则造成了内存泄漏。
那Object对象如何被回收的呢？每次set或者get时都会检查，当发现Entry.referent=null时，就会将他的value=null，从而让Object对象也得到了回收。也就是说当发现key=null的情况，也会清理掉其value，保持哈希表的性质
建议我们在每次在使用完之后需要手动的remove掉Entry对象
```

- 总结

  存在内存泄露的两个地方，ThreadLocal和Entry中Value；最保险还是不用时调用remove方法清理掉

- 避免内存泄露的最保障的方式

  只能手动调用remove处理，不要认为是弱引用就可以不用处理GC会帮忙清理掉，但是GC执行需要条件不可能在当前线程执行结束立即执行，即使GC帮忙清理掉弱引用，还有Entry中的value强引用，如果一直get、set的话是无法回收这部分内存，造成内存泄漏，所以手动remove更安全

### ThreadLocal的应用场景

#### 全局存储信息

##### Spring中使用到的

spring事务其实就是根据事务注解@Transactional生成代理类，然后在前置增强方法里获取connection，设置connection到threadlocal，开启事务。再执行原始方法，最后在后置增强方法中判断有无异常来进行事务回滚或提交，再释放连接。

```java
public abstract class TransactionSynchronizationManager {
	private static final ThreadLocal<Map<Object, Object>> resources =
			new NamedThreadLocal<>("Transactional resources");=
			...
}
```

##### Mybatis关于分页插件`PageHelper`

```java
// PageHelper.startPage()
public abstract class PageMethod {
    protected static final ThreadLocal<Page> LOCAL_PAGE = new ThreadLocal();

    protected static void setLocalPage(Page page) {
        LOCAL_PAGE.set(page);
    }
    ...
}
```

#### 解决线程安全问题

##### SimpleDateFormat线程安全问题

SimpleDataFormat的parse()方法，内部有一个Calendar对象，调用SimpleDataFormat的parse()方法会先调用Calendar.clear（），然后调用Calendar.add()，如果一个线程先调用了add()然后另一个线程又调用了clear()，这时候parse()方法解析的时间就不对了。

使用
```java
public class DateUtil {

    //使用线程，为每个线程创建局部变量
    private static ThreadLocal<SimpleDateFormat> t=ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	//获取局部变量
    private static SimpleDateFormat getSimpleDateFormat(){
        return t.get();
    }
    //格式化成date类型
    public static Date parse(String str) throws ParseException {
        return getSimpleDateFormat().parse(str);
    }
    //格式化成字符串
    public static String format(Date date){
        return getSimpleDateFormat().format(date);
    }
}
```

