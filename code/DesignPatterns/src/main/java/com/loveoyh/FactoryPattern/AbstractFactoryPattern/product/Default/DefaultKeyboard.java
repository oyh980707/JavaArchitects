package com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Default;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Keyboard;

public class DefaultKeyboard implements Keyboard {
    public void sayHi() {
        System.out.println("DefaultKeyboard:Hello!");
    }
}
