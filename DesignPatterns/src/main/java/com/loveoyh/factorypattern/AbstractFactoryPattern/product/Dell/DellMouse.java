package com.loveoyh.factorypattern.AbstractFactoryPattern.product.Dell;

import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Mouse;

public class DellMouse implements Mouse {
    public void sayHi() {
        System.out.println("DellMouse:Hello!");
    }
}
