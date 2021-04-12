package com.loveoyh.ChainOfResponsibilityPattern.ArrayImpl;

public class HandlerB implements IHandler {
	
	@Override
	public boolean handle() {
		boolean handled = false;
		System.out.println("=====HandlerB()");
		return handled;
	}
	
}