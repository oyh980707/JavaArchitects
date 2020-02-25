package com.loveoyh.ProxyPattern.CustomProxy;

import java.lang.reflect.Method;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/25 05:09
 */
public class DynamicProxy implements InvocationHandler{
    private Object target;

    public Object getInstance(Object target){
        this.target = target;
        Class<?> clazz = this.target.getClass();
        return Proxy.newProxyInstance(new ClassLoader(),clazz.getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Proxy before method.");
        method.invoke(this.target,args);
        System.out.println("Proxy after method.");
        return null;
    }
}
