package com.loveoyh.factorypattern.AbstractFactoryPattern.product.Dell;

import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Keyboard;

public class DellKeyboard implements Keyboard {
    public void sayHi() {
        System.out.println("DellKeyboard:Hello!");
    }
}
