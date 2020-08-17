package com.loveoyh.DecoratorPattern.battercake;

/**
 * 加火腿装饰器
 * @Created by oyh.Jerry to 2020/02/29 21:04
 */
public class SausageDecorator extends BattercakeDecorator{
	private Battercake battercake;
	public SausageDecorator(Battercake battercake) {
		super(battercake);
		this.battercake = battercake;
	}
	
	@Override
	protected String getMsg() {
		return super.getMsg()+"+一个火腿";
	}
	
	@Override
	protected int getPrice() {
		return super.getPrice()+1;
	}
}
