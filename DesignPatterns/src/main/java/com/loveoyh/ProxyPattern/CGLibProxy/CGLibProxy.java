package com.loveoyh.ProxyPattern.CGLibProxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.omg.PortableInterceptor.Interceptor;

import java.lang.reflect.Method;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/25 05:47
 */
public class CGLibProxy implements MethodInterceptor {
    public Object getInstance(Class<?> clazz){
        Enhancer enhancer = new Enhancer();
        //要把哪个设置为即将生成的新类父类
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("CGLibProxy before method.");
        Object obj = methodProxy.invokeSuper(o,objects);
        System.out.println("CGLibProxy after method.");
        return null;
    }
}
