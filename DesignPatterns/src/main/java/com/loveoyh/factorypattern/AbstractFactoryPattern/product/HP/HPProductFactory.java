package com.loveoyh.factorypattern.AbstractFactoryPattern.product.HP;

import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.HPFactory.HPKeyboardFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.HPFactory.HPMouseFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.defaultFactory.DefaultKeyboardFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.defaultFactory.DefaultMouseFactory;

public class HPProductFactory {
    public static AbstractFactory keyboardFactory(){
        return new HPKeyboardFactory();
    }

    public static AbstractFactory mouseFactory(){
        return new HPMouseFactory();
    }
}
