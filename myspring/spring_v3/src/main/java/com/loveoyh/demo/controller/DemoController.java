package com.loveoyh.demo.controller;

import com.loveoyh.demo.service.IDemoService;
import com.loveoyh.springframework.annotation.Autowired;
import com.loveoyh.springframework.annotation.Controller;
import com.loveoyh.springframework.annotation.RequestMapping;
import com.loveoyh.springframework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Created by oyh.Jerry to 2020/03/03 17:55
 */
@Controller
@RequestMapping("demo")
public class DemoController {
	@Autowired
	private IDemoService demoService;
	
	@RequestMapping("query")
	public void query(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name){
		String result = demoService.get(name);
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
