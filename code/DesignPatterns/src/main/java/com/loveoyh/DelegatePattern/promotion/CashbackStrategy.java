package com.loveoyh.DelegatePattern.promotion;

/**
 * 返现促销
 * Created by oyh.Jerry to 2020/02/26 21:00
 */
public class CashbackStrategy implements PromotionStrategy {
	@Override
	public void doPromotion() {
		System.out.println("返现促销");
	}
}
