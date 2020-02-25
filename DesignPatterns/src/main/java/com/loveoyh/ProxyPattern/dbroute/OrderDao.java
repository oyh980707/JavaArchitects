package com.loveoyh.ProxyPattern.dbroute;

/**
 * Created by oyh.Jerry.
 */
public class OrderDao {
    public int insert(Order order){
        System.out.println("OrderDao创建Order成功!");
        return 1;
    }
}
