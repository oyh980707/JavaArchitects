package com.loveoyh.AdapterPattern;

/**
 * @Created by oyh.Jerry to 2020/02/28 14:10
 */
public class PowerAdapterTest {
	public static void main(String[] args) {
		AlternatingCurrent ac = new AlternatingCurrent();
		DirectCurrent dc = new PowerAdapter(ac);
		dc.outputDC();
	}
}
