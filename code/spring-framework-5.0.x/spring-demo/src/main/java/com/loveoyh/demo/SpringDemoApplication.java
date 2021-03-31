package com.loveoyh.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringDemoApplication {

	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("spring-demo.xml");
		User user = ac.getBean("user", User.class);
		System.out.println("username: "+user.getUsername());
		System.out.println("password: "+user.getPassword());
	}

}
