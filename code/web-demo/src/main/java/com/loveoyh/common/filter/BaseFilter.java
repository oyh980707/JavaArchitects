package com.loveoyh.common.filter;

import com.loveoyh.common.context.ActionContext;
import com.loveoyh.common.context.ActionContextSupport;
import com.loveoyh.common.entity.ResultVO;
import com.loveoyh.common.log.LogStrategy;
import com.loveoyh.common.swagger.SwaggerUrlFilterHelper;
import com.loveoyh.common.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 第一个基础过滤器，用于解决跨域，参数封装及转换，日志记录
 */
@Component
@Order(1)
public class BaseFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(BaseFilter.class);

    @Resource
    private LogStrategy logStrategy;
    /**
     * 获取启动环境
     * */
    @Value("${gateway.env}")
    public String env = null;

    /**
     * 启动线上环境
     * */
    private static final String PROD_ENV = "prod";
    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException {
        try {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String requestPath = request.getRequestURI();
            // 解决跨域问题
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=UTF-8");
            //网关访问URL
            String requestMethod = request.getMethod();
            logger.info("BaseFilter doFilter start"+request.getRequestURI()+"|"+requestMethod);
            if (HttpMethod.OPTIONS.name().equals(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
                return ;
            }

            if("/".equals(requestPath)){
                response.getWriter().write(JSONUtils.toJSON(ResultVO.createError()));
                return;
            }

            //线上环境，拦截swagger请求规则的request，后续filter,interceptor不需要再校验是否是生产环境
            if(SwaggerUrlFilterHelper.containsSwaggerUrl(request)){
                if(PROD_ENV.equals(env)){
                    response.getWriter().write(JSONUtils.toJSON(ResultVO.createError()));
                    return;
                }
                chain.doFilter(servletRequest, servletResponse);
                return;
            }
            //重新构建request
            ParameterCheckServletRequestWrapper myWrapper = new ParameterCheckServletRequestWrapper((HttpServletRequest) servletRequest);
            String paramsJson = myWrapper.getAllParams();
            //请求数据绑定到Threadlocal上下文
            ActionContextSupport.bind(request,paramsJson);
            chain.doFilter(myWrapper, servletResponse);

            //执行业务逻辑后
            ActionContext current = ActionContextSupport.getCurrent();
            logger.info("BaseFilter access log:" + JSONUtils.toJSON(current));
            current.setResponseTime(System.currentTimeMillis());
            current.setTimeCost(current.getResponseTime()-current.getRequestTime());
            logStrategy.doLog(current);
        } catch (Exception e) {
            logger.error("BaseFilter access log error", e);
            ActionContext current = ActionContextSupport.getCurrent();
            current.setResponseTime(System.currentTimeMillis());
            current.setTimeCost(current.getResponseTime()-current.getRequestTime());
            logStrategy.doLog(current);
            servletResponse.getWriter().write(JSONUtils.toJSON(ResultVO.createError("API异常，异常信息：" + e.getMessage())));
        } finally {
            ActionContextSupport.unset();
        }
    }

    @Override
    public void destroy() {

    }


}
