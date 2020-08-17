package com.loveoyh.DelegatePattern.pay;

/**
 * @Created by oyh.Jerry to 2020/02/26 21:39
 */
public class PayState {
	private int stateCode;
	private String msg;
	private Object data;
	
	public PayState(int stateCode, String msg, Object data) {
		this.stateCode = stateCode;
		this.msg = msg;
		this.data = data;
	}
	
	public int getStateCode() {
		return stateCode;
	}
	
	public void setStateCode(int stateCode) {
		this.stateCode = stateCode;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "PayState{" +
				"stateCode=" + stateCode +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
}
