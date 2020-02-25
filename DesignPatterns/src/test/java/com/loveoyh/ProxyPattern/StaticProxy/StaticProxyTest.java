package com.loveoyh.ProxyPattern.StaticProxy;

/**
 * 静态代理测试类
 */
public class StaticProxyTest {
    public static void main(String[] args) {
        Father father = new Father(new Son());
        father.findLove();
        System.out.println("END.");
    }
}
