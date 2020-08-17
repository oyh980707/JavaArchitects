package com.loveoyh.ObserverPattern.guava;

import com.google.common.eventbus.Subscribe;

/**
 * @Created by oyh.Jerry to 2020/03/01 11:35
 */
public class GuavaEvent {
	
	@Subscribe
	public void subscript(String str){
		System.out.println("执行subscribe方法，传入的参数是："+str);
	}
	
}
