package com.loveoyh.DecoratorPattern.battercake;

/**
 * 煎饼基本套餐
 * @Created by oyh.Jerry to 2020/02/29 20:57
 */
public class BaseBattercake extends Battercake {
	@Override
	protected String getMsg() {
		return "一个煎饼";
	}
	
	@Override
	protected int getPrice() {
		return 5;
	}
}
