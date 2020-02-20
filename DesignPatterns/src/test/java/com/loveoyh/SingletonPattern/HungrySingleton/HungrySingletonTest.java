package com.loveoyh.SingletonPattern.HungrySingleton;

/**
 * 饿汉式单例测试
 */
public class HungrySingletonTest {
    public static void main(String[] args) {
        HungrySingleton hungrySingleton = HungrySingleton.getInstance();
        System.out.println(hungrySingleton);
    }
}
