package com.loveoyh.ChainOfResponsibilityPattern.ArrayImpl;

public class HandlerA implements IHandler {
	
	@Override
	public boolean handle() {
		boolean handled = true;
		System.out.println("=====HandlerA()");
		return handled;
	}
	
}