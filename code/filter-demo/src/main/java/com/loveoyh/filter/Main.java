package com.loveoyh.filter;

import com.loveoyh.filter.servlet.FilterChain;
import com.loveoyh.filter.servlet.ServletRequest;
import com.loveoyh.filter.servlet.ServletResponse;

/**
 * @Created by oyh.Jerry to 2021/03/30 18:33
 */
public class Main {
	
	public static void main(String[] args) {
		ServletRequest request = new ServletRequest();
		request.setRequestStr("requestStr");
		ServletResponse response = new ServletResponse();
		response.setResponseStr("responseStr");
		
		FilterChain fc = new FilterChain();
		fc.addFilter(new BaseFilter()).addFilter(new AuthFilter());
		fc.doFilter(request, response, fc);
	}
	
}
