package com.loveoyh.ProxyPattern.DynamicProxy;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/24 21:41
 */
public class Son implements Person {
    @Override
    public void findLove() {
        System.out.println("儿子要求：善良、孝顺");
    }
}
