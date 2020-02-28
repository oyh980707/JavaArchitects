package com.loveoyh.AdapterPattern.login;

/**
 * @Created by oyh.Jerry to 2020/02/28 14:20
 */
public class SigninService {
	
	/**
	 * 注册
	 * @param username
	 * @param password
	 * @return
	 */
	public ResultMsg regist(String username,String password){
		return new ResultMsg(200,"注册成功",new Member());
	}
	
	public ResultMsg login(String username,String password){
		return null;
	}
	
}
