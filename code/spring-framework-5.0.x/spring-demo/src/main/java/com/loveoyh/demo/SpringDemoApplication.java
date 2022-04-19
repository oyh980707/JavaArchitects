package com.loveoyh.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringDemoApplication {

	public static void main(String[] args) {
		// 构造Resource 统一资源 Resource 接口可以对所有资源文件进行统一处理
//		ClassPathResource resource = new ClassPathResource("spring-demo.xml");

//		ApplicationContext ac = new ClassPathXmlApplicationContext("spring-demo.xml");
//		AnnotationConfigApplicationContext aac = new AnnotationConfigApplicationContext("com.loveoyh.demo");
//		User user = aac.getBean("user", User.class);
//		System.out.println(user);
//		User user = ac.getBean("user", User.class);
//		System.out.println("username: "+user.getUsername());
//		System.out.println("password: "+user.getPassword());
//		Car car = ac.getBean("car", Car.class);
//		System.out.println("getBrand: "+car.getBrand());
//		System.out.println("getMaxSpeed: "+car.getMaxSpeed());
//		System.out.println("getPrice: "+car.getPrice());
//		HelloAware helloAware = ac.getBean("helloAware", HelloAware.class);
//		helloAware.test();

	}

}
