package com.loveoyh.filter;

import com.loveoyh.filter.servlet.FilterChain;
import com.loveoyh.filter.servlet.ServletRequest;
import com.loveoyh.filter.servlet.ServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤器
 * @Created by oyh.Jerry to 2021/03/30 18:26
 */
public interface Filter {
	
	void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain);
	
}
