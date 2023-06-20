package com.example.methodrefrence;

import java.util.ArrayList;
import java.util.List;

/**
 * 在classes目录下终端：java -Djdk.internal.lambda.dumpProxyClasses com.example.methodrefrence.MethodReference
 * 导出lambda中间文件类
 * 通过> javap -c -p 查看类中生成的private static 方法，供lambda生成的内部类来实现调用
 */
public class MethodReference {

    public static void consumer(String s) {
        System.out.println(s);
    }

    public static void main(String[] args) {
        List<String> l = new ArrayList<>();
        l.add("abc");
        l.forEach(MethodReference::consumer);
    }

}
