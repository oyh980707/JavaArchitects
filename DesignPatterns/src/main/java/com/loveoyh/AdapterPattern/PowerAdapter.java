package com.loveoyh.AdapterPattern;

/**
 * 电类型适配器类
 * @Created by oyh.Jerry to 2020/02/28 14:05
 */
public class PowerAdapter implements DirectCurrent{
	private AlternatingCurrent ac;
	public PowerAdapter(AlternatingCurrent ac){
		this.ac = ac;
	}
	
	@Override
	public int outputDC() {
		int adapterInput = ac.output();
		//做一系列转换
		//....
		int adapterOutput = adapterInput/44;
		System.out.println("使用PowerAdapter输如:"+adapterInput+"V，输出："+adapterOutput+"V");
		return adapterOutput;
	}
}
