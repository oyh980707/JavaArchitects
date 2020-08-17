package com.loveoyh.AdapterPattern;

/**
 * 交流电类
 * @Created by oyh.Jerry to 2020/02/28 14:02
 */
public class AlternatingCurrent {
	public int output(){
		int output = 220;
		System.out.println("输出交流电"+output+"V");
		return output;
	}
}
