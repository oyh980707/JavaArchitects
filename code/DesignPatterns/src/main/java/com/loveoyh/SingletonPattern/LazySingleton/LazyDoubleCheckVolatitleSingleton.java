package com.loveoyh.SingletonPattern.LazySingleton;

/**
 * 双重检查锁+防止指令重排的单例模式
 */
public class LazyDoubleCheckVolatitleSingleton {
    private static volatile LazyDoubleCheckVolatitleSingleton lazy = null;

    private LazyDoubleCheckVolatitleSingleton(){}

    public static LazyDoubleCheckVolatitleSingleton getInstance(){
        if(lazy == null){
            synchronized (LazyDoubleCheckVolatitleSingleton.class){
                if(lazy == null) {
                    lazy = new LazyDoubleCheckVolatitleSingleton();
                }
            }
        }
        return lazy;
    }
}
