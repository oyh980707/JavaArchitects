package com.loveoyh.DelegatePattern;

/**
 * 测试员工
 * Created by oyh.Jerry to 2020/02/26 20:03
 */
public class IEmployeeB implements IEmployee{
	
	@Override
	public void doing(String command) {
		System.out.println("测试程序，执行"+command);
	}
}
