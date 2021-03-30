package com.loveoyh.filter.servlet;

import com.loveoyh.filter.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟 FilterChain
 * @Created by oyh.Jerry to 2021/03/30 18:31
 */
public class FilterChain implements Filter{
	
	private List<Filter> filters = new ArrayList<Filter>();
	private Integer index = 0;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain){
		if(index == filters.size()) {
			return;
		}
		Filter filter = filters.get(index);
		index++;
		filter.doFilter(request, response, filterChain);
	};
	
	public FilterChain addFilter(Filter filter){
		filters.add(filter);
		return this;
	}
	
}
