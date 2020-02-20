package com.loveoyh.FactoryPattern.FactoryMethodPattern;

import com.loveoyh.FactoryPattern.FactoryMethodPattern.entity.ICourse;
import com.loveoyh.FactoryPattern.FactoryMethodPattern.factory.CourseFactory;
import com.loveoyh.FactoryPattern.FactoryMethodPattern.factory.JavaCourseFactory;

public class FactoryMethodPatternTest {
    public static void main(String[] args) {
        CourseFactory factory = new JavaCourseFactory();
        ICourse javaCourse = factory.create();
        javaCourse.record();
    }
}
