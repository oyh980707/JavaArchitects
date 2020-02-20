package com.loveoyh.SingletonPattern.LazySingleton;

import java.lang.reflect.Constructor;

public class LazyInnerClassSingletonTest {
    public static void main(String[] args) {
//        LazyInnerClassSingleton lazy = LazyInnerClassSingleton.getInstance();
//        System.out.println(lazy);

        /**
         * 反射破坏单例
         */
        try {
            //
            Class<?> cls = LazyInnerClassSingleton.class;
            //通过反射拿到私有的构造方法
            Constructor c = cls.getDeclaredConstructor();
            //强制访问
            c.setAccessible(true);
            Object obj1 = c.newInstance();
            Object obj2 = c.newInstance();

            System.out.println(obj1 == obj2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
