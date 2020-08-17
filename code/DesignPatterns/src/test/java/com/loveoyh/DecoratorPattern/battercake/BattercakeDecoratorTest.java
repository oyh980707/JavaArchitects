package com.loveoyh.DecoratorPattern.battercake;

/**
 * @Created by oyh.Jerry to 2020/02/29 21:11
 */
public class BattercakeDecoratorTest {
	
	public static void main(String[] args) {
		BaseBattercake baseBattercake = new BaseBattercake();
		EggDecorator eggDecorator = new EggDecorator(baseBattercake);
		System.out.println(eggDecorator.getMsg()+"，价格"+eggDecorator.getPrice());
		SausageDecorator sausageDecorator = new SausageDecorator(eggDecorator);
		System.out.println(sausageDecorator.getMsg()+"，价格"+sausageDecorator.getPrice());
	}
}
