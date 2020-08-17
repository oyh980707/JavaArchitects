package com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Dell;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.DellFactory.DellKeyboardFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.DellFactory.DellMouseFactory;

public class DellProductFactory {
    public static AbstractFactory keyboardFactory(){
        return new DellKeyboardFactory();
    }

    public static AbstractFactory mouseFactory(){
        return new DellMouseFactory();
    }
}
