package com.loveoyh.demo.service.service.impl;

import com.loveoyh.demo.service.IDemoService;
import com.loveoyh.springframework.annotation.Service;

/**
 * @Created by oyh.Jerry to 2020/03/03 17:54
 */
@Service
public class DemoService implements IDemoService {
	
	public String get(String name) {
		return "My name is "+ name;
	}
	
}
