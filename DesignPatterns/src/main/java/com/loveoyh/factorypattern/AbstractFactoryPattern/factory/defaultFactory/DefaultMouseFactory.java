package com.loveoyh.factorypattern.AbstractFactoryPattern.factory.defaultFactory;

import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Default.DefaultKeyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Default.DefaultMouse;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Mouse;

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
