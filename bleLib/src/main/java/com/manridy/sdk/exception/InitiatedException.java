package com.manridy.sdk.exception;

/**
 * 初始化异常
 * Created by jarLiao on 2016/10/18.
 */

public class InitiatedException extends BleException {
    public InitiatedException() {
        super(ERROR_CODE_INITIAL,"Initiated Exception Occurred! ");
    }
}
