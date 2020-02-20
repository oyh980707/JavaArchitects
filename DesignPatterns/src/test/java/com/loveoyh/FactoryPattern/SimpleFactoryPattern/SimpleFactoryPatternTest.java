package com.loveoyh.FactoryPattern.SimpleFactoryPattern;

import com.loveoyh.FactoryPattern.SimpleFactoryPattern.entity.ICourse;
import com.loveoyh.FactoryPattern.SimpleFactoryPattern.entity.JavaCourse;
import com.loveoyh.FactoryPattern.SimpleFactoryPattern.factory.DefualtCourseFactory;

public class SimpleFactoryPatternTest {
    public static void main(String[] args) {
        ICourse javaCourse = DefualtCourseFactory.create(JavaCourse.class);
        javaCourse.record();
    }
}
