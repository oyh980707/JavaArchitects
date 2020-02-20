package com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Default;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Mouse;

public class DefaultMouse implements Mouse{
    public void sayHi() {
        System.out.println("DefaultMouse:Hello!");
    }
}
