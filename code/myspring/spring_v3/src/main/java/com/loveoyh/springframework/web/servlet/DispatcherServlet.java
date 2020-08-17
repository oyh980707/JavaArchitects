package com.loveoyh.springframework.web.servlet;

import com.loveoyh.springframework.annotation.*;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Created by oyh.Jerry to 2020/03/04 17:27
 */
public class DispatcherServlet extends HttpServlet {
	//ioc容器
	private Map<String, Object> ioc = new HashMap<String, Object>();
	
	//保存url和Method的对应关系
	private List<Handler> HandlerMapping = new ArrayList<Handler>();
	
	//保存application.properties配置文件中的内容
	private Properties contextConfig = new Properties();
	
	//保存扫描的所有类名
	private List<String> classNames = new ArrayList<String>();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			doDispatcher(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
			resp.getWriter().write("500 Exception,Detail : " + Arrays.toString(e.getStackTrace()));
		}
	}
	
	/**
	 * 前端控制器
	 * doDispatcher中利用了委派模式
	 */
	private void doDispatcher(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Handler handler = getHandler(req);
		if(handler == null){
			res.getWriter().write("404 Not Found url对应的处理逻辑!");
			return;
		}
		
		//获得方法的形参列表
		Class<?>[] paramTypes = handler.method.getParameterTypes();
		
		Object[] paramValues = new Object[paramTypes.length];
		Map<String, String[]> params = req.getParameterMap();
		for(Map.Entry<String, String[]> param : params.entrySet()){
			String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s",",");
			
			if(!handler.paramIndexMapping.containsKey(param.getKey())) continue;
			
			int index = handler.paramIndexMapping.get(param.getKey());
			paramValues[index] = convert(paramTypes[index],value);
		}
		
		if(handler.paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
			int reqIndex = handler.paramIndexMapping.get(HttpServletRequest.class.getName());
			paramValues[reqIndex] = req;
		}
		
		if(handler.paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
			int resIndex = handler.paramIndexMapping.get(HttpServletResponse.class.getName());
			paramValues[resIndex] = res;
		}
		
		Object returnValue = handler.method.invoke(handler.controller,paramValues);
		
		if(returnValue==null || returnValue instanceof Void) return;
		
		res.getWriter().write(returnValue.toString());
	}
	
	/**
	 * 获取handler，通过req中的url
	 * @param req request对象
	 * @return 匹配到req中的url的Handler对象
	 */
	private Handler getHandler(HttpServletRequest req) {
		if(HandlerMapping.isEmpty()) return null;
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		uri = uri.replace(contextPath,"").replaceAll("/+","/");
		for(Handler handler : HandlerMapping){
			Matcher matcher = handler.pattern.matcher(uri);
			if(!matcher.matches()) continue;
			return handler;
		}
		return null;
	}
	
	/**
	 * url传过来的参数都是String类型的，HTTP 是基于字符串协议
	 * 把String转换为任意类型
	 * @param type 该参数的类型
	 * @param value 参数的值（字符串形式）
	 * @return 与参数类型一直的值
	 */
	private Object convert(Class<?> type, String value) {
		if(Integer.class == type) {
			return Integer.valueOf(value);
		}
		//如果还有 double 或者其他类型，继续加 if，此时，应该使用策略模式
		return value;
	}
	
	/**
	 * 初始化
	 * 1.加载配置文件
	 * 2.扫描相关的类
	 * 3.初始化扫描的类并把它放到IOC容器中
	 * 4.完成依赖注入
	 * 5.初始化HandlerMapping
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		doLoadConfig(config.getInitParameter("contextConfigLocation"));
		
		doScanner(contextConfig.getProperty("scanPackage"));
		
		doInstance();
		
		doAutowired();
		
		initHandlerMapping();
		
		System.out.println("Spring frameword is Init.");
	}
	
	/**
	 * 加载配置文件
	 * @param contextConfigLocation
	 */
	private void doLoadConfig(String contextConfigLocation) {
		//从类路径下找到Spring主配置文件所在的路径,并且将其读取出来放到Properties对象中，读取到内存里
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
		try {
			contextConfig.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 扫描相关的类
	 * @param scanPackage 需要扫描的包路径
	 */
	private void doScanner(String scanPackage) {
		//转换为文件路径进行递归扫描
		URL url = this.getClass().getClassLoader().getResource("/"+scanPackage.replaceAll("\\.","/"));
		File directory = new File(url.getFile());
		for(File file : directory.listFiles()){
			if(file.isDirectory()) {
				doScanner(scanPackage+"."+file.getName());
			}else{
				if (!file.getName().endsWith(".class")) continue;
				String className = (scanPackage+"."+file.getName().replace(".class",""));
				this.classNames.add(className);
			}
		}
	}
	
	/**
	 * 初始化扫描的类
	 */
	private void doInstance() {
		//初始化为DI做准备
		if(classNames.isEmpty()) return;
		
		try {
			for(String className : classNames){
				Class<?> clazz = Class.forName(className);
				//加了注解的类，才初始化，简化此处的扫描注解，只扫描@Controller,@Service等声明了的注解
				//TODO 重构代码，使得能够扫描所有组件而代码简介
				if(clazz.isAnnotationPresent(Controller.class)){
					Object instance = clazz.newInstance();
					//Spring 默认类名首字母小写
					String beanName = toLowerFirstCase(clazz.getSimpleName());
					ioc.put(beanName,instance);
				}else if(clazz.isAnnotationPresent(Service.class)){
					//自定义beanName
					Service service = clazz.getAnnotation(Service.class);
					Object instance = clazz.newInstance();
					String beanName = service.value();
					if("".equals(beanName.trim())){
						beanName = toLowerFirstCase(clazz.getSimpleName());
					}
					ioc.put(beanName,instance);
					// TODO 此处简化autowired的自动注入功能
					for(Class<?> i : clazz.getInterfaces()){
						if(ioc.containsKey(i.getName())){
							throw new Exception("The "+i.getName() + "is exists!");
						}
						ioc.put(toLowerFirstCase(i.getSimpleName()),instance);
					}
				}else{
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 依赖注入
	 */
	private void doAutowired() {
		if(ioc.isEmpty()) return;
		
		for(Map.Entry<String, Object> entry : ioc.entrySet()){
			//Declared，表示所有的声明的，包括private等修饰的属性
			//正常来说，只能拿到public的属性
			Field[] fields = entry.getValue().getClass().getDeclaredFields();
			for(Field field : fields){
				if(!field.isAnnotationPresent(Autowired.class)) continue;
				Autowired autowired = field.getAnnotation(Autowired.class);
				//获取用户自定义的名字
				String beanName = autowired.value().trim();
				if("".equals(beanName)){
					//类名首字母小写作为依赖注入依据
					beanName = toLowerFirstCase(field.getType().getSimpleName());
				}
				//如果是public以外的修饰符，需要强制依赖注入，就需要利用反射的暴力访问
				field.setAccessible(true);
				
				try {
					//利用反射，动态给字段赋值
					field.set(entry.getValue(),ioc.get(beanName));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 初始化HandlerMapping，IOC容器
	 */
	private void initHandlerMapping() {
		if(ioc.isEmpty()) return;
		
		for(Map.Entry<String, Object> entry : ioc.entrySet()){
			Class<?> clazz = entry.getValue().getClass();
			
			if(!clazz.isAnnotationPresent(Controller.class)) continue;
			
			//保存类上面的@RequestMapping("/demo")
			String baseUrl = "";
			if(clazz.isAnnotationPresent(RequestMapping.class)){
				RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
				baseUrl = requestMapping.value();
			}
			
			//获取方法上的@RequestMapping()
			for(Method method : clazz.getMethods()){
				if(!method.isAnnotationPresent(RequestMapping.class)) continue;
				
				RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
				String regex = ("/"+baseUrl+"/"+requestMapping.value()).replaceAll("/+","/");
				Pattern pattern = Pattern.compile(regex);
			 	HandlerMapping.add(new Handler(pattern,entry.getValue(),method));
			 	System.out.println("mapping:"+regex+","+method);
			}
		}
	}
	
	/**
	 * 将字符串首字母小写,首先需要遵守类名命名规则，首字母大写的驼峰命名，否则达不到期望值
	 * @param name 需要转换的字符串
	 * @return 转化后首字母小写的字符串
	 */
	private String toLowerFirstCase(String name) {
		char[] arr = name.toCharArray();
		arr[0] += 32;
		return String.valueOf(arr);
	}
	
	/**
	 * Handler记录Controller中的RequestMapping和Method的对应关系的内部类
	 * @author oyh.jerry
	 */
	private class Handler {
		//保存方法对应的实例
		protected Object controller;
		//保存映射关系
		protected Method method;
		protected Pattern pattern;
		//参数顺序
		protected Map<String, Integer> paramIndexMapping;
		
		/**
		 * 构造一个Handler基本的参数
		 * @param pattern    正则
		 * @param controller 实例
		 * @param method     方法
		 */
		protected Handler(Pattern pattern, Object controller, Method method) {
			this.controller = controller;
			this.method = method;
			this.pattern = pattern;
			paramIndexMapping = new HashMap<String, Integer>();
			putParamIndexMapping(method);
		}
		
		/**
		 * 将方法的顺序进行对应
		 * @param method
		 */
		private void putParamIndexMapping(Method method) {
			//提取方法中加入了注解的参数
			Annotation[][] pa = method.getParameterAnnotations();
			for (int i = 0; i < pa.length; i++) {
				for (Annotation a : pa[i]) {
					if (!(a instanceof RequestParam)) continue;
					String paramName = ((RequestParam) a).value();
					if ("".equals(paramName.trim())) continue;
					paramIndexMapping.put(paramName, i);
				}
			}
			//提取方法中的request和response
			Class<?>[] paramsTypes = method.getParameterTypes();
			for (int i = 0; i < paramsTypes.length; i++) {
				Class<?> type = paramsTypes[i];
				if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
					paramIndexMapping.put(type.getName(), i);
				}
			}
		}
	}
}
