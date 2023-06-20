package com.example.lambda;

/**
 * 在classes目录下终端：java -Djdk.internal.lambda.dumpProxyClasses com.example.lambda.Main
 * 导出lambda中间文件类
 * 通过> javap -c -p 查看类中生成的private static 方法，供lambda生成的内部类来实现调用
 */
public class Main {
    public static void main(String[] args) {
        Calculator c = () -> System.out.println("Hello Lambda!");
        c.calculator();
    }
}
