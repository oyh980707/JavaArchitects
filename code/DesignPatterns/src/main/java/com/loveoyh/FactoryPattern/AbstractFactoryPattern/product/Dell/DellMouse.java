package com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Dell;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Mouse;

public class DellMouse implements Mouse {
    public void sayHi() {
        System.out.println("DellMouse:Hello!");
    }
}
