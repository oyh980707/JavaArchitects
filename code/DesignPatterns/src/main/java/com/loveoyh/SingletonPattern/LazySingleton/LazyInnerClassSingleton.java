package com.loveoyh.SingletonPattern.LazySingleton;

/**
 * 静态内部类的方式
 * 这种形式兼顾饿汉式的内存浪费,也兼顾 synchronized 性能问题,完美地屏蔽了这两个缺点
 */
public class LazyInnerClassSingleton {
    private LazyInnerClassSingleton(){
        if(LazyHolder.LAZY != null){
            throw new RuntimeException("不允许创建两个实例!");
        }
    }
    /*
     * static 是为了使单例的空间共享
     * final 保证这个方法不会被重写，重载
     */
    public static final LazyInnerClassSingleton getInstance(){
        return LazyHolder.LAZY;
    }

    /*
     * 默认使用内部类的时候,会先初始化内部类,如果没使用的话，内部类是不加载
     */
    private static class LazyHolder {
        private static final LazyInnerClassSingleton LAZY = new LazyInnerClassSingleton();
    }
}
