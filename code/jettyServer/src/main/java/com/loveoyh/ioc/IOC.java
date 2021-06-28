package com.loveoyh.ioc;

import com.sun.istack.internal.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IOC {

    private static final ConcurrentHashMap<String, Object> beans = new ConcurrentHashMap();

    public static <T> T getBean(String beanId, @Nullable Class<T> requiredType){
        try {
            Object o = beans.get(beanId);
            if(o == null){
                o = requiredType.newInstance();
                beans.put(beanId,o);
            }
            return (T) o;
        } catch (Exception e) {
            return null;
        }
    }

    public static void clear() {
        beans.clear();
    }
}
