package com.loveoyh.common.swagger;

import javax.servlet.http.HttpServletRequest;

public class SwaggerUrlFilterHelper {
    public static String SWAGGER_SOURCE_KEY = "swaggerSource";
    public static String SWAGGER_SOURCE_VALUE = "1";
    public static String[] swaggerUrlArray = {"/v2/api-docs","/swagger-resources","/swagger-ui.html","/webjars/","/favicon.ico"};
    public static boolean containsSwaggerUrl(HttpServletRequest httpServletRequest){
        String requestUrl = httpServletRequest.getRequestURI();
        String swaggerSource = httpServletRequest.getHeader(SWAGGER_SOURCE_KEY);
        for(String swaggerUrl:swaggerUrlArray){
            if(requestUrl.contains(swaggerUrl) || SWAGGER_SOURCE_VALUE.equals(swaggerSource)){
                return true;
            }
        }
        return false;
    }


}