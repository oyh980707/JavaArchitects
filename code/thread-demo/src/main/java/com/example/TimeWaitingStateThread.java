package com.example;

public class TimeWaitingStateThread {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(100000);
            } catch (InterruptedException ignore) { }
        });
        t.start();
    }
}