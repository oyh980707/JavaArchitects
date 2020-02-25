package com.loveoyh.ProxyPattern.CustomProxy;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/24 23:34
 */
public class Proxy {
    private static final String ln = "\r\n";
    public static Object newProxyInstance(ClassLoader classLoader,Class<?>[] interfaces,InvocationHandler h){
        try {
            //1、拿到被代理对象的引用，并且获取到它的所有的接口，反射获取
            String src = generateSrc(interfaces);
            //2、java文件输出到磁盘
            String filePath = Proxy.class.getResource("").getPath();
            File f = new File(filePath + "$Proxy0.java");
            FileWriter fw = new FileWriter(f);
            fw.write(src);
            fw.flush();
            fw.close();

            //3、编译新生成的Java代码.class
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager manage = compiler.getStandardFileManager(null,null,null);
            Iterable iterable = manage.getJavaFileObjects(f);
            JavaCompiler.CompilationTask task = compiler.getTask(null,manage,null,null,null,iterable);
            task.call();
            manage.close();

            //4、再重新加载到JVM中运行
            Class proxyClass = classLoader.findClass("$Proxy0");
            Constructor c = proxyClass.getConstructor(InvocationHandler.class);
            f.delete();

            //5、返回字节码重组以后的新的代理对象
            return c.newInstance(h);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成java源文件
     * @param interfaces
     * @return
     */
    private static String generateSrc(Class<?>[] interfaces) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.loveoyh.ProxyPattern.CustomProxy;"+ln);
        sb.append("import com.loveoyh.ProxyPattern.CustomProxy.Person;"+ln);
        sb.append("import java.lang.reflect.*;"+ln);

        sb.append("public class $Proxy0 implements "+ interfaces[0].getName() +"{"+ln);
            sb.append("InvocationHandler h;" + ln);
            sb.append("public $Proxy0(InvocationHandler h) { " + ln);
                sb.append("this.h = h;"+ln);
            sb.append("}" + ln);
            for (Method m : interfaces[0].getMethods()){
                Class<?>[] params = m.getParameterTypes();
                StringBuffer paramNames = new StringBuffer();
                StringBuffer paramValues = new StringBuffer();
                StringBuffer paramClasses = new StringBuffer();
                for (int i = 0; i < params.length; i++) {
                    Class clazz = params[i];
                    String type = clazz.getName();
                    String paramName = toLowerFirstCase(clazz.getSimpleName());
                    paramNames.append(type + " " + paramName);
                    paramValues.append(paramName);
                    paramClasses.append(clazz.getName() + ".class");
                    if(i > 0 && i < params.length-1){
                        paramNames.append(",");
                        paramClasses.append(",");
                        paramValues.append(",");
                    }
                }
                sb.append("public " + m.getReturnType().getName() + " " + m.getName() + "(" + paramNames.toString() + ") {" + ln);
                    sb.append("try{" + ln);
                        sb.append("Method m = " + interfaces[0].getName() + ".class.getMethod(\"" + m.getName() + "\",new Class[]{" + paramClasses.toString() + "});" + ln);
                        sb.append((hasReturnValue(m.getReturnType()) ? "return " : "") + getCaseCode("this.h.invoke(this,m,new Object[]{"+paramValues+"})",m.getReturnType()) +";"+ ln);
                    sb.append("}catch(Error _ex) {"+ln);
                    sb.append("}catch(Throwable e){" + ln);
                        sb.append("throw new UndeclaredThrowableException(e);" + ln);
                    sb.append("}"+ln);
                sb.append(getReturnEmptyCode(m.getReturnType())+ln);
                sb.append("}"+ln);
            }
        sb.append("}" + ln);
        return sb.toString();
    }

    private static Map<Class,Class> mappings = new HashMap<Class, Class>();

    private static String getReturnEmptyCode(Class<?> returnClass){
        if(mappings.containsKey(returnClass)){
            return "return 0;";
        }else if(returnClass == void.class){
            return "";
        }else {
            return "return null;";
        }
    }


    private static boolean hasReturnValue(Class<?> clazz){
        return clazz != void.class;
    }

    private static String getCaseCode(String code,Class<?> returnClass){
        if(mappings.containsKey(returnClass)){
            return "((" + mappings.get(returnClass).getName() + ")" + code + ")." + returnClass.getSimpleName() + "Value()";
        }
        return code;
    }

    private static String toLowerFirstCase(String src){
        char [] chars = src.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
