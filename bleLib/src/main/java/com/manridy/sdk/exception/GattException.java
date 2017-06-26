package com.manridy.sdk.exception;

/**
 * 蓝牙中央异常
 * 返回蓝牙中央状态码
 * Created by jarLiao on 2016/10/18.
 */

public class GattException extends BleException {
    private int status;//gatt错误码

    public GattException(int status) {
        super(ERROR_CODE_GATT,"Gatt Exception Occurred Status = "+ status);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
