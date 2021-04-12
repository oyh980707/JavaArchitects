package com.loveoyh.ChainOfResponsibilityPattern.ArrayImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @Created by oyh.Jerry to 2021/04/12 10:30
 */
public class HandlerChain {
	
	private List<IHandler> handlers = new ArrayList<>();
	
	public void addHandler(IHandler handler) {
		this.handlers.add(handler);
	}
	
	/** 可以中断消息继续向下面的处理器传递 */
	public boolean handle() {
		for(int i=0; i<handlers.size();i++){
			boolean handled = handlers.get(i).handle();
			if (handled && i != handlers.size()-1) {
				return false;
			}
		}
		return true;
	}
	
}
