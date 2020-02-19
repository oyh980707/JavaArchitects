package com.loveoyh.factorypattern.AbstractFactoryPattern.factory.DellFactory;

import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Dell.DellKeyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.HP.HPKeyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Mouse;

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
