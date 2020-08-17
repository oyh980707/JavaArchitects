package com.loveoyh.DelegatePattern.promotion;

import java.util.HashMap;
import java.util.Map;

/**
 * 策略工厂类
 * @Created by oyh.Jerry to 2020/02/26 21:23
 */
public class PromotionStrategyFactory {
	private static final Map<String,PromotionStrategy> PROMOTION_STRATEGY_MAP = new HashMap<>();
	
	static {
		PROMOTION_STRATEGY_MAP.put(PromotionKey.CASHBACK,new CashbackStrategy());
		PROMOTION_STRATEGY_MAP.put(PromotionKey.COUPON,new CouponStrategy());
		PROMOTION_STRATEGY_MAP.put(PromotionKey.GROUPBUY,new GroupBuyStrategy());
	}
	
	public static PromotionStrategy getPromotionStrategy(String key){
		PromotionStrategy promotionStrategy = PROMOTION_STRATEGY_MAP.get(key);
		return promotionStrategy == null ? new EmptyStrategy() : promotionStrategy;
	}
	
	private interface PromotionKey{
		String COUPON = "COUPON";
		String CASHBACK = "CASHBACK";
		String GROUPBUY = "GROUPBUY";
	}
}
