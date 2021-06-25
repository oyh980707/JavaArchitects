package com.loveoyh.annotation;

import com.loveoyh.BaseServlet;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

public class ControllerAnnotationHelper {

    /**
     * 扫描指定包下的所有类
     * @param packagePath
     * @param childPackage
     */
    public static void getAnnotationClass(String packagePath,boolean childPackage){
        Reflections f = new Reflections(packagePath);
        Set<Class<?>> classes = f.getTypesAnnotatedWith(Controller.class);
        for (Class<?> clazz : classes) {
            try {
                //包含controller注解
                if(clazz.isAnnotationPresent(Controller.class)){
                    String url = "";
                    if(clazz.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping requestMapping = (RequestMapping)clazz.getAnnotation(RequestMapping.class);
                        url = requestMapping.value();
                    }
                    getAnnotationMethod(clazz, url);

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 扫描指定类下的所有方法
     * @param clazz
     * @param path
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void getAnnotationMethod(Class clazz,String path) throws IllegalAccessException, InstantiationException {
        BaseServlet.beanMap.put(clazz.getName(),clazz.newInstance());
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String url = path;
            if(method.isAnnotationPresent(RequestMapping.class)){
                RequestMapping requestMapping = (RequestMapping)method.getAnnotation(RequestMapping.class);
                url += requestMapping.value();
                if(BaseServlet.servletMethodMap.containsKey(url)){
                    continue;
                }
                BaseServlet.servletMethodMap.put(url,method);
            }
        }
    }
}
