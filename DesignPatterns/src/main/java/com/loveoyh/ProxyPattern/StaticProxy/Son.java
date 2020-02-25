package com.loveoyh.ProxyPattern.StaticProxy;

/**
 * 儿子要找对象，实现Person接口
 */
public class Son implements Person{
    public void findLove() {
        System.out.println("儿子要求：善良、孝顺");
    }
}
