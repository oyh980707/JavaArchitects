package com.loveoyh.common.entity;

import lombok.Data;

/**
 * 客户端往后台的请求参数对象
 */
@Data
public class RequestParams {
    /**
     * data业务参数
     */
    private String data;

    /**
     * 登录后颁发的令牌token
     */
    private String token;

    /**
     * 输入参数签名结果
     */
    private String sign;

    /**
     * 客户端
     */
    private String client;

    /**
     * 版本号
     */
    private String version;

    /**
     * mac地址
     */
    private String mac;

    /**
     * ip地址
     */
    private String ip;
    //时间戳
    private long timestamp;
    //POS(1), WEB(2), IOS(3), ANDROID(4);
    private Integer sourse;

}