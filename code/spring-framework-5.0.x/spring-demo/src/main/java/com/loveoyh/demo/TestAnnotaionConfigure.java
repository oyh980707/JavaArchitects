package com.loveoyh.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestAnnotaionConfigure {

	@Bean
	public User user(){
		User user = new User();
		user.setId("1");
		user.setPassword("aaa");
		user.setEmail("bbb");
		user.setUsername("hgr");
		return user;
	}

}
