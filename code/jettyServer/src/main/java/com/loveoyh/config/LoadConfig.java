package com.loveoyh.config;

import com.loveoyh.utils.PropertiesUtils;

public class LoadConfig {

    public static void loadSystemConfig() {
        System.out.println("开始解析application.properties配置文件...");
        parseProperties();
        System.out.println("开始解析application.properties配置文件完成.");
    }

    private static void parseProperties() {
        SystemConfig.port = Integer.valueOf(PropertiesUtils.getProperty("server.port"));
        SystemConfig.scanPackage = PropertiesUtils.getProperty("core.scanPackage");
    }

}
