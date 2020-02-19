package com.loveoyh.factorypattern.FactoryMethodPattern.factory;

import com.loveoyh.factorypattern.FactoryMethodPattern.entity.ICourse;

/**
 * 工厂接口
 */
public interface CourseFactory {
    public ICourse create();
}
