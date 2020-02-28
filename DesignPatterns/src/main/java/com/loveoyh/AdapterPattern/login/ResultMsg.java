package com.loveoyh.AdapterPattern.login;

/**
 * 响应实体类
 * @Created by oyh.Jerry to 2020/02/28 14:18
 */
public class ResultMsg {
	private int code;
	private String msg;
	private Object data;
	
	public ResultMsg() {
	}
	
	public ResultMsg(int code, String msg, Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "ResultMsg{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
}
