package com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.DellFactory;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Dell.DellKeyboard;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Mouse;

/**
 * 默认键盘工厂类
 */
public class DellKeyboardFactory extends AbstractFactory {

    public Mouse MouseFactory() {
        return null;
    }

    public Keyboard KeyboardFactory() {
        return new DellKeyboard();
    }
}
