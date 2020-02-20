package com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Default;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.defaultFactory.DefaultKeyboardFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.defaultFactory.DefaultMouseFactory;

public class DefaultProductFactory {
    public static AbstractFactory keyboardFactory(){
        return new DefaultKeyboardFactory();
    }

    public static AbstractFactory mouseFactory(){
        return new DefaultMouseFactory();
    }
}
