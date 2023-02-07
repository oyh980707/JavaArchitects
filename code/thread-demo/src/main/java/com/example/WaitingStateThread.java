package com.example;

public class WaitingStateThread {

    public static Object o = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            while (true) {}
        });
        t.start();
    }

}
