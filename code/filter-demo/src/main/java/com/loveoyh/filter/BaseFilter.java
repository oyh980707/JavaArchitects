package com.loveoyh.filter;

import com.loveoyh.filter.servlet.FilterChain;
import com.loveoyh.filter.servlet.ServletRequest;
import com.loveoyh.filter.servlet.ServletResponse;

/**
 * BaseFilter
 * @Created by oyh.Jerry to 2021/03/30 18:32
 */
public class BaseFilter implements Filter{
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
		System.out.println("==========Base Filter Before=========");
		filterChain.doFilter(request, response, filterChain);
		System.out.println("==========Base Filter After=========");
	}
	
}
