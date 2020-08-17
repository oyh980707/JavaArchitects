package com.loveoyh.ProxyPattern.dbroute;

import com.loveoyh.ProxyPattern.dbroute.proxy.OrderServiceDynamicProxy;
import com.loveoyh.ProxyPattern.dbroute.proxy.OrderServiceSaticProxy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/24 21:57
 */
public class DBRouteDynamicProxyTest {
    public static void main(String[] args) {
        try {
            Order order = new Order();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date createTime = sdf.parse("2017/11/11");
            order.setCreateTime(createTime.getTime());
            IOrderService service = (IOrderService) new OrderServiceDynamicProxy().getInstance(new OrderService());
            service.createOrder(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
