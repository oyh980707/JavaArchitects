package com.loveoyh.DelegatePattern.promotion;

/**
 * 优惠卷促销
 * Created by oyh.Jerry to 2020/02/26 20:59
 */
public class CouponStrategy implements PromotionStrategy {
	@Override
	public void doPromotion() {
		System.out.println("优惠卷促销");
	}
}
