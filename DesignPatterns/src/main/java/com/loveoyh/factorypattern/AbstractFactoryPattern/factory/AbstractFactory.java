package com.loveoyh.factorypattern.AbstractFactoryPattern.factory;

import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.defaultFactory.DefaultKeyboardFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.defaultFactory.DefaultMouseFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Mouse;

/**
 * 抽象工厂
 */
public abstract class AbstractFactory {
    public abstract Mouse MouseFactory();
    public abstract Keyboard KeyboardFactory();
}
