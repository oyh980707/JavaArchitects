# 关于Object o = new Object()

你们知道对象创建后在堆中长什么样子吗？

当我们需要访问一个对象的时候，是如何定位到对象的呢？

## 对象在内存中的存储分布

![](./images/%E5%AF%B9%E8%B1%A1%E5%9C%A8%E5%86%85%E5%AD%98%E4%B8%AD%E7%9A%84%E5%AD%98%E5%82%A8%E7%BB%93%E6%9E%84.jpg)

对象内存中可以分为三块区域：对象头(Header)，实例数据(Instance Data)和对齐填充(Padding)
- 对象头
    - Mark Word
        
        记录锁信息、HashCode、GC分代年龄等等占用8个字节
        
    - Class Pointer
        
        指向所属类的元信息地址，默认开启指针压缩占4个字节，不压缩占8字节
        
    - Length
        
        如果不是数组对象，则该项没有，如果是数组对象，此处表示数组的长度len
        
- 实例数据
    - 基本数据类型
        
        boolean/byte/char/int/long 分别占用1/1/2/4/8字节
        
    - 引用类型
        
        开启指针压缩占用4个字节，未开启占8个字节，指向真是对象的地址
        
- 对齐填充

    不管是内补齐还是外补齐，最终的字节数必须是8的倍数

## Object o=new Object()占用字节

前提：64位机器，开启了压缩指针(-XX:+UseCompressedOops)

分析：对象头占用8+4字节，实例数据无 占用0字节，对齐填充4字节

```java
Object obj = new Object();
System.out.println(ClassLayout.parseInstance(obj).toPrintable());
```
输出结果分析：
```text
java.lang.Object object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4        (object header)                           e5 01 00 f8 (11100101 00000001 00000000 11111000) (-134217243)
     12     4        (loss due to the next object alignment)
Instance size: 16 bytes
Space losses: 0 bytes internal + 4 bytes external = 4 bytes total

// (object header) 描述的是对象头，前8个字节表示Mark Word，后面4个字节表示指向类的元信息地址，占用4个字节
// 由于Object没有属性，所以实例数据无，占0个字节
// 由于目前12个字节，需要补齐成8的倍数，所以补齐填充占用4个字节
// 总共占用对内存的16个字节
```

如果关闭压缩指针(-XX:-UseCompressedOops)，还是当前例子得到的结果如下：
```text
java.lang.Object object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4        (object header)                           00 1c 6e 09 (00000000 00011100 01101110 00001001) (158211072)
     12     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
Instance size: 16 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
// Class Pointer 占用8个字节 + Mark Word 8个字节，总计16字节，所以无需填充补齐
```

## int[] arr = new int[8] 在堆内存占用字节

前提：64位机器，开启了压缩指针(-XX:+UseCompressedOops)

分析：对象头占用8+4+4(数组长度)字节，实例数据占用4*8=32字节，对齐填充0字节
```java
int[] arr = new int[8];
System.out.println(ClassLayout.parseInstance(arr).toPrintable());
```
结果输出：
```text
[I object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4        (object header)                           6d 01 00 f8 (01101101 00000001 00000000 11111000) (-134217363)
     12     4        (object header)                           08 00 00 00 (00001000 00000000 00000000 00000000) (8)
     16    32    int [I.<elements>                             N/A
Instance size: 48 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
// 首先他是数组对象，Mark Word 8字节，Class Pointer 4字节
// 注意 还有4个字节的对象头，他是表示数组的长度 长度表示8，结果一致
// 剩下的32个字节表示实例数据，8个int长度，4*8=32个字节
// So 总计所占内存为48字节
```

## 对象的访问

目前最主流的访问对象方式有两种：句柄访问和直接指针访问

- 句柄访问
    Java虚拟机会在堆内划分出一块内存来存储句柄池，对象引用当中存储的就是句柄地址，然后句柄池中的句柄才会存储对象实例数据和对象类型数据地址
    
    优点：垃圾回收时对象时，refrence引用指针不变，如果是多个reference引用指向同于个对象，此时就体现出它的好处，只需要改句柄中实例数据地址，而无需改引用地址
    
    缺点：定位速度慢
![](./images/%E9%80%9A%E8%BF%87%E5%8F%A5%E6%9F%84%E8%AE%BF%E9%97%AE%E5%AF%B9%E8%B1%A1.png)
    
- 直接指针访问(Hot Spot虚拟机采用的方式)
    对象引用中就会直接存储对象类型数据
    
    优点：定位速度快，节省一次指针定位的开销
    
    缺点：对象回收时，指针都需要重新定位，开销大
![](./images/%E9%80%9A%E8%BF%87%E7%9B%B4%E6%8E%A5%E6%8C%87%E9%92%88%E8%AE%BF%E9%97%AE%E5%AF%B9%E8%B1%A1.png)

---

以下实验都是基于64位机器，开启指针压缩(-XX:+UseCompressedOops)

引入ClassLayou的依赖
```xml
<dependency>
    <groupId>org.openjdk.jol</groupId>
    <artifactId>jol-core</artifactId>
    <version>0.10</version>
</dependency>
```

实例数据的占用内存演示
```java
public class MainDemo {
    private char c;
    private byte by;
    private int i;
    private boolean b;
    private long l;
    private Object o;

    public static void main(String[] args) {
        MainDemo o = new MainDemo();
        System.out.println(ClassLayout.parseInstance(o).toPrintable());
    }
}
```
结果
```text
com.example.demo.MainDemo object internals:
 OFFSET  SIZE               TYPE DESCRIPTION                               VALUE
      0     4                    (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
      4     4                    (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4                    (object header)                           05 c1 00 f8 (00000101 11000001 00000000 11111000) (-134168315)
     12     4                int MainDemo.i                                0
     16     8               long MainDemo.l                                0
     24     2               char MainDemo.c                                
     26     1               byte MainDemo.by                               0
     27     1            boolean MainDemo.b                                false
     28     4   java.lang.Object MainDemo.o                                null
Instance size: 32 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
```




