package com.loveoyh.common.context;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求的数据上下文
 */
@Data
public class ActionContext {

	/**
	 * data业务参数
	 */
	private String data;

	/**
	 * 登录后颁发的令牌token
	 */
	private String token;

	/**
	 * 输入参数签名
	 */
	private String clientSign;
	/**
	 * 服务签名结果
	 */
	private String serverSign;
	/**
	 * 客户端请求所有参数
	 */
	private String clientParams;

	/**
	 * 客户端
	 */
	private String client;

	/**
	 * 版本号
	 */
	private String version;
	//时间戳
//	private long timestamp;
	private String urlMatcher;
	private String method;
	private String ip;
	private String server;
	private String url;
	private String query;
	private String refer;
	private String userAgent;
	private long requestTime;
	private long responseTime;
	//经过api网关及服务总耗时
	private long timeCost;
	//调用服务耗时
	private long serviceTimeConsuming;
	private String responseResult;
	/**
	 * 异常信息
	 */
	private String errorMsg;

	private Map<String, String> params = new HashMap<String, String>();
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> cookies = new HashMap<String, String>();
	private Map<String, Object> contextMap = new HashMap<String, Object>();
	//用户登录态信息
	private CommonRequestModel model;

	public ActionContext() {
	}
}
