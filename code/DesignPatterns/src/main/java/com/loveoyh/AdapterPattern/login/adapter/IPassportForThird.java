package com.loveoyh.AdapterPattern.login.adapter;

import com.loveoyh.AdapterPattern.login.ResultMsg;

/**
 * @Created by oyh.Jerry to 2020/02/28 15:11
 */
public interface IPassportForThird {
	/**
	 * QQ登 录
	 * @param id * @return
	 */
	ResultMsg loginForQQ(String id);
	/**
	 * 微 信 登 录
	 * @param id
	 * @return
	 */
	ResultMsg loginForWechat(String id);
	/**
	 * 记 住 登 录 状 态 后 自 动 登 录
	 * @param token
	 * @return
	 */
	 ResultMsg loginForToken(String token);
	/**
	 * 手 机 号 登 录
	 * @param telphone
	 * @param code
	 * @return
	 */
	ResultMsg loginForTelphone(String telphone, String code);
	/**
	 * 注 册 后 自 动 登 录
	 * @param username
	 * @param passport
	 * @return
	 */
	ResultMsg loginForRegist(String username, String passport);
	
}
