package com.loveoyh.factorypattern.AbstractFactoryPattern.factory.defaultFactory;

import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Default.DefaultKeyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Mouse;

/**
 * 默认键盘工厂类
 */
public class DefaultKeyboardFactory extends AbstractFactory {

    public Mouse MouseFactory() {
        return null;
    }

    public Keyboard KeyboardFactory() {
        return new DefaultKeyboard();
    }
}
