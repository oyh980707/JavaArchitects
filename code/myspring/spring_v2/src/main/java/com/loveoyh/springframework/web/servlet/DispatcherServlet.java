package com.loveoyh.springframework.web.servlet;

import com.loveoyh.springframework.annotation.*;

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

/**
 * @Created by oyh.Jerry to 2020/03/04 17:27
 */
public class DispatcherServlet extends HttpServlet {
	//ioc容器
	private Map<String, Object> ioc = new HashMap<String, Object>();
	
	//保存url和Method的对应关系
	private Map<String, Method> HandlerMapping = new HashMap<String, Method>();
	
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
		String url = req.getRequestURI();
		String contextPath = req.getContextPath();
		url = url.replaceAll(contextPath,"").replaceAll("/+","/");
		if(!this.HandlerMapping.containsKey(url)){
			res.getWriter().write("404 Not Found!");
			return ;
		}
		
		Method method = this.HandlerMapping.get(url);
		//第一个参数，方法所在的实例；第二个参数调用时所需要的实参
		Map<String, String[]> params = req.getParameterMap();
		//获取方法的形参列表
		Class<?>[] parameterTypes = method.getParameterTypes();
		//保存请求的url参数列表
		Map<String, String[]> parameterMap = req.getParameterMap();
		//保存赋值参数的位置
		Object[] paramValues = new Object[parameterTypes.length];
		//根据参数位置动态赋值
		for(int i=0;i<parameterTypes.length;i++){
			Class parameterType = parameterTypes[i];
			if(parameterType == HttpServletRequest.class){
				paramValues[i] = req;
				continue;
			}else if(parameterType == HttpServletResponse.class){
				paramValues[i] = res;
				continue;
			}else if(parameterType == String.class){
				//提取方法中加了注解的参数
				Annotation[][] pa = method.getParameterAnnotations();
				for(int j=0;j<pa.length;j++){
					for(Annotation a : pa[j]){
						if(a instanceof RequestParam) {
							String paramName = ((RequestParam) a).value();
							if (!"".equals(paramName.trim())) {
								String value = Arrays.toString(parameterMap.get(paramName))
										.replaceAll("\\[|\\]", "")
										.replaceAll("\\s", ",");
								paramValues[i] = value;
							}
						}
					}
				}
			}
		}
		//简化调用
		String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
		method.invoke(ioc.get(beanName), paramValues);
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
				String url = ("/"+baseUrl+"/"+requestMapping.value()).replaceAll("/+","/");
				HandlerMapping.put(url, method);
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
}
