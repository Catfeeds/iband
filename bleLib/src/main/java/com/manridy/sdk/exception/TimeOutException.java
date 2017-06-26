package com.manridy.sdk.exception;

/**
 * 超时异常
 * Created by jarLiao on 2016/10/18.
 */

public class TimeOutException extends BleException {
    public TimeOutException() {
        super(ERROR_CODE_TIMEOUT,"TimeOut Exception Occurred! ");
    }
}
