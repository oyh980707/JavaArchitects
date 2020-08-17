package com.loveoyh.FactoryPattern.FactoryMethodPattern.factory;

import com.loveoyh.FactoryPattern.FactoryMethodPattern.entity.ICourse;
import com.loveoyh.FactoryPattern.FactoryMethodPattern.entity.JavaCourse;

/**
 * java课程工厂类
 */
public class JavaCourseFactory implements CourseFactory {
    public ICourse create() {
        return new JavaCourse();
    }
}
