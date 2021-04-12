package com.loveoyh.ChainOfResponsibilityPattern.LinkedListImpl;

public class HandlerB extends Handler {
	
	@Override
	protected boolean doHandle() {
		boolean handled = false;
		System.out.println("=====HandlerB()");
		return handled;
	}
	
}