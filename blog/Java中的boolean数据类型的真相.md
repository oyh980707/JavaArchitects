# JVM眼中的boolean

boolean基础类型在虚拟机中到底长什么样？

我们知道JVM的计算依赖操作数栈，就好像CPU计算依赖寄存器一样。再JVM眼中，操作数栈就是它的"寄存器"。那么栈上保存的都是int类型。那么boolean在字节码中使用也会用到操作数栈，而操作数栈只能保存int类型，也就是说boolean基本类型也会用int类型来表示。

上代码：
```java
public class OOP{
    public static void main(String[] args){
        boolean flag = true;
        if(flag) System.out.println("Hello Java!");
        if(flag == true) System.out.println("Hello JVM!");
    }
}
```
通过javac命令编译后，通过java命令运行的到
> javac OOP.java
> java OOP
```text
Hello Java!
Hello JVM!
```

通过反编译来分析class文件

> javap -v OOP.class

```text
...
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=1
         0: iconst_1
         1: istore_1
         2: iload_1
         3: ifeq          14
         6: getstatic     #2     // Field java/lang/System.out:Ljava/io/PrintStream;
         9: ldc           #3     // String Hello Java!
        11: invokevirtual #4     // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        14: iload_1
        15: iconst_1
        16: if_icmpne     27
        19: getstatic     #2    // Field java/lang/System.out:Ljava/io/PrintStream;
        22: ldc           #5    // String Hello JVM!
        24: invokevirtual #4    // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        27: return
...
```

分析以上字节码可以得出

如下代码
```java
boolean flag = true;
```
对应于字节码
```text
0: iconst_1 // 数字1入操作数栈
1: istore_1 // 存储操作栈上刚入栈的1
```
这其实就表示boolean基本数据类型在JVM看来其实就是保存的int类型数字1，意为true

继续分析如下代码
```java
if(flag) System.out.println("Hello Java!");
```

对应的字节码如下
```text
2: iload_1          // 从局部变量表1位置取出刚保存的int类型1，放入操作数栈
3: ifeq          14 // ifeq 指令执行后，从操作数栈弹出一个值即1，判断是否等于0
                    // 如果等于0，则跳转到14位置，如果不等于零，就继续执行下面指令
6: getstatic     #2 
9: ldc           #3
11: invokevirtual #4 // 这三不调用println输出 Hello Java!
```

分析最后一段代码

```java
if(flag == true) System.out.println("Hello JVM!");
```

对应的字节码如下
```text
14: iload_1             // 加载局部变量表下标1位置的值放入到操作数栈
                        // 我们知道 istore_0 = this，但在这里的static方法可不是
                        // 这里0号位置存储的是arg入参
15: iconst_1            // 将1加载到操作数栈，此时操作数栈有两个1
16: if_icmpne     27    // 从操作数栈取出两个参数即两个1,判断是否相等。即判断是否为true
                        // 如果不相等则跳转27位置return
                        // 否则执行println函数输出 Hello JVM!
19: getstatic     #2    // Field java/lang/System.out:Ljava/io/PrintStream;
22: ldc           #5    // String Hello JVM!
24: invokevirtual #4
27: return
```

有了以上的结论：boolean在JVM层面就是使用int类型存储的。

那么有趣的事情来了，我们手动修改一下字节码，就是将boolean的值修改为存储2试试看。我这里使用IDEA的插件修改的，注意使用javac编译的时候加上`-g`选项生成所有调试信息。如果不使用`-g`选项编译，将会忽略掉一些调试信息如LocalVariableTable等。通过javac编译默认丢掉调试信息是为了效率。
```text
...
public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=1
         0: iconst_2                    /////======注意着已经修改为iconst_2
         1: istore_1
         2: iload_1
         3: ifeq          14
         6: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         9: ldc           #3                  // String Hello Java!
        11: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        14: iload_1
        15: iconst_1
        16: if_icmpne     27
        19: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
        22: ldc           #5                  // String Hello JVM!
        24: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        27: return
      LineNumberTable:
        line 3: 0
        line 4: 2
        line 5: 14
        line 6: 27
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      28     0  args   [Ljava/lang/String;
            2      26     1  flag   Z
...
```

我们通过IDEA查看class文件
```java
public class OOP {
    public OOP() {
    }
    public static void main(String[] args) {
        boolean flag = 2;
        if (flag != 0) {
            System.out.println("Hello Java!");
        }
        if (flag == 1) {
            System.out.println("Hello JVM!");
        }
    }
}
```
可以看到boolean值存储的2，我们看看运行结果
```text
Hello Java!
```
可见在JVM看来boolean就是int类型，1为true，0为false。同时在这也得出了一个结论，非0即真的结论，和C/C++的保持一致。

我们再来看看使用Unsafe操作boolean类型
```java
public class ByteCode2 {
    boolean flag = false;
    public boolean getFlag() {
        return this.flag;
    }
    public static void main(String[] args) throws Exception {
        ByteCode2 b = new ByteCode2();
        Field field = ByteCode2.class.getDeclaredField("flag");
        Unsafe unsafe = b.getUnsafe();
        long flagOffset = unsafe.objectFieldOffset(field);

        System.out.println("--------------unsafe.putByte(2)-------------------");

        unsafe.putByte(b, flagOffset, (byte)2);

        System.out.println("Unsafe.getByte: " + unsafe.getByte(b, flagOffset)); // 总是会打印出put的值
        System.out.println("Unsafe.getBoolean: " + unsafe.getBoolean(b, flagOffset)); // 打印出的值，像是ifeq, flag != 0即true

        // ifeq指令(flag != 0)即true
        if (b.flag) {
            System.out.println("b.flag");
        }

        // if_cmpne指令做整数比较(1 == flag)即为false
        if (true == b.flag) {
            System.out.println("true == b.flag");
        }

        // ifeq((flag) & 1 != 0) 为false，为何？因为getFlag做了掩码
        if (b.getFlag()) {
            System.out.println("b.getFlag()");
        }
        // if_cmpne指令做整数比较，getFlag方法会对 boolean内容进行掩码操作，1 == (flag) & 1，则为false
        if (true == b.getFlag()) {
            System.out.println("true == b.getFlag()");
        }

        System.out.println("--------------unsafe.putByte(3)-------------------");

        unsafe.putByte(b, flagOffset, (byte)3);

        System.out.println("Unsafe.getByte: " + unsafe.getByte(b, flagOffset)); // 总是会打印出put的值
        System.out.println("Unsafe.getBoolean: " + unsafe.getBoolean(b, flagOffset)); // 打印出的值，像是ifeq, flag != 0即true

        // ifeq指令(flag != 0)即true，此时flag=3
        if (b.flag) {
            System.out.println("b.flag");
        }

        // if_cmpne指令做整数比较(1 == flag)即为false，此时flag=3
        if (true == b.flag) {
            System.out.println("true == b.flag");
        }

        // ifeq((flag) & 1 != 0) 为true。此时(flag) & 1 = 1
        if (b.getFlag()) {
            System.out.println("b.getFlag()");
        }
        // if_cmpne指令做整数比较，getFlag方法会对boolean内容进行掩码操作，1 == (flag) & 1，则为true
        if (true == b.getFlag()) {
            System.out.println("true == b.getFlag()");
        }
    }

    private static Unsafe getUnsafe() throws Exception {
        Constructor<Unsafe> unsafeConstructor = Unsafe.class.getDeclaredConstructor();
        unsafeConstructor.setAccessible(true);
        Unsafe unsafe = unsafeConstructor.newInstance();
        return unsafe;
    }
}
```
输出结果
```text
--------------unsafe.putByte(2)-------------------
Unsafe.getByte: 2
Unsafe.getBoolean: true
b.flag
--------------unsafe.putByte(3)-------------------
Unsafe.getByte: 3
Unsafe.getBoolean: true
b.flag
b.getFlag()
true == b.getFlag()
```

根据结果得知，boolean基本类型存储的是int类型，有印证了之前的结论。方法返回boolean的时候，会做掩码，如上getFlag返回boolean基本类型，会做掩码(flag & 1)后返回。同时byte、short、char也类似，方法返回会做掩码操作。







