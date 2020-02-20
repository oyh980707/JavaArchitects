package com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.HP;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Mouse;

public class HPMouse implements Mouse {
    public void sayHi() {
        System.out.println("HPMouse:Hello!");
    }
}
