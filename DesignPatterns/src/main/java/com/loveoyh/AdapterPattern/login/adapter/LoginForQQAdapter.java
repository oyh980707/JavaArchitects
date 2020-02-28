package com.loveoyh.AdapterPattern.login.adapter;

import com.loveoyh.AdapterPattern.login.ResultMsg;

/**
 * @Created by oyh.Jerry to 2020/02/28 14:40
 */
public class LoginForQQAdapter implements LoginAdapter {
	@Override
	public boolean support(Object adapter) {
		return adapter instanceof LoginForQQAdapter;
	}
	
	@Override
	public ResultMsg login(String id, Object adapter) {
		return null;
	}
}
