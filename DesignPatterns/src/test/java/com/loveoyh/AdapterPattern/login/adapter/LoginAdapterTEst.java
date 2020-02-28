package com.loveoyh.AdapterPattern.login.adapter;

/**
 * @Created by oyh.Jerry to 2020/02/28 15:24
 */
public class LoginAdapterTest {
	public static void main(String[] args) {
		IPassportForThird passportForThird = new PassportForThirdAdapter();
		passportForThird.loginForQQ("");
	}
}
