package com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.HP;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.HPFactory.HPKeyboardFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.HPFactory.HPMouseFactory;

public class HPProductFactory {
    public static AbstractFactory keyboardFactory(){
        return new HPKeyboardFactory();
    }

    public static AbstractFactory mouseFactory(){
        return new HPMouseFactory();
    }
}
