package com.loveoyh.FactoryPattern.FactoryMethodPattern.factory;

import com.loveoyh.FactoryPattern.FactoryMethodPattern.entity.ICourse;
import com.loveoyh.FactoryPattern.FactoryMethodPattern.entity.PythonCourse;

/**
 * python课程工厂类
 */
public class PythonCourseFactory implements CourseFactory {
    public ICourse create() {
        return new PythonCourse();
    }
}
