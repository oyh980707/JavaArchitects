package com.loveoyh.DelegatePattern.mvc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * DesignPatterns
 * Created by oyh.Jerry to 2020/02/26 20:26
 */
public class DispatcherServlet extends HttpServlet {
	private static List<Handler> handlerMapping = new ArrayList<>();
	
	public void init(){
		try {
			handlerMapping.add(new Handler()
					.setController(MemberController.class.newInstance())
					.setMethod(MemberController.class.getMethod("getMemberById",new Class[]{String.class}))
					.setUri("/web/getMemberById.do"));
			handlerMapping.add(new Handler()
					.setController(OrderController.class.newInstance())
					.setMethod(OrderController.class.getMethod("getOrderById",new Class[]{String.class}))
					.setUri("/web/getOrderById.do"));
			handlerMapping.add(new Handler()
					.setController(SystemController.class.newInstance())
					.setMethod(SystemController.class.getMethod("logout",new Class[]{}))
					.setUri("/web/logout.do"));
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public void doDispatcher(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String uri = request.getRequestURI();
		
		Handler handle = null;
		for (Handler h: handlerMapping) {
			if(uri.equals(h.getUri())){
				handle = h;
				break;
			}
		}
		
		Object object = handle.getMethod().invoke(handle.getController(),request.getParameter("mid"));
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			doDispatcher(req,resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	class Handler{
		private Object controller;
		private Method method;
		private String uri;
		
		public Object getController() {
			return controller;
		}
		
		public Handler setController(Object controller) {
			this.controller = controller;
			return this;
		}
		
		public Method getMethod() {
			return method;
		}
		
		public Handler setMethod(Method method) {
			this.method = method;
			return this;
		}
		
		public String getUri() {
			return uri;
		}
		
		public Handler setUri(String uri) {
			this.uri = uri;
			return this;
		}
	}
}
