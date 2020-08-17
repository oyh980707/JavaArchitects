package com.loveoyh.DecoratorPattern.battercake;

/**
 * 加鸡蛋装饰器
 * @Created by oyh.Jerry to 2020/02/29 21:09
 */
public class EggDecorator extends BattercakeDecorator {
	
	private Battercake battercake;
	
	public EggDecorator(Battercake battercake) {
		super(battercake);
		this.battercake = battercake;
	}
	
	@Override
	protected String getMsg() {
		return super.getMsg()+"+一个鸡蛋";
	}
	
	@Override
	protected int getPrice() {
		return super.getPrice()+1;
	}
}
