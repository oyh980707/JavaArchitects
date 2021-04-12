package com.loveoyh.ChainOfResponsibilityPattern.LinkedListImpl;

public class Application {
	
	public static void main(String[] args) {
		HandlerChain chain = new HandlerChain();
		chain.addHandler(new HandlerA());
		chain.addHandler(new HandlerB());
		chain.handle();
	}
	
}