package com.loveoyh.demo;

import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class TestMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor {
	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		System.out.println("beanName ["+beanName+"] " + "execute TestMergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition");
	}
}
