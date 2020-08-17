package com.loveoyh.SingletonPattern.HungrySingleton;

/**
 * 饿汉式单例
 */
public class HungrySingleton {
    //类加载时初始化，或者使用静态块来初始化
    private static final HungrySingleton HUNGRY_SINGLETON = new HungrySingleton();

    //私有化构造方法
    private HungrySingleton(){}

    public static HungrySingleton getInstance(){
        return HUNGRY_SINGLETON;
    }
}
