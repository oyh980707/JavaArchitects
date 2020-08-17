package com.loveoyh.DelegatePattern.promotion;

/**
 * 活动推销
 * Created by oyh.Jerry to 2020/02/26 21:03
 */
public class PromotionActivity {
	private PromotionStrategy promotionStrategy;
	
	public PromotionActivity(PromotionStrategy promotionStrategy){
		this.promotionStrategy = promotionStrategy;
	}
	
	public void execute(){
		promotionStrategy.doPromotion();
	}
}
