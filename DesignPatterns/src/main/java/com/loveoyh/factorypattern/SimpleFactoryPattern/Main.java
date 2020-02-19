package com.loveoyh.factorypattern.SimpleFactoryPattern;

import com.loveoyh.factorypattern.SimpleFactoryPattern.entity.ICourse;
import com.loveoyh.factorypattern.SimpleFactoryPattern.entity.JavaCourse;
import com.loveoyh.factorypattern.SimpleFactoryPattern.factory.DefualtCourseFactory;

public class Main {
    public static void main(String[] args) {
        ICourse javaCourse = DefualtCourseFactory.create(JavaCourse.class);
        javaCourse.record();
    }
}
