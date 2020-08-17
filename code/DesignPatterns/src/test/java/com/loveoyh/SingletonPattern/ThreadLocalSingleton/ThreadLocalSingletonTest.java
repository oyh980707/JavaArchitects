package com.loveoyh.SingletonPattern.ThreadLocalSingleton;

public class ThreadLocalSingletonTest {
    public static void main(String[] args) {
        System.out.println(ThreadLocalSingleton.getInstance());
        System.out.println(ThreadLocalSingleton.getInstance());
        System.out.println(ThreadLocalSingleton.getInstance());
        System.out.println(ThreadLocalSingleton.getInstance());
        System.out.println(ThreadLocalSingleton.getInstance());
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                System.out.println(ThreadLocalSingleton.getInstance());
            }
        });
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                System.out.println(ThreadLocalSingleton.getInstance());
            }
        });
        t1.start();
        t2.start();
        System.out.println("End");
    }
}
