package com.loveoyh.AdapterPattern.login.adapter;

import com.loveoyh.AdapterPattern.login.ResultMsg;

/**
 * @Created by oyh.Jerry to 2020/02/28 15:08
 */
public class LoginForWechatAdapter implements LoginAdapter {
	@Override
	public boolean support(Object adapter) {
		return adapter instanceof LoginForWechatAdapter;
	}
	
	@Override
	public ResultMsg login(String id, Object adapter) {
		return null;
	}
}
