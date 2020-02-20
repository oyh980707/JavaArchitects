package com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Dell;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Keyboard;

public class DellKeyboard implements Keyboard {
    public void sayHi() {
        System.out.println("DellKeyboard:Hello!");
    }
}
