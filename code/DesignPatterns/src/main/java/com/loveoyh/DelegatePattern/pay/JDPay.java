package com.loveoyh.DelegatePattern.pay;

/**
 * @Created by oyh.Jerry to 2020/02/26 21:44
 */
public class JDPay extends Payment {
	@Override
	public String getName() {
		return "京东支付";
	}
	
	@Override
	protected double queryBalance(String uid) {
		return 200;
	}
}
