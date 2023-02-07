package com.example;

public class BlockedStateThread {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                synchronized (Object.class) {
                    Object.class.wait();
                }
            } catch (InterruptedException ignore) { }
        });
        t.start();
    }
}