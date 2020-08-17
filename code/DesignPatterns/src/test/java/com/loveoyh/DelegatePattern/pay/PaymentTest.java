package com.loveoyh.DelegatePattern.pay;

/**
 * @Created by oyh.Jerry to 2020/02/26 21:45
 */
public class PaymentTest {
	public static void main(String[] args) {
		Order order = new Order("1","1",200);
		PayState payState = order.pay("WX");
		System.out.println(payState);
	}
}
