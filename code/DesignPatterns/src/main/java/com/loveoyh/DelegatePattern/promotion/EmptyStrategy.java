package com.loveoyh.DelegatePattern.promotion;

/**
 * 无优惠
 * Created by oyh.Jerry to 2020/02/26 21:02
 */
public class EmptyStrategy implements PromotionStrategy {
	@Override
	public void doPromotion() {
		System.out.println("无优惠");
	}
}
