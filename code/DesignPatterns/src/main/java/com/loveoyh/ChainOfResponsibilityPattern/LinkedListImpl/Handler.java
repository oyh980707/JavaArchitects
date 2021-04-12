package com.loveoyh.ChainOfResponsibilityPattern.LinkedListImpl;

/**
 * 责任链模式的第一种实现，采用链表的形式保存每个处理对象，如果返回false则继续向下执行，返回true则不向后进行执行。
 */
public abstract class Handler {
	
	protected Handler successor = null;
	
	public void setSuccessor(Handler successor) {
		this.successor = successor;
	}
	
	public final void handle() {
		boolean handled = doHandle();
		if (successor != null && !handled) {
			successor.handle();
		}
	}
	
	protected abstract boolean doHandle();
}
