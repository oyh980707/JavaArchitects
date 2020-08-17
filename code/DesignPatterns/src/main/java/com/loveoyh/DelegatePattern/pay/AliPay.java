package com.loveoyh.DelegatePattern.pay;

/**
 * @Created by oyh.Jerry to 2020/02/26 21:43
 */
public class AliPay extends Payment {
	@Override
	public String getName() {
		return "支付宝";
	}
	
	@Override
	protected double queryBalance(String uid) {
		return 100;
	}
}
