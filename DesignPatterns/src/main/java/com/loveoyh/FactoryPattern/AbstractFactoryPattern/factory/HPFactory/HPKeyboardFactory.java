package com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.HPFactory;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.HP.HPKeyboard;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Mouse;

/**
 * 默认键盘工厂类
 */
public class HPKeyboardFactory extends AbstractFactory {

    public Mouse MouseFactory() {
        return null;
    }

    public Keyboard KeyboardFactory() {
        return new HPKeyboard();
    }
}
