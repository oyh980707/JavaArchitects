package com.loveoyh;

import com.loveoyh.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet(name = "BaseServlet", urlPatterns = "/*")
public class BaseServlet extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(BaseServlet.class);
    public static final ConcurrentHashMap<String, Method> servletMethodMap = new ConcurrentHashMap();
    public static final ConcurrentHashMap<String, Object> beanMap = new ConcurrentHashMap();
    private static final String REQUEST = "javax.servlet.http.HttpServletRequest";
    private static final String RESPONSE = "javax.servlet.http.HttpServletResponse";

    @Override
    public void init() throws ServletException {
        //加载异常处理
    }

    /**
     * GET请求
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String uri = request.getRequestURI();

        response.getWriter().print("hello");

    }

    /**
     * POST请求
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Method method = servletMethodMap.get(request.getPathInfo());    // 获取请求方法
            if (method == null) {
                response.setStatus(404);
                response("没有该访问地址", response);
                return;
            }

            Object resultObj = invokeMethod(method, request, response);
            response(resultObj, response);

        } catch (InvocationTargetException e) {
            logger.error("e:{}", e);
        } catch (Exception e) {
            logger.error("e:{}", e);
        }
    }


    /**
     * 调用controller制定的requestMapping方法
     *
     * @param method   反射方法
     * @param request  request对象
     * @param response response对象
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IOException
     */
    private Object invokeMethod(Method method, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        Object o = beanMap.get(method.getDeclaringClass().getName());   // 获取方法所属类对象
        Class<?>[] parameterTypes = method.getParameterTypes();         // 获取方法参数
        if (parameterTypes.length == 0) {
            // 无参数方法调用
            return method.invoke(o, null);
        } else {
            // 存在参数方法调用
            List<Object> args = new ArrayList<>();                       // 初始化参数集合
            for (int i = 0; i < parameterTypes.length; i++) {
                String paramClassName = parameterTypes[i].getName();
                if (REQUEST.equals(paramClassName)) {
                    args.add(request);
                } else if (RESPONSE.equals(paramClassName)) {
                    args.add(response);
                } else {

                    Object object = JsonUtil.jsonToBean(requstBodyToString(request), Class.forName(paramClassName));
                    args.add(object);
                }
            }
            for (Object obj:args) {
                if(obj instanceof HttpServletRequest){
                    continue;
                }
                logger.info("[统一  入参] param:{}",JsonUtil.getJsonString(obj));
            }
            Object object =  method.invoke(o, args.toArray());
            logger.info("[统一  出参] param:{}",JsonUtil.getJsonString(object));
            return object;

        }
    }

    public static void response(Object result, HttpServletResponse response) throws IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.addHeader("Access-Control-Max-Age", "1800");//30 min
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(JsonUtil.getJsonStringForFE(result));
    }

    /**
     * 获取request中body信息
     */
    public static String requstBodyToString(HttpServletRequest request) throws IOException {
        try (
                InputStream is = request.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ) {
            int i;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            String result = new String(baos.toByteArray(), "UTF-8");
            return result;
        }
    }
}
