package com.loveoyh.PrototypePattern.ShallowClone;

import com.loveoyh.PrototypePattern.ShallowClone.Client;
import com.loveoyh.PrototypePattern.ShallowClone.ConcretePrototypeA;

import java.util.ArrayList;
import java.util.List;

public class PrototypeTest {
    public static void main(String[] args) {
        ConcretePrototypeA cPrototypeA = new ConcretePrototypeA();
        cPrototypeA.setName("Tony");
        cPrototypeA.setAge(20);
        List<String> hobbies = new ArrayList<String>();
        hobbies.add("play");
        cPrototypeA.setHobbies(hobbies);

        System.out.println("克隆前："+cPrototypeA);

        //创建客户端对象，准备开始克隆
        Client client = new Client(cPrototypeA);
        ConcretePrototypeA clonePrototypeA = (ConcretePrototypeA)client.startClone(cPrototypeA);

        System.out.println("克隆后："+clonePrototypeA);
        System.out.println("克隆对象中的引用类型地址值：" + clonePrototypeA.getHobbies());
        System.out.println("原对象中的引用类型地址值：" + cPrototypeA.getHobbies());
        System.out.println("对象地址比较："+(clonePrototypeA.getHobbies() == cPrototypeA.getHobbies()));
    }
}
