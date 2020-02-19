package com.loveoyh.factorypattern.AbstractFactoryPattern.product.Default;

import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Mouse;

public class DefaultMouse implements Mouse{
    public void sayHi() {
        System.out.println("DefaultMouse:Hello!");
    }
}
