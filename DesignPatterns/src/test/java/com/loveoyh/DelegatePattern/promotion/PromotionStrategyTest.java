package com.loveoyh.DelegatePattern.promotion;

/**
 * DesignPatterns
 * Created by oyh.Jerry to 2020/02/26 21:06
 */
public class PromotionStrategyTest {
	public static void main(String[] args) {
		PromotionStrategy promotionStrategy = PromotionStrategyFactory.getPromotionStrategy("COUPON");
		PromotionActivity promotionActivity = new PromotionActivity(promotionStrategy);
		promotionActivity.execute();
		System.out.println("END.");
	}
}
