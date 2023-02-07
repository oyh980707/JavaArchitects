package com.example;

public class NewStateThread {

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            System.out.println(Thread.currentThread().getName());
        });// 此时的t线程
        Thread.sleep(10000);
    }

}
