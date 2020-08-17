package com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.HP;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Keyboard;

public class HPKeyboard implements Keyboard {
    public void sayHi() {
        System.out.println("HPKeyboard:Hello!");
    }
}
