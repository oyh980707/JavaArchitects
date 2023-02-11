# synchronized

## 概述

synchronized中文意思是同步，在Java里是一个关键字，也称之为“同步锁“。它的作用是保证在同一时刻，被修饰的代码块或方法只会有一个线程执行，以达到保证并发安全的效果。

在JDK1.5之前synchronized是一个重量级锁，效率低下。随着JDK1.6对synchronized进行的各种优化后，从而有了偏向锁、轻量级锁、自旋锁、适应性自旋锁、锁消除、锁粗化等等，他们还会存在锁升级，让synchronized并不会显得那么重了。

synchronized是可以保证原子性、有序性、可见性

## synchronized的使用

### 修饰方法

- 修饰实例方法

```java
// 修饰方法，锁的是this对象
public synchronized void m(){
    // 业务
}
```
反编译可以看到：javap -v /.../ByteCode.class
```class
public synchronized void m();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_SYNCHRONIZED
    Code:
      ...
```
    
- 修饰静态方法

```java
// 修饰静态方法，锁的是Class对象
public static synchronized void m(){
    // 业务
}
```
反编译可以看到：javap -v /.../ByteCode.class
```class
public static synchronized void m();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_STATIC, ACC_SYNCHRONIZED
    Code:
        ...
```

- 方法的同步并没有显示的通过指令 monitorenter 和 monitorexit 来完成，不过相对于普通方法，其常量池中多了`ACC_SYNCHRONIZED`标示符。JVM就是根据该标示符来实现方法的同步的
- 当方法调用时，调用指令将会检查方法的ACC_SYNCHRONIZED访问标志是否被设置，如果设置了，执行线程将先获取调用对象关联的monitor，获取成功之后才能执行方法体，方法执行完后再释放调用对象关联的monitor。在方法执行期间，其他任何线程都无法再获得同一个monitor对象。

### 修饰代码块

```java
// 可以指定锁哪个对象，也可以是class对象
synchronized (this) {
    // 业务
}
```
反编译可以看到：javap -v /.../ByteCode.class
```class
    ...
     2: astore_1
     3: monitorenter
     4: aload_1
     5: monitorexit
     6: goto          14
     9: astore_2
    10: aload_1
    11: monitorexit
    12: aload_2
    ...
```


---
两种同步方式本质上没有区别，只是方法的同步是一种隐式的方式来实现，无需通过字节码来完成。两个指令的执行是JVM通过调用操作系统的互斥原语mutex来实现，被阻塞的线程会被挂起、等待重新调度，会导致“用户态和内核态”两个态之间来回切换，对性能有较大影响。

## synchronized底层实现

### 概述

synchronized的底层实现是完全依赖与JVM虚拟机的，是通过一个monitor的对象来完成，其实wait/notify等方法也依赖于monitor对象，这就是为什么只有在同步的块或者方法中才能调用wait/notify等方法，否则会抛出java.lang.IllegalMonitorStateException的异常的原因。

在JVM虚拟机中，对象在内存中的存储布局，可以分为三个区域:
- 对象头(Header)
- 实例数据(Instance Data)
- 对齐填充(Padding)

| 长度 | 内容 | 说明  |
| -------- | -------- | -------- |
|8bit(64位)；4bit(32位) | Mark Word | 存储对象自身的运行时数据 |
|8bit(64位)；4bit(32位/64位开启指针压缩) | Class Pointer | 指向它的类元数据的指针 |
|4bit(64位/32位) | Array Length | 数组类型的对象包含该数据，表示数组的长度 |

Java对象头主要包括的数据：
- 标记字段(Mark Word)：用于存储对象自身的运行时数据，如哈希码（HashCode）、GC分代年龄、锁状态标志、线程持有的锁、偏向线程 ID、偏向时间戳等等，它是实现轻量级锁和偏向锁的关键。
- 类型指针(Class Pointer)：对象指向它的类元数据的指针，虚拟机通过这个指针来确定这个对象是哪个类的实例;
- 数组长度(Length)：数组类型的对象包含该数据，表示数组的长度

很明显synchronized使用的锁对象是存储在Java对象头里Mark Word标记字段里的。

### Mark Word数据结构

Mark Word字段在64位机器上占8个字节，在32位机器上占4个字节
这里可以为大家证实下
以下代码
```java
package com.example.demo;
import org.openjdk.jol.info.ClassLayout;
public class ByteCode {
    public static void main(String[] args) {
        Object o = new Object();
        System.out.println(ClassLayout.parseInstance(o).toPrintable());
    }
}
```
在32位机器上执行得到的结果，可以看到header部分总共占用8个字节，这里就不需要指针压缩，所以mark word占用4个字节，class pointer占用4个字节
```text
java.lang.Object object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           09 00 00 00 (00001001 00000000 00000000 00000000) (9)
      4     4        (object header)                           88 41 13 e4 (10001000 01000001 00010011 11100100) (-468500088)
Instance size: 8 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
```
同样的代码在64位机器上执行，header占用12个字节，其中mark word占用8个字节，在指针压缩开启的情况下class pointer占用4个字节，不开启占用8个字节。最后4个字节是补齐8的倍数字节数
```text
java.lang.Object object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4        (object header)                           e5 01 00 f8 (11100101 00000001 00000000 11111000) (-134217243)
     12     4        (loss due to the next object alignment)
Instance size: 16 bytes
Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
```

在64位的虚拟机下，我们看看synchronized如何利用class对象的header实现加锁的

<table>
<tr>
  <th rowspan="2"  align="center">锁状态</th>
  <th align="center">25bit</th>
  <th align="center">31bit</th>
  <th align="center">1bit</th>
  <th align="center">4bit</th>
  <th align="center">1bit</th>
  <th align="center">2bit</th>
</tr>
<tr>
  <td></td>
  <td></td>
  <td>cms_free</td>
  <td>分代年龄</td>
  <td>偏向锁</td>
  <td>锁标志</td>
</tr>
<tr>
  <td>无锁</th>
  <td>unused</td>
  <td>hashCode</td>
  <td></td>
  <td></td>
  <td>0</td>
  <td>01</td>
</tr>
<tr>
  <td>偏向锁</th>
  <td colspan="2" >Thread ID(54bit) Epoch(2bit)</td>
  <td></td>
  <td></td>
  <td>1</td>
  <td>01</td>
</tr>
</table>

对象头的最后两位存储了锁的标志位，01是初始状态(未加锁)，其对象头里存储的是对象本身的哈希码，随着锁级别的不同，对象头里会存储不同的内容。偏向锁存储的是当前占用此对象的线程ID

HotSpot虚拟机对象头Mark Word
| 存储内容     | 标识位     | 状态     |
| -------- | -------- | -------- |
|对象hashcode、对象分代年龄 | 01 | 无锁 |
|指向锁记录的指针 |  00 | 轻量级锁 |
|指向重量级锁的指针 |  10 | 膨胀(重量级锁) |
|空 |  11 | GC标记 |
|偏向线程ID、偏向时间戳、对象分代年龄 |  01 | 可偏向 |

而在32位虚拟机下，考虑到虚拟机的空间效率，Mark Word被设计成一个非固定的数据结构以便在极小的空间内存存储尽量多的数据，它会根据对象的状态复用自己的存储空间，其结构如下：

<table>
<tr>
  <th rowspan="2"  align="center">锁状态</th>
  <th colspan="2" align="center">25bit</th>
  <th align="center">4bit</th>
  <th align="center">1bit</th>
  <th align="center">2bit</th>
</tr>
<tr>
  <td align="center">23bit</td>
  <td align="center">2bit</td>
  <td align="center"></td>
  <td align="center">是否偏向</td>
  <td align="center">锁标志</td>
</tr>
<tr>
  <td align="center">轻量级锁</td>
  <td colspan="4" align="center">指向栈中锁记录的指针</td>
  <td align="center">00</td>
</tr>
<tr>
  <td align="center">重量级锁</td>
  <td colspan="4" align="center">指向重量级锁的指针</td>
  <td align="center">10</td>
</tr>
<tr>
  <td align="center">GC标记</td>
  <td colspan="4" align="center">空</td>
  <td align="center">11</td>
</tr>
<tr>
  <td align="center">偏向锁</td>
  <td align="center">线程ID</td>
  <td align="center">Epoch</td>
  <td align="center">对象分代年龄</td>
  <td align="center">1</td>
  <td align="center">10</td>
</tr>
</table>


## monitor原理

### 概述

Monitor被翻译为`监视器`或`管程`

Java虚拟机中的同步是通过monitor进入和退出实现的，可以显式地(通过使用monitorenter和monitorexit指令)，也可以隐式地(通过方法调用和返回指令，即同步方法)。

同步方法通常不会使用monitorenter和monitorexit实现。它只是通过ACC_SYNCHRONIZED标志在运行时常量池中进行区分，该标志由方法调用指令检查。

```text
Tips：
monitor与加锁对象的关联可以通过各种超出本规范范围的方式进行管理。
例如
监视器可以与对象同时分配和释放；也可以在线程试图获得对象的独占访问权时动态分配它，并在稍后的某个时间释放该对象的监视器中没有线程的时候
```
如下程序摘自官方文档：
```java
void onlyMe(Foo f) {
    synchronized(f) {
        doSomething();
    }
}
```
反编译得到如下的部分内容
```class
Method void onlyMe(Foo)
0   aload_1             // Push f
1   dup                 // Duplicate it on the stack
2   astore_2            // Store duplicate in local variable 2
3   monitorenter        // Enter the monitor associated with f
4   aload_0             // Holding the monitor, pass this and...
5   invokevirtual #5    // ...call Example.doSomething()V
8   aload_2             // Push local variable 2 (f)
9   monitorexit         // Exit the monitor associated with f
10  goto 18             // Complete the method normally
13  astore_3            // In case of any throw, end up here
14  aload_2             // Push local variable 2 (f)
15  monitorexit         // Be sure to exit the monitor!
16  aload_3             // Push thrown value...
17  athrow              // ...and rethrow value to the invoker
18  return              // Return in the normal case
Exception table:
From    To      Target      Type
4       10      13          any
13      16      13          any
```

当一个线程访问同步代码块时，首先是需要得到锁才能执行同步代码，当退出或者抛出异常时必须要释放锁。如果使用synchronized给对象上锁（重量级）之后，该对象头的Mark Word中就被设置指向Monitor对象的指针。所有的Java对象是天生的Monitor。


### Monitor对象

在Java虚拟机（HotSpot）中，Monitor是由ObjectMonitor实现的，其主要数据结构如下（位于HotSpot虚拟机源码ObjectMonitor.hpp文件，C++实现的）
```c++
ObjectMonitor() {
    _header       = NULL;
    _count        = 0; // 记录个数
    _waiters      = 0,
    _recursions   = 0;
    _object       = NULL;
    _owner        = NULL;
    _WaitSet      = NULL; // 处于wait状态的线程，会被加入到_WaitSet
    _WaitSetLock  = 0 ;
    _Responsible  = NULL ;
    _succ         = NULL ;
    _cxq          = NULL ;
    FreeNext      = NULL ;
    _EntryList    = NULL ; // 处于等待锁block状态的线程，会被加入到该列表
    _SpinFreq     = 0 ;
    _SpinClock    = 0 ;
    OwnerIsThread = 0 ;
  }
```

ObjectMonitor中有两个队列，_WaitSet 和 _EntryList，用来保存ObjectWaiter对象列表（ 每个等待锁的线程都会被封装成ObjectWaiter对象 ），_owner指向持有ObjectMonitor对象的线程

![](./images/monitor%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84.png)

Monitor对象存在于每个Java对象的对象头Mark Word中（存储的指针的指向），Synchronized锁便是通过这种方式获取锁的，也是为什么Java中任意对象可以作为锁的原因，同时notify/notifyAll/wait等方法会使用到Monitor锁对象，所以必须在同步代码块中使用。

![](./images/monitor%E5%AF%B9%E8%B1%A1%E5%A4%84%E7%90%86%E6%B5%81%E7%A8%8B.png)
线程从获取锁到结束的整个流程如下：
1. 当多个线程同时访问一段同步代码时，首先会通过1号门进入Entry Set集合，如果在入口区没有线程等待，那么当线程直接获取到对象的monitor后进入Owner区域并把monitor中的owner变量设置为当前线程同时monitor中的计数器count加1。
2. 若线程调用wait()方法，将释放当前持有的monitor，owner变量恢复为null，count自减1，同时该线程通过3号门进入Wait Set集合中等待被唤醒。直到相应的条件满足后再通过4号门进入重新获取监视器再执行。
4. 若当前线程执行完毕也将释放monitor(锁)并复位变量的值，通过5号门退出监视器

### monitorenter

每个对象都关联一个监视器锁（monitor），只要监视器锁对象中的owner有值，他就被锁定。执行monitorenter的线程尝试获得与锁定对象相关的监视器的所有权

1. 如果与加锁对象关联的monitor的条目计数为0，则线程进入monitor并将其条目计数设置为1，线程就是monitor的`所有者(owner)`
2. 如果线程已经拥有与加锁对象关联的monitor，那么它将重新进入monitor，增加其条目计数。
3. 如果另一个线程已经拥有与加锁对象关联的monitor，那么该线程将阻塞，直到monitor的条目计数为零，然后再次尝试获得所有权。
4. 如果给定的加锁对象为null，monitorenter会抛出一个NullPointerException。

### monitorexit

一个或多个monitorexit指令可以与一个monitorenter指令一起使用

1. 执行monitorexit的线程必须是与加锁对象相关联的monitor的owner。
2. 线程减少与加锁对象关联的监视器的条目计数。如果结果是条目计数的值为零，则线程退出monitor，不再是它的owner。其他正在阻塞以进入monitor的线程可以尝试进入monitor
3. 当同步语句中抛出异常时，使用Java虚拟机的异常处理机制从执行同步语句之前输入的监视器中退出

> 参考官方文档
> https://docs.oracle.com/javase/specs/jvms/se16/html/jvms-6.html#jvms-6.5.monitorenter
> https://docs.oracle.com/javase/specs/jvms/se16/html/jvms-3.html#jvms-3.14

---

- 多个线程访问同步代码块时，相当于去争抢对象监视器修改对象中的锁标识，ObjectMonitor这个对象和线程争抢锁的逻辑有密切的关系。
- Java中的每个对象都派生自Object类，而每个Java Object在JVM内部都有一个native的C++对象进行对应。线程在获取锁的时候，实际上就是获得一个监视器对象(monitor) ，monitor可以认为是一个同步对象，所有的Java对象是天生携带monitor。
- Synchronized的happens-before规则，即监视器锁规则：对同一个监视器的解锁，happens-before于对该监视器的加锁

## 锁升级、锁优化

锁主要存在四种状态：无锁状态、偏向锁状态、轻量级锁状态、重量级锁状态。锁可以从偏向锁升级到轻量级锁，再升级的重量级锁。但是锁的升级是单向的，不会出现锁的降级。

锁膨胀方向：无锁 → 偏向锁 → 轻量级锁 → 重量级锁

在JDK 1.6中默认是开启偏向锁和轻量级锁的，可以通过-XX:-UseBiasedLocking来禁用偏向锁。

### 偏向锁

在大多数情况下，锁不仅不存在多线程竞争，而且总是由同一线程多次获得，为了让线程获得锁的代价更低，引进了偏向锁，偏向锁是JDK6中的重要引进。

引入偏向锁主要目的是：偏向锁是在单线程执行代码块时使用的机制，为了在没有多线程竞争的情况下尽量减少不必要的轻量级锁执行路径。如果在多线程并发的环境下，则很可能会转化为轻量级锁或者重量级锁，因为轻量级锁的加锁解锁操作是需要依赖多次CAS原子指令的，而偏向锁只需要在置换ThreadID的时候依赖一次CAS原子指令（一旦出现多线程竞争的情况就必须撤销偏向锁，所以偏向锁的撤销操作的性能损耗也必须小于节省下来的CAS原子指令的性能消耗）。

现在几乎所有的锁都是可重入的，即已经获得锁的线程可以多次锁住/解锁监视对象，按照之前的HotSpot设计，每次加锁/解锁都会涉及到一些CAS操作（比如对等待队列的CAS操作），CAS操作会延迟本地调用，因此偏向锁的想法是一旦线程第一次获得了监视对象，之后让监视对象“偏向”这个线程，之后的多次调用则可以避免CAS操作，说白了就是在对象的对象头中的Mark Word中用1个字节来表示是否偏向，如果发现为偏向则无需再走各种加锁/解锁流程，省去了很多加锁解锁流程。具体流程如下：
1. 检测Mark Word是否为可偏向状态，即是否为偏向锁1，锁标识位为01；
2. 若为可偏向状态，则测试线程ID是否为当前线程ID，如果是，则执行步骤（5），否则执行步骤（3）；
3. 如果测试线程ID不为当前线程ID，则通过CAS操作竞争锁，竞争成功，则将Mark Word的线程ID替换为当前线程ID，否则执行线程（4）；
4. 通过CAS竞争锁失败，证明当前存在多线程竞争情况，当到达全局安全点，获得偏向锁的线程被挂起，偏向锁升级为轻量级锁，然后被阻塞在安全点的线程继续往下执行同步代码块；
5. 执行同步代码块；

偏向锁的释放采用了一种只有竞争才会释放锁的机制，线程是不会主动去释放偏向锁，需要等待其他线程来竞争。偏向锁的撤销需要等待全局安全点（这个时间点是上没有正在执行的代码）。其步骤如下：

1. 暂停拥有偏向锁的线程；
2. 判断锁对象是否还处于被锁定状态，
3. 否，则恢复到无锁状态（01），以允许其余线程竞争。
4. 是，则挂起持有锁的当前线程，并将指向当前线程的锁记录地址的指针放入对象头Mark Word，升级为轻量级锁状态（00），然后恢复持有锁的当前线程，进入轻量级锁的竞争模式；

如果并发数较大，同时同步代码块执行时间较长，则被多个线程同时访问的概率就很大，就可以使用参数-XX:-UseBiasedLocking来禁止偏向锁(但这是个JVM参数，不能针对某个对象锁来单独设置)。

#### 优缺点

- 优点

加锁、解锁和非同步方法相比相差甚小

- 缺点

如果线程间存在锁竞争，会带来额外的锁撤销消耗

- 适用场景

只有一个线程访问同步方法的场景


### 轻量级锁

引入轻量级锁的主要目的是 在没有多线程竞争的前提下，减少传统的重量级锁使用操作系统互斥量产生的性能消耗。当关闭偏向锁功能或者多个线程竞争偏向锁导致偏向锁升级为轻量级锁，则会尝试获取轻量级锁
1. 在线程进入同步块时，如果同步对象锁状态为无锁状态（锁标志位为“01”状态，是否为偏向锁为“0”），虚拟机首先将在当前线程的栈帧中建立一个名为锁记录(Lock Record)的空间，用于存储锁对象目前的Mark Word的拷贝，官方称之为Displaced Mark Word。
2. 拷贝对象头中的Mark Word复制到锁记录（Lock Record）中；
3. 拷贝成功后，虚拟机将使用CAS操作尝试将对象Mark Word中的Lock Word更新为指向当前线程Lock Record的指针，并将Lock record里的owner指针指向object mark word。如果更新成功，则执行步骤（4），否则执行步骤（5）；
4. 如果这个更新动作成功了，那么当前线程就拥有了该对象的锁，并且对象Mark Word的锁标志位设置为“00”，即表示此对象处于轻量级锁定状态，
5. 如果这个更新操作失败了，虚拟机首先会检查对象Mark Word中的Lock Word是否指向当前线程的栈帧，如果是，就说明当前线程已经拥有了这个对象的锁，那就可以直接进入同步块继续执行。否则说明多个线程竞争锁，进入自旋执行（3），若自旋结束时仍未获得锁，轻量级锁就要膨胀为重量级锁，锁标志的状态值变为“10”，Mark Word中存储的就是指向重量级锁（Monitor）的指针，当前线程以及后面等待锁的线程也要进入阻塞状态。
6. 轻量级解锁时，会使用原子的CAS操作将Displaced Mark Word替换回到对象头中，如果成功，则表示没有发生竞争关系。如果失败，表示当前锁存在竞争关系。锁就会膨胀成重量级锁。两个线程同时争夺锁，导致锁膨胀。

#### 自旋锁

线程的阻塞和唤醒需要CPU从用户态转为核心态，频繁的阻塞和唤醒对CPU来说是一件负担很重的工作，势必会给系统的并发性能带来很大的压力。为此有了自旋锁；所谓自旋锁，就是指当一个线程尝试获取某个锁时，如果该锁已被其他线程占用，就一直循环检测锁是否被释放，而不是进入线程挂起或睡眠状态。

自旋锁适用于锁保护的临界区很小的情况，锁占用的时间就很短，线程数量少的情况下。自旋等待不能替代阻塞，虽然它可以避免线程切换带来的开销，但是它占用了CPU处理器的时间。如果持有锁的线程很快就释放了锁，那么自旋的效率就非常好，反之，自旋的线程就会白白消耗掉处理的资源，它不会做任何有意义的工作，典型的占着茅坑不拉屎，这样反而会带来性能上的浪费。所以自旋等待的时间（自旋的次数）必须要有一个限度，如果自旋超过了定义的时间仍然没有获取到锁，则应该被挂起。在JDK1.6中默认开启。同时自旋的默认次数为10次，可以通过参数-XX:PreBlockSpin来调整。

固定值的设定往往让程序不灵活，假如系统很多线程都是等你刚刚退出的时候就释放了锁（假如多自旋一两次就可以获取锁），所以JDK1.6引入自适应的自旋锁让虚拟机会变得越来越聪明。

#### 自适应自旋锁

它是由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定。

线程如果自旋成功了，那么下次自旋的次数会更加多，因为虚拟机认为既然上次成功了，那么此次自旋也很有可能会再次成功，那么它就会允许自旋等待持续的次数更多。反之，如果对于某个锁，很少有自旋能够成功，那么在以后要或者这个锁的时候自旋的次数会减少甚至省略掉自旋过程，以免浪费处理器资源。

#### 优缺点

- 优点

竞争的线程不会阻塞，提高了程序的响应性能

- 缺点

如果始终得不到锁，竞争的线程使用自旋会占用大量CPU资源

- 使用场景

追求响应时间，线程数量少，同步块执行速度快


### 重量级锁

重量级锁也就是原始的Synchronized的实现，它的特点是其他线程试图获取锁时，都会被阻塞，只有持有锁的线程释放锁之后才会唤醒这些线程。

Synchronized是通过对象内部的一个叫做监视器锁（Monitor）来实现的。但是监视器锁本质又是依赖于底层的操作系统的Mutex Lock来实现的。而操作系统实现线程之间的切换这就需要从用户态转换到核心态，这个成本非常高，状态之间的转换需要相对比较长的时间，因此，这种依赖于操作系统Mutex Lock所实现的锁我们称之为“重量级锁”。


#### 优缺点

- 优点

线程的竞争不需要自旋，不会消耗CPU资源

- 缺点

线程阻塞，响应速度缓慢

- 应用场景

同步块执行时间较长，追求吞吐量



### 锁消除

为了保证数据的完整性，在进行操作时需要对这部分操作进行同步控制，但是在有些情况下，JVM检测到不可能存在共享数据竞争，这是JVM会对这些同步锁进行锁消除。

锁消除的依据是逃逸分析的数据支持

锁消除可以节省毫无意义的请求锁的时间。变量是否逃逸，对于虚拟机来说需要使用数据流分析来确定。但在使用一些JDK的内置API时，如StringBuffer、Vector、HashTable等，这个时候会存在隐形的加锁操作。
```java
public void stringBufferTest(){
    StringBuffer buffer = new StringBuffer();
    for(int i = 0 ; i < 10 ; i++){
        buffer.append(i);
    }
    System.out.println(buffer.toString());
}
```
在运行这段代码时，JVM可以明显检测到变量buffer没有逃逸出方法stringBufferTest()之外，所以JVM可以大胆地将buffer内部的加锁操作消除。

### 锁粗化

在使用同步锁的时候，需要让同步块的作用范围尽可能小，仅在共享数据的实际作用域中才进行同步，这样做的目的是为了使需要同步的操作数量尽可能缩小，如果存在锁竞争，那么等待锁的线程也能尽快拿到锁。但是如果一系列的连续加锁解锁操作，可能会导致不必要的性能损耗。

锁粗化就是将多个连续的加锁、解锁操作连接在一起，扩展成一个范围更大的锁

## 关于Lock Record

Lock Record用于偏向锁优化和轻量级锁优化。

Lock Record在获取轻量级锁时才会有用，Lock Record是线程私有的(TLS)，也就是每个线程有自己的一份锁记录，在创建完锁记录的空间后，会将当前对象的Mark Word拷贝到锁记录中(Displaced Mark Word)。因此当前对象的Mark Word的结构会有所变化，不再是存着hashcode等信息，这些信息将会被拷贝到Lock Record中，与此同时Mark Word中将会保存一个指向Lock Record的指针，指向锁记录。Lock Record保存指向对象的Mark Word地址

Lock Record的创建时机是当字节码解释器执行monitorenter字节码轻度锁住一个对象时，就会在获取锁的线程的栈上显式或者隐式分配一个Lock Record。Lock Record在线程的Interpretered Frame上(解释帧)分配

以下为轻量级锁的Lock Record结构

![](./images/%E8%BD%BB%E9%87%8F%E7%BA%A7%E9%94%81Lock%20Record.png)

以下为重量级锁的Lock Record结构
![](./images/%E9%87%8D%E9%87%8F%E7%BA%A7%E9%94%81Lock%20Record.png)

## 总结加锁流程

![](./images/Synchronized%E5%81%8F%E5%90%91%E9%94%81%E3%80%81%E8%BD%BB%E9%87%8F%E7%BA%A7%E9%94%81%E5%8F%8A%E9%87%8D%E9%87%8F%E7%BA%A7%E9%94%81%E8%BD%AC%E6%8D%A2%E6%B5%81%E7%A8%8B.png)

## 注意事项

- synchronized一般不会用于锁基本类型和String类型
- synchronized关键字不能继承，虽然可以使用synchronized来定义方法，但synchronized并不属于方法定义的一部分
- 锁对象不能为空，因为锁的信息都保存在对象头里
- synchronized是非公平的

> https://zhuanlan.zhihu.com/p/262211521
> https://www.jianshu.com/p/e62fa839aa41
> https://blog.csdn.net/weixin_40757930/article/details/123848536
> https://blog.csdn.net/slslslyxz/article/details/106363990


