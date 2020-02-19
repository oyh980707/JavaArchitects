package com.loveoyh.factorypattern.FactoryMethodPattern.factory;

import com.loveoyh.factorypattern.FactoryMethodPattern.entity.ICourse;
import com.loveoyh.factorypattern.FactoryMethodPattern.entity.JavaCourse;

/**
 * java课程工厂类
 */
public class JavaCourseFactory implements CourseFactory {
    public ICourse create() {
        return new JavaCourse();
    }
}
