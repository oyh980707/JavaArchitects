package com.loveoyh.SingletonPattern.LazySingleton;

/**
 * 懒汉式加载测试类
 */
public class LazyDoubleCheckSingletonTest {
    public static void main(String[] args) {
        Thread t1 = new Thread(new ExcutorThead());
        Thread t2 = new Thread(new ExcutorThead());

        t1.start();
        t2.start();

        System.out.println("Main end.");
    }
}
