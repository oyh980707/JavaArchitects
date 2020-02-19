package com.loveoyh.factorypattern.AbstractFactoryPattern.factory.HPFactory;

import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Default.DefaultMouse;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.HP.HPMouse;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Mouse;

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
