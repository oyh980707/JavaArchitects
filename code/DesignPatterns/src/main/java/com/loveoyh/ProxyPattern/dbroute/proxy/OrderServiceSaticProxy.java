package com.loveoyh.ProxyPattern.dbroute.proxy;

import com.loveoyh.ProxyPattern.dbroute.IOrderService;
import com.loveoyh.ProxyPattern.dbroute.Order;
import com.loveoyh.ProxyPattern.dbroute.OrderService;
import com.loveoyh.ProxyPattern.dbroute.db.DynamicDataSourceEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/24 19:33
 */
public class OrderServiceSaticProxy implements IOrderService{
    private SimpleDateFormat format = new SimpleDateFormat("yyyy");
    private IOrderService service;

    public OrderServiceSaticProxy(IOrderService service) {
        this.service = service;
    }

    public int createOrder(Order order){
        before();
        Long time = order.getCreateTime();
        Integer dbRouter = Integer.valueOf(format.format(new Date(time)));
        System.out.println("静态代理类自动分配到[DB_"+dbRouter+"]数据源处理数据");
        DynamicDataSourceEntity.set(dbRouter);
        service.createOrder(order);
        after();
        return 1;
    }

    private void before() {
        System.out.println("Proxy before method.");
    }

    private void after() {
        System.out.println("Proxy after method");
    }
}
