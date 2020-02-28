package com.loveoyh.AdapterPattern.login.adapter;

import com.loveoyh.AdapterPattern.login.ResultMsg;

/**
 * 登录适配器接口
 * @Created by oyh.Jerry to 2020/02/28 14:39
 */
public interface LoginAdapter {
	boolean support(Object adapter);
	ResultMsg login(String id,Object adapter);
}
