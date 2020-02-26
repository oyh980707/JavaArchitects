package com.loveoyh.DelegatePattern;

/**
 * 架构师
 * @Created by oyh.Jerry to 2020/02/26 19:58
 */

public class IEmployeeA implements IEmployee {
	@Override
	public void doing(String command) {
		System.out.println("完成软件的架构，执行"+command);
	}
}
