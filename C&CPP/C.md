# C语言学习

C语言的编译过程

1. 代码预处理：编译器首先会移除源代码中的所有注释信息，并处理所有的宏指令。包括宏展开、宏替换、条件编译等等。
2. 编译优化：编译器会分析和优化源代码，并将其编译成对应的汇编代码。这部分代码中含有使用汇编指令描述的原始C程序逻辑。
3. 汇编：编译器将这些汇编指令代码编译成可以被操作系统使用的某种对象文件格式
4. 链接：通过连接处理，编译器会将目前需要进行整合的文件进行整合，设置好所有调用函数的正确地址，并生成对应的二进制可执行文件。

以上所有过程结束后，我们就得到可以直接运行的二进制文件。在不同的操作系统上，可以通过不同的方式来运行这个程序，双击或者命令行等等。

```c
#include <stdio.h>
int main(){
    int a = 0;
    printf("Hello Word!");
    return 0;
}
```
预处理
```shell
gcc -E hello.c -o hello.i
```
编译
```shell
gcc -S hello.i -o hello.s
```
汇编
```shell
gcc -c hello.s -o hello.o
```
链接
```shell
gcc hello.o -o hello
```

## C语言的运算符

C17标准中，C语言总共有48个运算符
```text
算数运算符：+、-、*、/、%、++、--
关系运算符：==、!=、>、>=、<、<=
位运算符：&、|、^、~、<<、>>
赋值运算符：=、+=、-+、*=、/=
逻辑运算符：&&、||、!
成员访问运算符：a[b]、a.b、a->b、&a、*a
其他运算符：sizeof、?:、(type)a、a、...
```

在这些运算符里面
通常来说，算数、关系、位、复制运算符的实现大多数情况下都是与特定的汇编指令一一对应。
逻辑运算符的实现会借助test、cmp等指令，来判断操作数的状态，并在此基础上再进行相应的数值转换过程。
成员访问运算符中，取地址运算符一般对应汇编指令lea，解引用运算符可以通过mov指令来完成。
对于其他运算符，sizeof运算符会在编译的时候进行求值替换，强类型转换则 直接对应于不同指令对同一块数据的不同处理方式。

## 控制逻辑

### 表达式

表达式有一系列运算符与操作数组成的语法结构，操作数是参与运算符计算的独立单元，也是运算符的操作对象。

对表达式的求值过程，实际上就是根据运算符的优先级和结合性，来对表达式和其它所包含的字表达时进行递归求值的过程。

```c
int main(){
    return (1 + 2) * 3 + 4 / 5;
}
```

借助Clang编译器提供的"-ast-dump"选项编译并打印对应的AST结构

```shell
clang -Xclang -ast-dump -fsyntax-only main.c
```

```text
`-FunctionDecl 0x7fa49e009e00 <main.c:1:1, line:3:1> line:1:5 main 'int ()'
  `-CompoundStmt 0x7fa49e00a038 <col:11, line:3:1>
    `-ReturnStmt 0x7fa49e00a028 <line:2:5, col:30>
      `-BinaryOperator 0x7fa49e00a008 <col:12, col:30> 'int' '+'
        |-BinaryOperator 0x7fa49e009f88 <col:12, col:22> 'int' '*'
        | |-ParenExpr 0x7fa49e009f48 <col:12, col:18> 'int'
        | | `-BinaryOperator 0x7fa49e009f28 <col:13, col:17> 'int' '+'
        | |   |-IntegerLiteral 0x7fa49e009ee8 <col:13> 'int' 1
        | |   `-IntegerLiteral 0x7fa49e009f08 <col:17> 'int' 2
        | `-IntegerLiteral 0x7fa49e009f68 <col:22> 'int' 3
        `-BinaryOperator 0x7fa49e009fe8 <col:26, col:30> 'int' '/'
          |-IntegerLiteral 0x7fa49e009fa8 <col:26> 'int' 4
          `-IntegerLiteral 0x7fa49e009fc8 <col:30> 'int' 5
```

表达式能够让数据同时参与到多个操作数的不同计算过程中。

### 语句

语句用来描述程序的基本构建块，与表达式不同，语句是构成C程序的最大力度单元，在它内部可以包含有简单或复杂的表达式结构，但也可以不包含任何内容。语句都以分号结尾，并按照从上到下的顺序执行。语句分为复合语句、选择语句、表达式语句、迭代语句、跳转语句。

复合语句主要以花括号标记一块区域。
表达式语句直接是由表达式外加一个分号构成的一个语句。
选择语句主要由if...else和switch...case这两种语法结构组成的语句。
迭代语句主要包含do...while、for、while这三种基本语法形式。
跳转语句主要是那些可以改变程序执行流程的语法结构，例如break、continue、return、goto语句。





