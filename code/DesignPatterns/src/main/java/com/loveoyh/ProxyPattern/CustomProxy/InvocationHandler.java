package com.loveoyh.ProxyPattern.CustomProxy;

import java.lang.reflect.Method;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/24 23:32
 */
public interface InvocationHandler {
    public Object invoke(Object proxy, Method method,Object[] args) throws Throwable;
}
