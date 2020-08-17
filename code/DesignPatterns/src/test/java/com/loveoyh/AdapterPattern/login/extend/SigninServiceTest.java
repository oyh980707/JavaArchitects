package com.loveoyh.AdapterPattern.login.extend;

import com.loveoyh.AdapterPattern.login.extend.SigninForThirdService;

/**
 * @Created by oyh.Jerry to 2020/02/28 14:32
 */
public class SigninServiceTest {
	public static void main(String[] args) {
		SigninForThirdService signin = new SigninForThirdService();
		// 不改变原来的代码，也要能够兼容新的需求
		// 还可以再加一层策略模式
		signin.loginForQQ("qq");
	}
}
