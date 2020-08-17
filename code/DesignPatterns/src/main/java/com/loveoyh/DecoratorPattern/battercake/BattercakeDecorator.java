package com.loveoyh.DecoratorPattern.battercake;

/**
 * 基本装饰器
 * @Created by oyh.Jerry to 2020/02/29 20:58
 */
public class BattercakeDecorator extends Battercake{
	
	private Battercake battercake;
	
	public BattercakeDecorator(Battercake battercake){
		this.battercake = battercake;
	}
	
	@Override
	protected String getMsg() {
		return battercake.getMsg();
	}
	
	@Override
	protected int getPrice() {
		return battercake.getPrice();
	}
}
