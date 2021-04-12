package com.loveoyh.ChainOfResponsibilityPattern.LinkedListImpl;

/**
 * 责任链采用链表的方式保存
 */
public class HandlerChain {
	
	private Handler head = null;
	
	private Handler tail = null;
	
	public void addHandler(Handler handler) {
		handler.setSuccessor(null);
		if (head == null) {
			head = handler;
			tail = handler;
		}
		tail.setSuccessor(handler);
		tail = handler;
	}
	
	public void handle() {
		if (head != null) {
			head.handle();
		}
	}
	
}