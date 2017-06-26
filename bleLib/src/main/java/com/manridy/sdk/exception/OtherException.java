package com.manridy.sdk.exception;

/**
 * 其他异常
 * Created by jarLiao on 2016/10/18.
 */

public class OtherException extends BleException {
    public OtherException(String description) {
        super(ERROR_CODE_OTHER,description);
    }
}
