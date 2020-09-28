package com.loveoyh.BuilderPattern;

/**
 * @Created by oyh.Jerry to 2020/09/28 10:28
 */
public class Demo {
	private final String str;
	
	private Demo(String str) {
		this.str = str;
	}
	
	private Demo(DefaultBuilder builder){
		this.str = builder.str;
	}
	
	public static class DefaultBuilder {
		private String str;
		
		public Demo build(){
			return new Demo(this);
		}
		
		public DefaultBuilder setStr(String str){
			this.str = str;
			return this;
		}
	}
	
}
