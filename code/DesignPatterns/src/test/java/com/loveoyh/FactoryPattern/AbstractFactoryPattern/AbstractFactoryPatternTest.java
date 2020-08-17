package com.loveoyh.FactoryPattern.AbstractFactoryPattern;

import com.loveoyh.FactoryPattern.AbstractFactoryPattern.factory.AbstractFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Dell.DellProductFactory;
import com.loveoyh.FactoryPattern.AbstractFactoryPattern.product.Mouse;

public class AbstractFactoryPatternTest {
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
