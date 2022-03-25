package com.loveoyh.demo;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.beans.PropertyDescriptor;

public class TestInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		System.out.println("beanName ["+beanName+"] "+"execute TestInstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation.");
		return null;
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		System.out.println("beanName ["+beanName+"] "+"execute TestInstantiationAwareBeanPostProcessor#postProcessAfterInstantiation.");
		return true;
	}

	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
		System.out.println("beanName ["+beanName+"] "+"execute TestInstantiationAwareBeanPostProcessor#postProcessPropertyValues.");
		return pvs;
	}
}
