package com.loveoyh.ProxyPattern.CustomProxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/25 00:14
 */
public class ClassLoader extends java.lang.ClassLoader {
    private File classPathFile;

    public ClassLoader(){
        String classPath = ClassLoader.class.getResource("").getPath();
        this.classPathFile = new File(classPath);
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String className = ClassLoader.class.getPackage().getName() + "." + name;
        if(classPathFile != null){
            File classFile = new File(classPathFile,name.replaceAll("\\.","/") + ".class");
            if(classFile.exists()){
                FileInputStream in = null;
                ByteArrayOutputStream out = null;
            try{
                in = new FileInputStream(classFile);
                out = new ByteArrayOutputStream();
                byte [] buff = new byte[1024];
                int len;
                while ((len = in.read(buff)) != -1){
                    out.write(buff,0,len);
                }
                return defineClass(className,out.toByteArray(),0,out.size());
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(null != in){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(out != null){
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        }
        return null;
    }

}
