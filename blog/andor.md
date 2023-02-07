# false || true && false 的运行过程

在Java中，论如下代码是如何执行的呢？先与后或，还是先或后与，还是从左向右执行呢？

写个小demo分析分析便知晓答案：
```java
public class AndOr {
    static boolean a = true; // 这样写为了防止编译器优化
    static boolean b = false;
    static boolean c = false;
    public static void main(String[] args) {
        boolean d = a || b && c;
    }
}
```

我们看反编译的字节码，取关键部分如下：

```text
0: getstatic     #2                  // Field a:Z 获取a
3: ifne          18 // 从操作数栈弹出一个值判断是否不等于0
                    // 言外之意：a是否为true，为true就保存变量d为true，为false就继续向下执行
6: getstatic     #3                  // Field b:Z
9: ifeq          22 // 判断b是否为true，为false，就将false保存变量d中，如果为true，则继续向下执行
12: getstatic     #4                  // Field c:Z
15: ifeq          22 // 如果c为true，结果为true，c为false结果为false
18: iconst_1
19: goto          23
22: iconst_0
23: istore_1
24: return
```

分析字节码执行流程得出：a || b && c
1. 当a为true，结果为true
2. 当a为false，则判断b，如果b为false则结果为false
3. 当a为false，b为true，则就需要判断c是否为true
4. 如果c为true，则结果为true，如果c为false则结果为false

所以说，这种表达式自左向右依次执行，也就是说||和&&优先级没有高低之分，表达值自左向右来执行程序。多个短路与或，遇到||如果前者为true整个结果为true，遇到&&​前者为false，整个结果就为false。​

每天进步一点点...