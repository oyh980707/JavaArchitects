package com.loveoyh.SingletonPattern.SerializableSingleton;

import java.io.Serializable;

/**
 * 序列化时导致单例破坏
 */
public class SerializableSingleton implements Serializable {
    /*
     * 序列化就是说把内存中的状态通过转换成字节码的形式,从而转换一个 IO 流,
     * 写入到其他地方(可以是磁盘、网络 IO),内存中状态给永久保存下来了
     * 反序列化将已经持久化的字节码内容,转换为IO流通过IO流的读取，
     * 进而将读取的内容转换为 Java 对象,在转换过程中会重新创建对象new
     *
     * 解决方案：只需要增加readResolve()方法即可
     */
    private static final SerializableSingleton SERIALIZABLE_SINGLETON = new SerializableSingleton();

    private SerializableSingleton(){}

    public static SerializableSingleton getInstance(){
        return SERIALIZABLE_SINGLETON;
    }

    private Object readResolve() {
        return SERIALIZABLE_SINGLETON;
    }

}
