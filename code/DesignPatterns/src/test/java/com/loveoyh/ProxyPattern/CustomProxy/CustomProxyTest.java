package com.loveoyh.ProxyPattern.CustomProxy;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/25 05:14
 */
public class CustomProxyTest {
    public static void main(String[] args) {
        Person obj = (Person) new DynamicProxy().getInstance(new Customer());
        obj.findLove();
    }
}
