package com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Mouse;

/**
 * 抽象工厂
 */
public abstract class AbstractFactory {
    public abstract Mouse MouseFactory();
    public abstract Keyboard KeyboardFactory();
}
