package com.loveoyh.DelegatePattern.pay;

/**
 * @Created by oyh.Jerry to 2020/02/26 21:45
 */
public class WeChatPay extends Payment {
	@Override
	public String getName() {
		return "微信支付";
	}
	
	@Override
	protected double queryBalance(String uid) {
		return 300;
	}
}
