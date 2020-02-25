package com.loveoyh.ProxyPattern.DynamicProxy;

import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;
import java.lang.reflect.Proxy;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/24 21:42
 */
public class DynamicProxyTest {
    public static void main(String[] args) {
        try {
            Person person = (Person) new JDKProxy().getInstance(new Son());
            person.findLove();

            //获取动态加载的代理类
//            byte[] bytes = ProxyGenerator.generateProxyClass("$proxy0",new Class[]{Person.class});
//            FileOutputStream fos = new FileOutputStream("C:\\Users\\HP\\Desktop\\proxy.class");
//            fos.write(bytes);
//            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
