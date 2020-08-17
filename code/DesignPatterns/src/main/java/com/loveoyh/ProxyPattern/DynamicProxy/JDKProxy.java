package com.loveoyh.ProxyPattern.DynamicProxy;

import javax.swing.text.html.HTML;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/24 21:25
 */
public class JDKProxy implements InvocationHandler {
    //被代理的对象，把引用给保存下来
    private Object target;

    public Object getInstance(Object target){
        this.target = target;
        Class<?> clazz = this.target.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(),clazz.getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("JDKProxy before method.");
        method.invoke(this.target,args);
        System.out.println("JDKProxy after method");
        return null;
    }
}
