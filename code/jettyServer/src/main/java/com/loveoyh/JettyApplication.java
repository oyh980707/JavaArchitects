package com.loveoyh;

import com.loveoyh.annotation.ControllerAnnotationHelper;
import com.loveoyh.config.LoadConfig;
import com.loveoyh.config.SystemConfig;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHandler;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class JettyApplication {

    private static Server server;
    public static List<Filter> filters = new ArrayList<>();

    public static void main(String[] args) {
        try {
            // 加载系统配置
            LoadConfig.loadSystemConfig();

            // 注解扫描
            ControllerAnnotationHelper.getAnnotationClass(SystemConfig.scanPackage, true);

            System.out.println("准备启动jetty，端口为 = "+SystemConfig.port);
            // 监听端口
            server = new Server(SystemConfig.port);

            ServletHandler handler = new ServletHandler();
            server.setHandler(handler);

            // 注入Servlet
            handler.addServletWithMapping(BaseServlet.class, "/*");

            //加入过滤器
            for (Filter filter : filters) {
                handler.addFilterWithMapping(new FilterHolder(filter), "/*", EnumSet.of(DispatcherType.REQUEST));
            }

            server.start();

            System.out.println("Jetty启动成功,端口号:"+SystemConfig.port);
        }catch (Exception e){
            System.out.println("Jetty启动异常：" + e);
            throw new RuntimeException();
        }
    }

}
