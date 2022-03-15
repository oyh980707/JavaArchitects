package com.loveoyh.common.enums;

public enum ResultCodeEnum {
    SUCCESS(0, "success"),
    ERROR(1, "error"),
    ERROR_INVALID_PARAM(100001, "无效的参数"),
    ERROR_REPEAT_SUBMIT(100002, "重复提交"),
    ERROR_TIMEOUT(100003, "请求超时"),
    ERROR_DATA_NULL(100004, "数据不存在"),
    ERROR_DATA_EXISTS(100005, "数据已存在"),
    ERROR_SECURITY_CHECK(100006, "安全检查异常"),
    ERROR_INNER(100007, "程序内部错误，操作失败"),
    ERROR_DATABASE(100008, "数据库操作异常"),
    ERROR_MQ_SEND(100009, "mq消息发送失败"),
    ERROR_DATA_REPEAT(100010, "数据重复"),
    ERROR_RPC(100011, "RPC调用异常"),
    ERROR_FREQUENT_SUBMIT(100012, "操作太频繁"),
    ERROR_FLOW_LIMIT(100013, "限流异常，请稍后再试"),
    COMMON_PARAM_FAIL_CODE(100015, "请传入正确的公共参数"),
    SIGN_NO_PARAM_CODE(100016, "请传入sign参数"),
    CLIENT_NO_PARAM_MESSAGE(100019, "请传入client参数"),
    CLIENT_NO_EXISTS_MESSAGE(100017, "未授权的客户端"),
    SIGN_FAIL_ERROR_MESSAGE(100018, "签名sign错误"),
    REQUEST_METHOD_ERROR_CODE(100014, "请求方式错误");

    private int code;
    private String msg;

    private ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
