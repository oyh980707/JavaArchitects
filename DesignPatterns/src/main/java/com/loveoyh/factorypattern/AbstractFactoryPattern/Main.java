package com.loveoyh.factorypattern.AbstractFactoryPattern;

import com.loveoyh.factorypattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Default.DefaultKeyboard;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Default.DefaultProductFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Dell.DellProductFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.HP.HPProductFactory;
import com.loveoyh.factorypattern.AbstractFactoryPattern.product.Mouse;

public class Main {
    public static void main(String[] args) {
//        AbstractFactory mouseFactory = DefaultProductFactory.mouseFactory();
//        Mouse mouse = mouseFactory.MouseFactory();
//        mouse.sayHi();

//        AbstractFactory hpMouseFactory = HPProductFactory.mouseFactory();
//        Mouse hpMouse = hpMouseFactory.MouseFactory();
//        hpMouse.sayHi();

        AbstractFactory dellMouseFactory = DellProductFactory.mouseFactory();
        Mouse dellMouse = dellMouseFactory.MouseFactory();
        dellMouse.sayHi();
    }
}
