package com.loveoyh.common.entity;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class OperationLog {

    /**
     * 日志id
     */
    private Integer id;

    /**
     * 操作类型 0未设置 1登录 2登出 3网关日志
     */
    private Byte operationType;

    /**
     * 操作内容
     */
    private String operationContent;

    /**
     * 操作时间
     */
    private Date operationTime;

    /**
     * 客户端版本号
     */
    private String version;

    /**
     * 请求mac地址
     */
    private String mac;

    /**
     * 请求ip
     */
    private String ip;

    /**
     * 请求地址
     */
    private String requestPath;

    /**
     * 请求总共耗时
     */
    private String timeConsuming;

    /**
     * 服务请求耗时
     */
    private String serviceTimeConsuming;

    /**
     * 请求服务地址列表
     */
    private String servicePaths;

    /**
     * 异常信息
     */
    private String errorMsg;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应结果
     */
    private String responseResult;

    /**
     * 客户端
     */
    private String client;

    /**
     * 客户端签名sign
     */
    private String clientSign;

    /**
     * 服务签名后sign
     */
    private String serverSign;

    /**
     * 公共参数params
     */
    private String clientParams;
    /**
     * 公共header
     */
    private Map<String,String> headerMap;
}