package com.loveoyh.factorypattern.AbstractFactoryPattern.factory.HPFactory;

import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Default.DefaultKeyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.HP.HPKeyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Mouse;

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
