package com.loveoyh.factorypattern.AbstractFactoryPattern.factory.DellFactory;

import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Dell.DellMouse;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.HP.HPMouse;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Keyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Mouse;

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
