package com.loveoyh.FactoryPattern.SimpleFactoryPattern.factory;

import com.loveoyh.FactoryPattern.SimpleFactoryPattern.entity.ICourse;

/**
 * 简单工厂类
 */
public class DefualtCourseFactory {
    public static ICourse create(Class className){
        try {
            if (className != null && !"".equals(className)){
                return (ICourse) className.newInstance();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
}
