package com.loveoyh.ProxyPattern.CustomProxy;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/25 05:15
 */
public class Customer implements Person {
    @Override
    public void findLove() {
        System.out.println("要求:高端大气上档次!!!");
    }
}
