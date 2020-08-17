package com.loveoyh.SingletonPattern.RegisterSingleton;

public class ContainerSingletonTest {
    public static void main(String[] args) {
        ContainerSingleton ioc = (ContainerSingleton) ContainerSingleton.getInstance("com.loveoyh.SingletonPattern.RegisterSingleton.ContainerSingleton");
        System.out.println(ioc);
    }
}
