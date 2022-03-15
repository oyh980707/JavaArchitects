package com.loveoyh.common.filter;

import com.loveoyh.common.context.ActionContext;
import com.loveoyh.common.context.ActionContextSupport;
import com.loveoyh.common.entity.ResultVO;
import com.loveoyh.common.enums.ClientEnum;
import com.loveoyh.common.enums.ResultCodeEnum;
import com.loveoyh.common.log.LogStrategy;
import com.loveoyh.common.swagger.SwaggerUrlFilterHelper;
import com.loveoyh.common.utils.JSONUtils;
import com.loveoyh.common.utils.SignOauthUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 参数校验过滤器
 */

@Component
public class GlobalParamCheckFilter implements Filter {

    public static Logger logger = LoggerFactory.getLogger(GlobalParamCheckFilter.class);


    /**
     * 无需过滤的url请求
     * */
    @Value("#{'${gateway.no-need-filter.url}'.split(',')}")
    private List<String> NO_NEED_FILTER_URL = null;

    /**
     * 网关统一请求方式
     */
    private static final String REQUEST_METHOD = "POST";

    @Autowired
    private LogStrategy logStrategy;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestPath = request.getRequestURI();
        if(logger.isDebugEnabled()){
            logger.debug("GlobalParamCheckFilter doFilter start"+requestPath);
        }
        // 获取请求地址
        String requestURI = request.getRequestURI();
//        List<String> split = Splitter.on("/").trimResults().omitEmptyStrings().limit(2).splitToList(requestURI);

        //允许swagger页面或者通过swagger请求的接口
        if(SwaggerUrlFilterHelper.containsSwaggerUrl(request)){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 无需过滤的URL请求
        if (NO_NEED_FILTER_URL.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        ActionContext current = ActionContextSupport.getCurrent();
        logger.debug("gateway api filter aop拦截的url={},clientParams={} ",requestPath,current.getClientParams());
        if (!REQUEST_METHOD.equals(request.getMethod())) {
            logger.warn("GlobalParamCheckFilter requestMethod not supported,{},{}", requestPath,request.getMethod());
            current.setResponseResult(ResultCodeEnum.REQUEST_METHOD_ERROR_CODE.getMsg());
            response.getWriter().write(JSONUtils.toJSON(ResultVO.createError(ResultCodeEnum.REQUEST_METHOD_ERROR_CODE, "当前请求方式为: " + request.getMethod() + ", 请求地址为:" + requestURI + ", " + ResultCodeEnum.REQUEST_METHOD_ERROR_CODE.getMsg())));
            return;
        }

        // 判断是否有公共参数 param
        if (current == null) {
            logger.warn("GlobalParamCheckFilter ActionContext is null,{}", requestPath);
            current.setResponseResult(ResultCodeEnum.COMMON_PARAM_FAIL_CODE.getMsg());
            response.getWriter().write(JSONUtils.toJSON(ResultVO.createError(ResultCodeEnum.COMMON_PARAM_FAIL_CODE)));
            return;
        }


        // 判断公共参数是否上传 sign
        if (StringUtils.isBlank(current.getClientSign())) {
            logger.warn("GlobalParamCheckFilter clientSign is null,{},{}", requestPath,current.getClientParams());
            current.setResponseResult(ResultCodeEnum.SIGN_NO_PARAM_CODE.getMsg());
            response.getWriter().write(JSONUtils.toJSON(ResultVO.createError(ResultCodeEnum.SIGN_NO_PARAM_CODE)));
            return;
        }


        // 判断公共参数是否上传 client
        if (StringUtils.isBlank(current.getClient())) {
            logger.warn("GlobalParamCheckFilter client is null,{},{}", requestPath,current.getClientParams());
            current.setResponseResult(ResultCodeEnum.CLIENT_NO_PARAM_MESSAGE.getMsg());
            response.getWriter().write(JSONUtils.toJSON(ResultVO.createError(ResultCodeEnum.CLIENT_NO_PARAM_MESSAGE)));
            return;
        }


        List<String> allClient = ClientEnum.getAllClient();
        if (!allClient.contains(current.getClient())) {
            logger.warn("GlobalParamCheckFilter client not supported,{},{}", requestPath,current.getClientParams());
            current.setResponseResult(ResultCodeEnum.CLIENT_NO_EXISTS_MESSAGE.getMsg());
            response.getWriter().write(JSONUtils.toJSON(ResultVO.createError(ResultCodeEnum.CLIENT_NO_EXISTS_MESSAGE)));
            return;
        }

        boolean s = SignOauthUtil.checkSign(current.getClientSign(),current,
                ClientEnum.getClientSecretKey(current.getClient()));
//         比较方法签名, 成功则通过拦截器
        if (!s) {
            logger.warn("GlobalParamCheckFilter sign compare fail,{},{},{}", requestPath,current.getClientSign(),current.getServerSign());
            current.setResponseResult(ResultCodeEnum.SIGN_FAIL_ERROR_MESSAGE.getMsg());
            response.getWriter().write(JSONUtils.toJSON(ResultVO.createError(ResultCodeEnum.SIGN_FAIL_ERROR_MESSAGE)));
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }



    @Override
    public void destroy() {
    }
}
