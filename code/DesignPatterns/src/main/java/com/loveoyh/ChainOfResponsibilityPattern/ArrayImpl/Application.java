package com.loveoyh.ChainOfResponsibilityPattern.ArrayImpl;

public class Application {
	
	public static void main(String[] args) {
		HandlerChain chain = new HandlerChain();
		chain.addHandler(new HandlerA());
		chain.addHandler(new HandlerB());
		boolean handle = chain.handle();
		// 根据是否所有处理器处理过来判断是否进行下面的内容
		if(handle){
			System.out.println("=====handler success!");
		}else{
			System.out.println("=====handler fail!");
		}
	}
	
}