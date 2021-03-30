package com.loveoyh.filter;

import com.loveoyh.filter.servlet.FilterChain;
import com.loveoyh.filter.servlet.ServletRequest;
import com.loveoyh.filter.servlet.ServletResponse;

/**
 * @Created by oyh.Jerry to 2021/03/30 18:35
 */
public class AuthFilter implements Filter{
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
		System.out.println("===========AuthFilter Filter Before==========");
		filterChain.doFilter(request, response, filterChain);
		System.out.println("===========AuthFilter Filter After===========");
	}
	
}
