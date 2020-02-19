package com.loveoyh.factorypattern.AbstractFactoryPattern.product.HP;

import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Keyboard;

public class HPKeyboard implements Keyboard {
    public void sayHi() {
        System.out.println("HPKeyboard:Hello!");
    }
}
