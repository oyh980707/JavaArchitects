package com.loveoyh.factorypattern.AbstractFactoryPattern.product.Default;

import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Keyboard;

public class DefaultKeyboard implements Keyboard {
    public void sayHi() {
        System.out.println("DefaultKeyboard:Hello!");
    }
}
