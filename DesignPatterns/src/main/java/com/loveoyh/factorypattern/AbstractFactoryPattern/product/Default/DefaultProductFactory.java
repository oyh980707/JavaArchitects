package com.loveoyh.factorypattern.AbstractFactoryPattern.product.Default;

import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.defaultFactory.DefaultKeyboardFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.defaultFactory.DefaultMouseFactory;

public class DefaultProductFactory {
    public static AbstractFactory keyboardFactory(){
        return new DefaultKeyboardFactory();
    }

    public static AbstractFactory mouseFactory(){
        return new DefaultMouseFactory();
    }
}
