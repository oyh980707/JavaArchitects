package com.loveoyh.common.context;

import com.loveoyh.common.entity.RequestParams;
import com.loveoyh.common.enums.ClientEnum;
import com.loveoyh.common.utils.IpUtil;
import com.loveoyh.common.utils.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 请求数据上下文工具类
 */
public class ActionContextSupport {
	public static final Logger log = LoggerFactory.getLogger(ActionContextSupport.class);
	public static final String ACTION_CONTEXT_KEY = "_actionContext";
	private static ThreadLocal<ActionContext> actionThreadLocal = new ThreadLocal<ActionContext>();

	private ActionContextSupport() {
	}

//	public static ActionContext bind(HttpServletRequest request) {
//		return bind(request, ClientEnum.WEB_CLIENT.getClient());
//	}

	public static ActionContext bind(HttpServletRequest request, String resultJson ) {
		RequestParams params = null;
		if(StringUtils.isBlank(resultJson)){
			params = new RequestParams();
		}else{
			params = JSONUtils.parse2Object(resultJson, RequestParams.class);
		}
		String client = params.getClient();
		String sign = params.getSign();
		String data = StringUtils.isNoneBlank(params.getData())?params.getData():"{}";
		String token = params.getToken();
		String version = params.getVersion();
		ActionContext context = new ActionContext();
		context.setClientSign(sign);
		context.setData(data);
        context.setClientParams(resultJson);
		context.setToken(token);
		context.setClient(client);
		context.setVersion(version);
		String url=request.getServletPath();
		context.setUrlMatcher(url);
		context.setMethod(request.getMethod());
		context.setIp(IpUtil.getRealIp(request));
		context.setServer(request.getServerName());
		context.setUrl(request.getRequestURL().toString());
		context.setQuery(StringUtils.isBlank(request.getQueryString()) ? "" : request.getQueryString());

		Enumeration<String> headerEnum = request.getHeaderNames();
		while (headerEnum.hasMoreElements()) {
			String headerName = headerEnum.nextElement();
			if (headerName.equals("cookie")) {
				continue;
			}
			String headerValue = request.getHeader(headerName);
			context.getHeaders().put(headerName.toLowerCase(), headerValue);
		}
		Cookie[] cookieArr = request.getCookies();
		if (cookieArr != null && cookieArr.length > 0) {
			for (Cookie cookie : cookieArr) {
				context.getCookies().put(cookie.getName().toLowerCase(), cookie.getValue());
			}
		}
		Enumeration<String> paramEnum = request.getParameterNames();
		while (paramEnum.hasMoreElements()) {
			String paramName = paramEnum.nextElement();
			String paramValue = request.getParameter(paramName);
			context.getParams().put(paramName.toLowerCase(), paramValue);
		}

		context.setRefer(context.getHeaders().get("referer"));
		context.setUserAgent(context.getHeaders().get("user-agent"));

		context.setRequestTime(System.currentTimeMillis());

		if (ClientEnum.IOS_CLIENT.getClient().equals(client)  || ClientEnum.ANDROID_CLIENT.getClient().equals(client)
			|| ClientEnum.C_NET_CLIENT.getClient().equals(client)) {
			if(StringUtils.isNotBlank(context.getUserAgent())){
				context.setUserAgent(context.getHeaders().get("ua"));
			}

			if (client != null) {
//				AppActionContext appContext = new AppActionContext();
//				appContext.setAppOs(client.getOs());
//				appContext.setAppVersion(params.getVersion());
//				appContext.setAppImei(client.getImei());
//				appContext.setAppChannelId(client.getChannelid());
//				appContext.setAppUid(client.getUid());
//				appContext.setAppLat(client.getLat());
//				appContext.setAppLon(client.getLon());
//				appContext.setAppIp(client.getIp());
//				appContext.setAppUa(client.getUa());
//				appContext.setAppMac(params.getMac());
//				appContext.setAppUniqueId(client.getUniqueid());
//				context.setAppContext(appContext);
			}
		}
		actionThreadLocal.set(context);
		return context;
	}

	public static ActionContext getCurrent() {

		ActionContext context = actionThreadLocal.get();
		if (context == null) {
			throw new IllegalStateException("ActionContext is null");
		} else {
			return context;
		}
	}

	public static void unset() {
		actionThreadLocal.remove();
	}

}