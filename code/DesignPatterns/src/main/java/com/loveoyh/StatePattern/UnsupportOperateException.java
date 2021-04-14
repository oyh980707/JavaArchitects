package com.loveoyh.StatePattern;

/**
 * @Created by oyh.Jerry to 2021/04/14 15:21
 */
public class UnsupportOperateException extends RuntimeException {
	public UnsupportOperateException() {
		super();
	}
	
	public UnsupportOperateException(String message) {
		super(message);
	}
	
	public UnsupportOperateException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public UnsupportOperateException(Throwable cause) {
		super(cause);
	}
	
	protected UnsupportOperateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
