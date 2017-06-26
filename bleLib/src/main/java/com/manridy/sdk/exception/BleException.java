package com.manridy.sdk.exception;

import java.io.Serializable;

/**
 * 蓝牙异常基础类
 * 通过异常返回需要的参数
 * Created by jarLiao on 2016/10/18.
 */

public class BleException implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int ERROR_CODE_TIMEOUT = 1;
    public static final int ERROR_CODE_INITIAL = 101;
    public static final int ERROR_CODE_GATT = 102;
    public static final int ERROR_CODE_WRITE = 102;
    public static final int ERROR_CODE_OTHER = 301;
    public static final int ERROR_CODE_PARSE = 401;

    private int code;//错误代号
    private String description;//错误描述

    public BleException(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "BleException{" +
                "code=" + code +
                ", description='" + description + '\'' +
                '}';
    }

}
