package com.loveoyh.SingletonPattern.LazySingleton;

/**
 * 懒汉式加载
 * 1.不做线程安全的话，会出现两个测试线程得到的lazySingleton实例不一致，但是一致的情况也
 * 未必只实例化了一次，当两个线程同时进入，同时出来，输出结果就一直，但是实例化了两次
 * 2.给getInstance方法加上synchronized，是这个方法变成同步方法。用synchronized加锁
 * 在线程数量比较多情况下，如果CPU 分配压力上升，会导致大批量线程出现阻塞，
 * 从而导致程序运行性能大幅下降。
 */
public class LazySingleton {

    private static LazySingleton lazySingleton = null;

    private LazySingleton(){}

    public synchronized static LazySingleton getInstance(){
        if(lazySingleton == null){
            lazySingleton = new LazySingleton();
        }
        return lazySingleton;
    }
}
