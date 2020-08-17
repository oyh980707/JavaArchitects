package com.loveoyh.DelegatePattern.promotion;

/**
 * 团购优惠
 * Created by oyh.Jerry to 2020/02/26 21:02
 */
public class GroupBuyStrategy implements PromotionStrategy {
	@Override
	public void doPromotion() {
		System.out.println("团购优惠");
	}
}
