package com.loveoyh.factorypattern.AbstractFactoryPattern.product.HP;

import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Mouse;

public class HPMouse implements Mouse {
    public void sayHi() {
        System.out.println("HPMouse:Hello!");
    }
}
