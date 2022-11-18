# Class文件

## 概述

我们都知道Java语言是跨平台的语言，依托的事Java虚拟机(JVM)实现。而Java虚拟机是运行字节码的，也就是加载并运行class字节码。总而言之Java虚拟机不和任何一个语言绑定，它只与"Class 文件"这种特定的二进制文件格式所关联。无论使用何种语言进行软件开发，只要能将源文件编译为正确的Class文件，那么这种语言就可以在Java虚拟机上执行。

那么想让一个程序在JVM上面运行，有以下步骤：
1. 源代码被编译为符合JVM规范的字节码(Class字节码)
2. 通过ClassLoader将类加载到内存，生成class对象
3. 初始化main方法所在的类，然后从main方法开始执行
4. 每次执行会从内存中取字节码指令，字节码不是机器指令，还需要通过解释器解释成机器认识的机器码指令才能执行，在Hotspot虚拟机中还需判断是否热点数据进行解释执行或者即时编译执行

![](./images/class%E5%AD%97%E8%8A%82%E7%A0%81%E8%A2%AB%E7%BC%96%E8%AF%91%E5%8A%A0%E8%BD%BD%E6%A6%82%E5%86%B5.png)

### class字节码的两种执行方式

首先我们要知道，通常javac将程序源代码编译，转换成java字节码（.java文件编译转换成.class文件，这也是我们通常意义上所理解的编译）。但是字节码不是机器语言，想要让机器执行，还需要通过JVM来解释字节码，将其翻译成对应的机器指令执行。

通过解释执行(传统的JVM解释器Interpreter)，它的执行效率必定会比可执行的二进制机器码指令程序慢很多。为了提高速度，引入了JIT技术，JIT会把JVM认为是“热点代码”（运行特别频繁的某个方法，或者代码块）的部分代码，翻译成目标机器相关的机器码并优化，然后缓存起来，以备下次使用。

class字节码有两种执行方式，即：解释执行（interpreter）、即时编译（Just-In-Time compilation，JIT），

JIT编译器（无论C1还是C2）与解释器配合工作的方式，这是默认的方式，通过“-Xmixed”参数设定。

#### 解释器Interpreter

程序启动时首先发挥作用，解释执行Class字节码。只有解释器工作，省去编译时间，加快启动速度，但执行效率较低，这个过程也叫前端编译。使用-Xint指定使用该模式执行。

#### JIT(即时编译、动态编译)

JIT是在java程序运行时，将最频繁执行的代码（hot spot）编译成本地代码，接下来这些hot spot code直接通过机器码执行，省去了重复解释。使用-Xcomp指定。-client使用C1编译器，-server使用C2编译器。HotSpot虚拟机实现了即时编译

![](./images/JIT%E6%89%A7%E8%A1%8C%E5%8E%9F%E7%90%86.png)

优点：
JIT保证了程序可移植性又提升了运行效率，对于大量的Java应用程序来说，动态编译已经弥补了与C++之类语言的静态本地编译性能之间的差距，在某些情况下，甚至超过了后者的性能。

缺点：
由于识别、编译hot spot code都需要一定的时间，所以应用程序通常要经历一个准备过程。其次，JIT在运行时也会带来一定的性能损失（因为在程序执行时进行编译，所以编译代码的时间将计入程序的执行时间。任何编写过大型C或C++程序的人都知道，编译过程往往较慢），与之相对应，C++是在运行前编译成本地代码，但这牺牲了移植性。

HotSpot虚拟机内置两个即时编译器，分别Client Compiler和Server Comiler

##### Client Compiler

简称C1编译器，较为轻量，只做少量性能开销比较高的优化，它占用内存较少，适合于桌面交互式应用。

它是一个简单快速的三段式编译器，主要关注点在于局部性的优化，而放弃了许多耗时较长的全局优化，在寄存器分配策略上，JDK6以后采用的为线性扫描寄存器分配算法，其他方面的优化，主要有方法内联、去虚拟化、冗余消除等。

##### Server Comiler

简称C2编译器，也叫Opto编译器，较为重量，采用了大量传统编译优化的技巧来进行优化，占用内存相对多一些，适合服务器端的应用。

会执行所有经典的优化动作，如无用代码消除、循环展开、循环表达式外提、消除公表达式、常量传播、基本块重排序等，还会一些与Java语言特性密切相关的优化技术，如范围检查消除、空值检查消除等，还进行一些不稳定的激进优化，如守护内联、分支频率预测等。

由于C2会收集程序运行信息，因此其优化范围更多在于全局优化，不仅仅是一个方块的优化，收集的信息主要有：分支的跳转/不跳转的频率、某条指令上出现过的类型、是否出现过空值、是否出现过异常等。

## 字节码

### 字节码文件内容

源代码经过编译器编译之后便会生成一个class字节码文件，是一种二进制文件，它的内容包含魔数、版本、常量池、访问标志、指令等等，该字节码只能被符合Java虚拟机规范的JVM来识别并加载，它不像C经由编译器直接生成机器码直接运行。

### 字节码指令

Java虚拟机的指令由一个字节长度的、代表着某种特定操作含义的操作码以及跟随其后的零至多个代表此操作所需参数的操作数所构成，该指令只能被符合Java虚拟机规范的JVM来识别并执行。

如下通过javap指令反编译得到的字节码指令：
```java
package com.example.demo;

public class ByteCode {
    
}
```
编译过后，javap反编译后得到如下：
```class
public class com.example.demo.ByteCode {
  public com.example.demo.ByteCode();
    Code:
       0: aload_0
       1: invokespecial #1 // Method java/lang/Object."<init>":()V
       4: return
}
// 对应的机器码
// .... 2ab7 0001 b1.. ...
// 0x2a 表示 aload_0 指令
// 0xb7 表示 invokespecial指令，0x0001 表示参数
// 0xb1 表示 return 指令
```
> 详细的指令集合参考Java虚拟机规范：The Java Virtual Machine Specification(https://docs.oracle.com/javase/specs/jvms/se8/jvms8.pdf)
> https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.10.1.9

对应的详细二进制如下：
```
cafe babe 0000 0034 0010 0a00 0300 0d07
000e 0700 0f01 0006 3c69 6e69 743e 0100
0328 2956 0100 0443 6f64 6501 000f 4c69
6e65 4e75 6d62 6572 5461 626c 6501 0012
4c6f 6361 6c56 6172 6961 626c 6554 6162
6c65 0100 0474 6869 7301 001b 4c63 6f6d
2f65 7861 6d70 6c65 2f64 656d 6f2f 4279
7465 436f 6465 3b01 000a 536f 7572 6365
4669 6c65 0100 0d42 7974 6543 6f64 652e
6a61 7661 0c00 0400 0501 0019 636f 6d2f
6578 616d 706c 652f 6465 6d6f 2f42 7974
6543 6f64 6501 0010 6a61 7661 2f6c 616e
672f 4f62 6a65 6374 0021 0002 0003 0000
0000 0001 0001 0004 0005 0001 0006 0000
002f 0001 0001 0000 0005 2ab7 0001 b100
0000 0200 0700 0000 0600 0100 0000 0300
0800 0000 0c00 0100 0000 0500 0900 0a00
0000 0100 0b00 0000 0200 0c
```

### 查看class字节码文件

- 通过十六进制查看class文件；用文本编辑器例如sublime、vsCode、notepad++等以16进制显示打开；
- 用javap命令反编译class文件
- 使用idea插件查看：BinEd查看class的二进制源文件；jclasslib查看class的文件结构

## class文件结构

每个Class文件都包含唯一一个类或接口的定义信息。通俗来讲将类或接口的任何有效表示称为类文件格式。Class文件由8位字节流组成，其中的数据项，无论是字节顺序还是数量，都是被严格限定的，哪个字节代表什么含义，长度是多少，先后顺序如何，都不允许改变。

Class 文件格式采用一种类似于C语言结构体的方式进行数据存储，这种结构中只有两种数据类型：无符号数和表
1. 无符号数属于基本的数据类型，以 u1、u2、u4、u8 来分别代表1个字节、2个字节、4个字节、8个字节的无符号数，无符号数可以用来描述数字、索引引用、数量值或者按照 UTF-8 编码构成字符串值
2. 表是由多个无符号数或者其他表作为数据项构成的复合数据类型，所有表都习惯性地以"_info"结尾。表用于描述有层次关系的复合结构的数据，整个Class文件本质上就是一张表。由于表没有固定长度，所以通常会在其前面加上个数说明

### Class 文件结构概述

Class文件的结构并不是一成不变的，随着Java虚拟机的不断发展，总是不可避免地会对Class文件结构做出一些调整，但是其基本结构和框架是非常稳定的。Class 文件的总体结构如下：

```c
ClassFile {
    u4             magic; //魔数 4个字节
    u2             minor_version; //小版本
    u2             major_version; //大版本号；例如1.8
    u2             constant_pool_count; //常量池长度
    cp_info        constant_pool[constant_pool_count-1];//常量内容 常量池长度-1
    u2             access_flags; //访问标志
    u2             this_class; //本类this的在常量池的索引
    u2             super_class; //父类的在常量池的索引
    u2             interfaces_count; //接口数量
    u2             interfaces[interfaces_count]; //接口索引集合，常量池的索引
    u2             fields_count; //字段数量
    field_info     fields[fields_count]; //字段表
    u2             methods_count; //方法数量
    method_info    methods[methods_count]; //方法表
    u2             attributes_count; //属性数量
    attribute_info attributes[attributes_count]; //属性表
}
```

#### 魔数

Magic Number(魔数)
- 每个Class文件开头的4个字节的无符号整数称为魔数(Magic Number)\
- 它的唯一作用是确定这个文件是否为一个能被虚拟机接受的有效合法的Class文件。即：魔数是Class文件的标识符
- 魔数值固定为0xCAFEBABE
- 如果一个Class文件不以0xCAFEBABE开头，虚拟机在进行文件校验的时候就会直接抛出错误

#### Class文件版本号

紧接着魔数的4个字节存储的是Class文件的版本号。占用4个字节。第5、6个字节所代表的含义就是编译的副版本号minor_version，而第7、8个字节就是编译的主版本号major_version，它们共同构成了Class文件的格式版本号。0x00000034表示主版本1.8 副版本0

#### 常量池

常量池是整个Class文件的基石，常量池中常量的数量是不固定的，所以在常量池的入口需要放置一项u2类型的无符号数，代表常量池容量计数值(constant_pool_count)，与Java中语言习惯不一样的是，这个容量计数是从1而不是0开始的，所以后面的常量表内容为constant_pool_count-1

常量池表项中，用于存放编译时期生成的各种字面量和符号引用，这部分内容将在类加载后进入方法区的运行时常量池中存放

##### 常量池计数器

由于常量池的数量不固定，所以需要放置两个字节来表示常量池容量计数值，常量池容量计数值(u2类型)从1开始，表示常量池中有多少项常量。

案例中的0x0010，表示常量表中有15项(constant_pool_count-1)

##### 常量池表

constant_pool是一种表结构，以1 ~ constant_pool_count - 1为索引。表明了有多少个常量项，常量池主要存放两大类常量：字面量(Literal)和符号引用(Symbolic References)。

它包含了Class文件结构及其子结构中引用的所有字符串常量、类或接口名、字段名和其他常量。常量池中的每一项都具备相同的特征。第1个字节作为类型标记，用于确定该项的格式，这个字节称为tag byte(标记字节、标签字节)

常量池的数据结构
```c
cp_info {
    u1 tag;
    u1 info[];
}
```

tag一个字节的类型如下：
> 常量池中的每一项都有固定的数据结构，需要时查询官方文档
> https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4

| Constant Type | Value | Description |
|---|---|---|
|CONSTANT_Class | 	7 | 表示类或接口 |
|CONSTANT_Fieldref | 	9 | 字段符号引用 ｜
|CONSTANT_Methodref | 10 | 类中方法符号引用 ｜
|CONSTANT_InterfaceMethodref | 11 | 接口中方法符号引用 ｜
|CONSTANT_String | 	8 | 字符串类型字面量 ｜
|CONSTANT_Integer | 	3 | 整数字面量 ｜
|CONSTANT_Float | 	4 | 浮点数字面量 ｜
|CONSTANT_Long | 	5 | 长整形字面量 ｜
|CONSTANT_Double | 	6 | 双精度浮点数字面量 ｜
|CONSTANT_NameAndType | 12 | 字段或方法符号引用 ｜
|CONSTANT_Utf8 | 1 | 常量字符串值 ｜
|CONSTANT_MethodHandle | 15 | 方法句柄 ｜
|CONSTANT_MethodType | 16 | 标志方法类型 ｜
|CONSTANT_InvokeDynamic | 18 | 表示动态方法调用点 ｜

###### 字面量和符号引用

常量池主要存放两大类常量：字面量(Literal)和符号引用(Symbolic References)

字面量：文本字符串、声明final的常量值
符号引用：类和接口的全限名、字段的名称的描述符、方法的名称和描述符

- 全限定名
    com/example/demo/ByteCode这个就是类的全限定名，仅仅是把包的"."替换成"/"，为了使连续的多个全限定名之间不产生混淆，在使用时最后一般会加入一个";"表示全限定名结束

- 简单名称
    简单名称是指没有类型和参数修饰的方法或者字段名称，例如类的add()方法和num字段的简单名称分别是add和num。

- 描述符
    描述符的作用是用来描述字段的数据类型、方法的参数列表(包括数量、类型以及顺序)和返回值。

| FieldType term | Type | Interpretation |
|---|---|---|
| B | byte | signed byte |
| C | char | Unicode character code point in the Basic Multilingual Plane, encoded with UTF-16 |
| D | double | double-precision floating-point value |
| F | float | single-precision floating-point value |
| I | int | integer |
| J | long | long integer |
| L ClassName ; | reference | an instance of class ClassName |
| S | short | signed short |
| Z | boolean | true or false |
| [ | reference | one array dimension |

举例：
方法 java.lang.String toString() 的描述符为 () Ljava/lang/String;
方法 int abc(int[] x ,int y)描述符为 ([II) I

- 符号引用：符号引用以一组符号来描述所引用的目标，符号可以是任何形式的字面量，只要使用时能无歧义地定位到目标即可。符号引用与虚拟机实现的内存布局无关，引用的目标并不一定已经加载到内存中
- 直接引用：直接引用可以是直接指向目标的指针、相对偏移量或是一个能间接定位到目标的句柄。直接引用是与虚拟机实现的内存布局相关的，同一个符号引用在不同虚拟机实例上翻译出来的直接引用一般不会相同。如果有了直接引用，那说明引用的目标必定已经存在于内存之中了。

#### 访问标识

访问标识使用两个字节表示，用于识别一些类或者接口层次的访问信息，包括：这个Class是类还是接口；是否定义为public类型；是否定义为abstract类型；如果是类的话，是否被声明为final等。
各种访问标记如下所示：
| Flag Name | Value | Interpretation |
|---|---|---|
| ACC_PUBLIC | 0x0001 | Declared public; may be accessed from outside its package. |
| ACC_FINAL | 0x0010 | Declared final; no subclasses allowed. |
| ACC_SUPER | 0x0020 | Treat superclass methods specially when invoked by the invokespecial instruction. |
| ACC_INTERFACE | 0x0200 | Is an interface, not a class. |
| ACC_ABSTRACT | 0x0400 | Declared abstract; must not be instantiated. |
| ACC_SYNTHETIC | 0x1000 | Declared synthetic; not present in the source code. |
| ACC_ANNOTATION | 0x2000 | Declared as an annotation type. |
| ACC_ENUM | 0x4000 | Declared as an enum type. |

- 类的访问权限通常为 ACC_ 开头的常量
- 每一个种类型的表示都是通过设置访问标记的16位中的特定位来实现的。比如，若是public final的类，则该标记为 ACC_PUBLIC | ACC_FINAL

#### 类索引、父类索引、接口索引集合

该索引指定该类的类别、父类类别以及实现的接口。两个字节无符号整数，指向常量池的索引。它提供了类的全限定名，如com/example/demo/ByteCode。this_class的值必须是对常量池表中某项的一个有效索引值。常量池在这个索引处的成员必须为CONSTANT_Class_info类型结构体，该结构体表示这个Class文件所定义的类或接口

- 类索引用于确定这个类的全限定名
- 父类索引用于确定这个类的父类的全限定名。Java语言不允许多重继承，所以父类索引只有一个，除了java.lang.Object之外，所有的Java类都有父类。super_class(父类索引)，两个字节无符号整数，指向常量池的索引。
- 接口索引集合就用来描述这个类实现了哪些接口，这些被实现的接口将按implements语句(如果这个类本身是一个接口，则应当是extends语句)后的接口顺序从左到右排列在接口索引集合中。interfaces_count(接口计数器)：表示当前类或接口的直接超接口数量；interface[] (接口索引集合)：每个成员的值必须是对常量池表中某项的有效索引值，必须为CONSTANT_Class_info结构。

#### 字段表集合(field_info)

用于描述接口或类中声明的变量。字段(field)包括类级变量以及实例级变量，但是不包括方法内部、代码块内部声明的局部变量。
它指向常量池索引集合，它描述了每个字段的完整信息。比如字段的标识符、访问修饰符(public、private 或 protected)、是类变量还是实例变量(static 修饰符)、是否是常量(final 修饰符)等。

##### 字段计数器(fields_count)

fields_count的值表示当前Class文件fields表的成员个数，使用两个字节来表示。

##### fields [] (字段表)

fields表中的每个成员都必须是一个fields_info结构的数据项，用于表示当前类或接口中某个字段的完整描述，不包括方法内部声明的变量，也不包括从父类或父接口继承的那些字段。

field_info数据结构如下：
```c
field_info {
    u2             access_flags; //访问标识如下表
    u2             name_index; //字段名索引的值，查询常量池中的指定索引项即可
    u2             descriptor_index; //描述符索引，描述字段的数据类型、方法的参数列表
    u2             attributes_count; //属性个数，一个字段还可能拥有一些属性，用于存储更多的额外信息。比如初始化值等
    attribute_info attributes[attributes_count]; //属性具体内容存放处
}
```

一个字段的访问flags定义如下：
| Flag Name | Value | Interpretation |
|---|---|---|
| ACC_PUBLIC | 0x0001 | Declared public; may be accessed from outside its package. |
| ACC_PRIVATE | 0x0002 | Declared private; usable only within the defining class. |
| ACC_PROTECTED | 0x0004 | Declared protected; may be accessed within subclasses. |
| ACC_STATIC | 0x0008 | Declared static. |
| ACC_FINAL | 0x0010 | Declared final; never directly assigned to after object construction. |
| ACC_VOLATILE | 0x0040 | Declared volatile; cannot be cached. |
| ACC_TRANSIENT | 0x0080 | Declared transient; not written or read by a persistent object manager. |
| ACC_SYNTHETIC | 0x1000 | Declared synthetic; not present in the source code. |
| ACC_ENUM | 0x4000 | Declared as an element of an enum. |


#### 方法表集合(method_info)

指向常量池索引的集合，它完整描述了每个方法的签名
- 每一个method_info项都对应着一个类或者接口中的方法信息。比如方法的访问修饰符(public、private 或 protected)，方法的返回值类型以及方法的参数信息等。如果这个方法不是抽象的或者不是native的，那么字节码中会体现出来以上信息。
- methods 表只描述当前类或接口中声明的方法，不包括从父类或父接口继承的方法。
- methods 表有可能会出现由编译器自动添加的方法，最典型的便是编译器产生的方法信息(比如：类初始化方法()和实例初始化方法()

```text
注：
在Java语言中，要重载(Overload)一个方法，除了要与原方法具有相同的简单名称之外，还要求必须拥有一个与原方法不同的特征签名，特征签名就是一个方法中各个参数在常量池中的字段符号引用的集合，也就是因为返回值不会包含在特征签名之中，因此Java语言里无法仅仅依靠返回值的不同来对一个已有方法进行重载。但在Class文件格式中，特征签名的范围更大一些，只要描述符不是完全一致的两个方法就可以共存。也就是说，如果两个方法有相同的名称和特征签名，但返回值不同，那么也是可以合法共存于同一个Class文件中。尽管Java语法规范并不允许在一个类或者接口中声明多个方法签名相同的方法，但是和Java语法规范相反，字节码文件中却恰恰允许存放多个方法签名相同的方法，唯一的条件就是这些方法之间的返回值不能相同。
```

methods_count：表示当前 Class 文件 methods 表的成员个数，使用两个字节来表示
methods：表中每个成员都是一个method_info结构
```c
method_info {
    u2             access_flags; //方法的访问标识
    u2             name_index; //名称索引
    u2             descriptor_index; //描述符索引
    u2             attributes_count; //属性个数
    attribute_info attributes[attributes_count]; //属性表，注意不是方法的入参属性
}
```

#### 属性(attribute_info)

指的是Class文件所携带的辅助信息。

attributes_count：属性个数
attributes[]：属性表

属性(attribute_info)的数据结构：
```text
attribute_info {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 info[attribute_length];
}
```



## 分析

分析一个最简单的类：
```java
package com.example.demo;

public class ByteCode {
}
```
编译生成的二进制以十六进制打开如下：
```text
cafe babe 0000 0034 0010 0a00 0300 0d07
000e 0700 0f01 0006 3c69 6e69 743e 0100
0328 2956 0100 0443 6f64 6501 000f 4c69
6e65 4e75 6d62 6572 5461 626c 6501 0012
4c6f 6361 6c56 6172 6961 626c 6554 6162
6c65 0100 0474 6869 7301 001b 4c63 6f6d
2f65 7861 6d70 6c65 2f64 656d 6f2f 4279
7465 436f 6465 3b01 000a 536f 7572 6365
4669 6c65 0100 0d42 7974 6543 6f64 652e
6a61 7661 0c00 0400 0501 0019 636f 6d2f
6578 616d 706c 652f 6465 6d6f 2f42 7974
6543 6f64 6501 0010 6a61 7661 2f6c 616e
672f 4f62 6a65 6374 0021 0002 0003 0000
0000 0001 0001 0004 0005 0001 0006 0000
002f 0001 0001 0000 0005 2ab7 0001 b100
0000 0200 0700 0000 0600 0100 0000 0300
0800 0000 0c00 0100 0000 0500 0900 0a00
0000 0100 0b00 0000 0200 0c
```

- 前4个字节是魔数：0xcafebabe
- 接着4个字节表示版本：0x00000034，大版本1.8，小版本0
- 接着1个字节表示常量池大小：0x0010，表示常量池计数16，后面有15个常量
- 我们来看常量池的第一项
    1. 解析1个字节0x0a，表示tag，查询常量池表示CONSTANT_Methodref，而其数据结构为
    ```c
    CONSTANT_Methodref_info {
        u1 tag;
        u2 class_index;
        u2 name_and_type_index;
    }
    ```
    2. 接着2个字节表示class_index：0x0003，索引指向常量池的第3项
    3. 接着2个字节表示name_and_type_index：0x000d，索引指向常量池的第13项
    4. 至此，第一项解析完
- 接下来看第二项
    1. 解析1个字节0x07，表示tag，查询常量池改标识表示CONSTANT_Class，其数据结构如下：

    ```c
    CONSTANT_Class_info {
        u1 tag;
        u2 name_index;
    }
    ```
    2. 解析2个字节表示name_index：000e，索引常量池的第14项
    3. 第二项解释完
- 接下来看第三项，此项也是第一项中的class_index指向的位置
    1. 解析1个字节0x07，表示tag，查询常量池表示CONSTANT_Class一个类
    2. 接着读取两个字节0x000f，表示索引指向常量池的15项
- 接下来看第四项
    1. 解析一个字节0x01，在常量池中表示CONSTANT_Utf8，其结构
    
    ```c
    CONSTANT_Utf8_info {
        u1 tag;
        u2 length;
        u1 bytes[length];
    }
    ```
    2. 读取两个字节0x0006，表示6个字节长
    3. 接下来就有6个字节长度表示内容，翻译成字符为"<init>"，表示一个字符串常量
- 略过直接看解析第13项，也就是第一项name_and_type_index所指向的位置
    1. 读取一个字节0x0c，在常量池中表示的就是CONSTANT_NameAndType，他的数据结构：

    ```c
    CONSTANT_NameAndType_info {
        u1 tag;
        u2 name_index;
        u2 descriptor_index;
    }
    ```
    2. 读取两个字节0x0004表示name_index，就是索引到常量池的第四项内容，即："<init>"
    3. 再读取两个字节0x0005表示descriptor_index，那么常量池的第五项内容为："()V"
    4. 其实这里已经能看得出了，这是一个无参的构造方法
- 依照此方法解析完15个常量

- 解析完常量接下来的两个字节就表示access_flags，0x0021，其表示 ACC_SUPER｜ACC_PUBLIC 或运算得来的，即0x0020｜0x0001。表示是public修饰的，而在jdk8以后为每个类加上ACC_SUPER修饰
- 接下来两个字节为0x0002表示this_class，指向常量池的第二项，而第二项指向第14项，内容为"com/example/demo/ByteCode"类的全限名
- 接下来的两个字节为0x0003表示super_class，指向常量池的第三项，而第三项指向第15项，内容为"java/lang/Object"
- 按照class文件的结构依次解析完所有的字节

通过javap -v classpath 命令解析如下：
清晰可见我们如上分析的常量池每项内容
```text
Classfile /.../project/demo/target/classes/com/example/demo/ByteCode.class
  Last modified 2022-10-31; size 283 bytes
  MD5 checksum 8e621c9fa4c4e046f31004dae562d886
  Compiled from "ByteCode.java"
public class com.example.demo.ByteCode
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #3.#13         // java/lang/Object."<init>":()V
   #2 = Class              #14            // com/example/demo/ByteCode
   #3 = Class              #15            // java/lang/Object
   #4 = Utf8               <init>
   #5 = Utf8               ()V
   #6 = Utf8               Code
   #7 = Utf8               LineNumberTable
   #8 = Utf8               LocalVariableTable
   #9 = Utf8               this
  #10 = Utf8               Lcom/example/demo/ByteCode;
  #11 = Utf8               SourceFile
  #12 = Utf8               ByteCode.java
  #13 = NameAndType        #4:#5          // "<init>":()V
  #14 = Utf8               com/example/demo/ByteCode
  #15 = Utf8               java/lang/Object
{
  public com.example.demo.ByteCode();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1 // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 3: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lcom/example/demo/ByteCode;
}
SourceFile: "ByteCode.java"
```

也可以通过idea的插件jclasslib插件查看class的内容，非常方便

通过该方法慢慢在方法中加属性，方法等等，徐徐渐进。

> oracle jdk1.8的java虚拟机规范手册查询地址
> https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.1

以下是我对上面简单程序的class文件进行了详细的分析
![](./images/class%E6%96%87%E4%BB%B6%E4%BA%8C%E8%BF%9B%E5%88%B6%E8%AF%A6%E7%BB%86%E5%88%86%E6%9E%90.jpg)

## 总结

1. Java虚拟机(JVM)是运行字节码的，也就是加载并运行class字节码，只要满足class规范，都可以被虚拟机识别并加载执行，其实和语言无关，例如groovy、Scala等语言也可以通过编译器翻译成符合Java虚拟机规范的字节码文件也可被加载运行。
2. class文件十分简单，只是非常繁琐，他规定了哪个字节代表什么含义，长度是多少等等。
3. 通过分析简单的类class文件，然后通过添加属性和方法依次分析，徐徐渐进。





