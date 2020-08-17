package com.loveoyh.SingletonPattern.SerializableSingleton;

import com.loveoyh.SingletonPattern.SerializableSingleton.SerializableSingleton;

import java.io.*;

/**
 * 序列化反序列化破坏单例
 */
public class SerializableSingletonTest {
    public static void main(String[] args) {
        SerializableSingleton s1 = null;
        SerializableSingleton s2 = SerializableSingleton.getInstance();

        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream("SerializableSingleton.obj"));
            oos.writeObject(s2);
            oos.flush();

            ois = new ObjectInputStream(new FileInputStream("SerializableSingleton.obj"));
            s1 = (SerializableSingleton) ois.readObject();

            System.out.println("s1:"+s1);
            System.out.println("s2:"+s2);
            System.out.println(s1==s2);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                oos.close();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
