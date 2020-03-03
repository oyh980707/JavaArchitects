package com.loveoyh.springframework.web.servlet;

import com.loveoyh.springframework.annotation.Autowired;
import com.loveoyh.springframework.annotation.Controller;
import com.loveoyh.springframework.annotation.RequestMapping;
import com.loveoyh.springframework.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Created by oyh.Jerry to 2020/03/03 17:39
 */
public class DispatcherServlet extends HttpServlet {
	
	private Map<String, Object> mapping = new HashMap<String, Object>();
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
		System.out.println("doGet");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			this.doDispatch(req,resp);
		} catch (Exception e) {
			resp.getWriter().write(e.getMessage());
		}
	}
	
	private void doDispatch(HttpServletRequest req,HttpServletResponse res) throws Exception{
		String url = req.getRequestURI();
		String contextPath = req.getContextPath();
		url = url.replace(contextPath,"").replaceAll("/+","/");
		if(!this.mapping.containsKey(url)) {
			res.getWriter().write("404 Not Found!");
		}
		Method method = (Method) this.mapping.get(url);
		Map<String, String[]> params = req.getParameterMap();
		method.invoke(this.mapping.get(method.getDeclaringClass().getName()),new Object[]{req,res,params.get("name")[0]});
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		InputStream in = null;
		try{
			Properties configContext = new Properties();
			in = this.getClass().getClassLoader().getResourceAsStream(config.getInitParameter("contextConfigLocation"));
			configContext.load(in);
			String scanPackage = configContext.getProperty("scanPackage");
			doScanner(scanPackage);
			for(String className : mapping.keySet()){
				if(!className.contains(".")) continue;
				Class<?> clazz = Class.forName(className);
				if(clazz.isAnnotationPresent(Controller.class)){
					mapping.put(className,clazz.newInstance());
					String baseUrl = "";
					if(clazz.isAnnotationPresent(RequestMapping.class)){
						RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
						baseUrl = requestMapping.value();
					}
					Method[] methods = clazz.getMethods();
					for(Method method : methods){
						if(!method.isAnnotationPresent(RequestMapping.class)) continue;
						RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
						String url = (baseUrl + "/" + requestMapping.value()).replaceAll("/+","/");
						mapping.put(url, method);
						System.out.println("url:"+url);
					}
				}else if(clazz.isAnnotationPresent(Service.class)){
					Service service = clazz.getAnnotation(Service.class);
					String beanName = service.value();
					if("".equals(beanName)){
						beanName = clazz.getName();
					}
					Object instance = clazz.newInstance();
					mapping.put(beanName, instance);
					for(Class<?> i : clazz.getInterfaces()){
						mapping.put(i.getName(), instance);
					}
				}else{
					continue;
				}
			}
			
			/* 依赖注入 */
			for(Object object : mapping.values()){
				if(object == null) continue;
				Class clazz = object.getClass();
				if(clazz.isAnnotationPresent(Controller.class)){
					Field[] fields = clazz.getDeclaredFields();
					for(Field field : fields){
						if(!field.isAnnotationPresent(Autowired.class)) continue;
						Autowired autowired = field.getAnnotation(Autowired.class);
						String beanName = autowired.value();
						if("".equals(beanName)){
							beanName = field.getType().getName();
						}
						field.setAccessible(true);
						field.set(mapping.get(clazz.getName()),mapping.get(beanName));
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void doScanner(String scanPackage) {
		URL url = this.getClass().getClassLoader().getResource("/"+scanPackage.replaceAll("\\.","/"));
		File classDir = new File(url.getFile());
		for(File file : classDir.listFiles()){
			if(file.isDirectory()) {
				doScanner(scanPackage+"."+file.getName());
			}else{
				if(!file.getName().endsWith(".class")) continue;
				String className = (scanPackage + "." + file.getName().replace(".class",""));
				mapping.put(className,null);
			}
		}
	}
}
