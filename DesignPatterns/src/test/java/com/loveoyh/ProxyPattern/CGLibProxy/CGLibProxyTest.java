package com.loveoyh.ProxyPattern.CGLibProxy;

import net.sf.cglib.core.DebuggingClassWriter;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/25 05:53
 */
public class CGLibProxyTest {
    public static void main(String[] args) {
        //利用 cglib 的代理类可以将内存中的 class 文件写入本地磁盘
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "C://Users//HP//Desktop//cglib_proxy_class.class");
        Customer obj = (Customer) new CGLibProxy().getInstance(Customer.class);
        obj.findLove();
    }
}
