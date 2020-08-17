package com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.HPFactory;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.HP.HPMouse;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Mouse;

/**
 * 默认鼠标工厂类
 */
public class HPMouseFactory extends AbstractFactory {
    public Mouse MouseFactory() {
        return new HPMouse();
    }

    public Keyboard KeyboardFactory() {
        return null;
    }
}
