package com.loveoyh.SingletonPattern.ThreadLocalSingleton;

/**
 * ThreadLocal将所有的对象全部放在ThreadLocalMap中，为每个线程都提供一个对象
 * 实际上是以空间换时间来实现线程间隔离的。
 */
public class ThreadLocalSingleton {
    private static final ThreadLocal<ThreadLocalSingleton> threadLocalInstance = new ThreadLocal<ThreadLocalSingleton>() {
        @Override
        protected ThreadLocalSingleton initialValue() {
            return new ThreadLocalSingleton();
        }
    };
    private ThreadLocalSingleton(){}

    public static ThreadLocalSingleton getInstance(){
        return threadLocalInstance.get();
    }

}
