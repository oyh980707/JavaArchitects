package com.loveoyh.factorypattern.FactoryMethodPattern.factory;

import com.loveoyh.factorypattern.FactoryMethodPattern.entity.ICourse;
import com.loveoyh.factorypattern.FactoryMethodPattern.entity.PythonCourse;

/**
 * python课程工厂类
 */
public class PythonCourseFactory implements CourseFactory {
    public ICourse create() {
        return new PythonCourse();
    }
}
