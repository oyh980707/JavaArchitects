package com.loveoyh.SingletonPattern.LazySingleton;

public class ExcutorThead implements Runnable {
    public void run() {
//        LazySingleton lazySingleton = LazySingleton.getInstance();
        LazyDoubleCheckSingleton lazy = LazyDoubleCheckSingleton.getInstance();
        System.out.println(Thread.currentThread().getName()+":"+lazy);
    }
}
