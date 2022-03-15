package com.loveoyh.common.entity;

import com.loveoyh.common.enums.ResultCodeEnum;

import java.io.Serializable;

public class ResultVO<T> implements Serializable {
    private static final long serialVersionUID = 74704857074895138L;
    private int code;
    private String msg;
    private T result;

    public ResultVO() {
    }

    public ResultVO(T result) {
        this(ResultCodeEnum.SUCCESS, result);
    }

    public ResultVO(Integer code, String msg, T result) {
        this.result = result;
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(ResultCodeEnum resultCodeEnum, T result) {
        this.result = result;
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMsg();
    }

    public ResultVO(ResultCodeEnum resultCodeEnum, String msg) {
        this.code = resultCodeEnum.getCode();
        this.msg = msg;
    }

    public ResultVO(ResultCodeEnum resultCodeEnum) {
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMsg();
    }

    public static <T> ResultVO<T> createSuccess(T result) {
        return new ResultVO(result);
    }

    public static <T> ResultVO<T> createError(ResultCodeEnum resultCodeEnum, String msg) {
        return new ResultVO(resultCodeEnum, msg);
    }

    public static ResultVO createSuccess() {
        return new ResultVO(ResultCodeEnum.SUCCESS);
    }

    public static ResultVO createError() {
        return new ResultVO(ResultCodeEnum.ERROR);
    }

    public static ResultVO createError(ResultCodeEnum resultCodeEnum) {
        return new ResultVO(resultCodeEnum.getCode());
    }

    public static ResultVO createError(String msg) {
        return new ResultVO(ResultCodeEnum.ERROR, msg);
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
