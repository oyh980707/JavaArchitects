package com.loveoyh.factorypattern.AbstractFactoryPattern.product.Dell;

import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.DellFactory.DellKeyboardFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.DellFactory.DellMouseFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.HPFactory.HPKeyboardFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.HPFactory.HPMouseFactory;

public class DellProductFactory {
    public static AbstractFactory keyboardFactory(){
        return new DellKeyboardFactory();
    }

    public static AbstractFactory mouseFactory(){
        return new DellMouseFactory();
    }
}
