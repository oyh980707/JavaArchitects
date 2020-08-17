package com.loveoyh.ProxyPattern.dbroute.proxy;

import com.loveoyh.ProxyPattern.dbroute.db.DynamicDataSourceEntity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/24 21:50
 */
public class OrderServiceDynamicProxy implements InvocationHandler {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy");
    private Object target;

    public Object getInstance(Object target){
        this.target = target;
        Class<?> clazz = this.target.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(),clazz.getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Proxy before method.");
        before(args[0]);
        Object object = method.invoke(this.target,args);
        System.out.println("Proxy after method.");
        return object;
    }

    public void before(Object target) throws Exception {
        Long time = (Long) target.getClass().getMethod("getCreateTime").invoke(target);
        Integer dbRouter = Integer.valueOf(format.format(new Date(time)));
        System.out.println("静态代理类自动分配到[DB_"+dbRouter+"]数据源处理数据");
        DynamicDataSourceEntity.set(dbRouter);
    }
}
