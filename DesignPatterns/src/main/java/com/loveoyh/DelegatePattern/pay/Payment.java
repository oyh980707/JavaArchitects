package com.loveoyh.DelegatePattern.pay;

/**
 *
 * @Created by oyh.Jerry to 2020/02/26 21:37
 */
public abstract class Payment {
	public abstract String getName();
	
	protected abstract double queryBalance(String uid);
	
	public PayState pay(String uid,double amount){
		if(queryBalance(uid) < amount){
			return new PayState(500,"支付失败","余额不足");
		}
		return new PayState(200,"支付成功","支付金额：" + amount);
	}
}
