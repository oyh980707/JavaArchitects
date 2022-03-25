package com.loveoyh.demo;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class TestBeanPostProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("beanName ["+beanName+"] execute TestBeanPostProcessor#postProcessBeforeInitialization.");
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("beanName ["+beanName+"] execute TestBeanPostProcessor#postProcessAfterInitialization.");
		return bean;
	}
}
