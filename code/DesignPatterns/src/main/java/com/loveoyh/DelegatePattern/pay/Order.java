package com.loveoyh.DelegatePattern.pay;

/**
 * @Created by oyh.Jerry to 2020/02/26 21:54
 */
public class Order {
	private String uid;
	private String oid;
	private double amount;
	
	public Order(String uid,String oid,double amount){
		this.uid = uid;
		this.oid = oid;
		this.amount = amount;
	}
	
	public PayState pay(){
		return pay("ALI");
	}
	
	public PayState pay(String payKey){
		Payment payment = PayStrategy.getPaymentStrategy(payKey);
		System.out.println("欢迎使用" + payment.getName());
		System.out.println("本次交易金额为：" + amount + "，开始扣款...");
		return payment.pay(uid,amount);
		
	}
}
