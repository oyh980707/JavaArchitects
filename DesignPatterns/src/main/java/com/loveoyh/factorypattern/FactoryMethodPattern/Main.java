package com.loveoyh.factorypattern.FactoryMethodPattern;

import com.loveoyh.factorypattern.FactoryMethodPattern.entity.ICourse;
import com.loveoyh.factorypattern.FactoryMethodPattern.factory.CourseFactory;
import com.loveoyh.factorypattern.FactoryMethodPattern.factory.JavaCourseFactory;

public class Main {
    public static void main(String[] args) {
        CourseFactory factory = new JavaCourseFactory();
        ICourse javaCourse = factory.create();
        javaCourse.record();
    }
}
