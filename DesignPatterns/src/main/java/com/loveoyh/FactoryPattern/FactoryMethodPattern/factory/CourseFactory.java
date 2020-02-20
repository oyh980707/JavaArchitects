package com.loveoyh.FactoryPattern.FactoryMethodPattern.factory;

import com.loveoyh.FactoryPattern.FactoryMethodPattern.entity.ICourse;

/**
 * 工厂接口
 */
public interface CourseFactory {
    public ICourse create();
}
