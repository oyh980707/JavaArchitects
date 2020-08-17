package com.loveoyh.DelegatePattern.pay;

import java.util.HashMap;
import java.util.Map;

/**
 * @Created by oyh.Jerry to 2020/02/26 21:48
 */
public class PayStrategy {
	private static final Map<String,Payment> PAYMENT_STRATEGY_MAP = new HashMap<>();
	
	static {
		PAYMENT_STRATEGY_MAP.put("JD",new JDPay());
		PAYMENT_STRATEGY_MAP.put("WX",new WeChatPay());
		PAYMENT_STRATEGY_MAP.put("ALI",new AliPay());
	}
	
	public static Payment getPaymentStrategy(String key){
		Payment payment = PAYMENT_STRATEGY_MAP.get(key);
		return payment == null ? new AliPay() : payment;
	}
}
