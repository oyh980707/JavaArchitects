package com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.defaultFactory;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Default.DefaultMouse;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Mouse;

/**
 * 默认鼠标工厂类
 */
public class DefaultMouseFactory extends AbstractFactory {
    public Mouse MouseFactory() {
        return new DefaultMouse();
    }

    public Keyboard KeyboardFactory() {
        return null;
    }
}
