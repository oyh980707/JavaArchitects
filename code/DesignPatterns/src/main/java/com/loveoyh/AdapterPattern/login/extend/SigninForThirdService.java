package com.loveoyh.AdapterPattern.login.extend;

import com.loveoyh.AdapterPattern.login.ResultMsg;
import com.loveoyh.AdapterPattern.login.SigninService;

/**
 * @Created by oyh.Jerry to 2020/02/28 14:29
 */
public class SigninForThirdService extends SigninService {
	public ResultMsg loginForQQ(String openId){
		//1、openId 是全局唯一，我们可以把它当做是一个用户名(加长)
		// 2、密码默认为 QQ_EMPTY
		// 3、注册（在原有系统里面创建一个用户）
		//4、调用原来的登录方法
		System.out.println("qq sign in!");
		return loginForRegist(openId,null);
	}
	public ResultMsg loginForWechat(String openId){
		return null;
	}
	public ResultMsg loginForToken(String token){
		//通过 token 拿到用户信息，然后再重新登陆了一次
		return null;
	}
	public ResultMsg loginForTelphone(String telphone,String code){
		return null;
	}
	public ResultMsg loginForRegist(String username,String password){
		super.regist(username,null);
		return super.login(username,null);
	}
}
