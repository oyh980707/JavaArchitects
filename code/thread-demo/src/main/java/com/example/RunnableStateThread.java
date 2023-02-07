package com.example;

public class RunnableStateThread {

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            System.out.println(Thread.currentThread().getName());
        });
        t.start();
        Thread.sleep(10000);
    }

}
