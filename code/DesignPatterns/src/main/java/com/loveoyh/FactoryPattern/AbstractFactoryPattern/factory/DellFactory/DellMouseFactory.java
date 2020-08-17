package com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.DellFactory;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Dell.DellMouse;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Mouse;

/**
 * 默认鼠标工厂类
 */
public class DellMouseFactory extends AbstractFactory {
    public Mouse MouseFactory() {
        return new DellMouse();
    }

    public Keyboard KeyboardFactory() {
        return null;
    }
}
