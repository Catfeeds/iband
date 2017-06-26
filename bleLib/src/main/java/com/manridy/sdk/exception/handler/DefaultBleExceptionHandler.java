package com.manridy.sdk.exception.handler;

import android.content.Context;

import com.manridy.sdk.exception.ConnectException;
import com.manridy.sdk.exception.GattException;
import com.manridy.sdk.exception.InitiatedException;
import com.manridy.sdk.exception.TimeOutException;


/**
 * 默认蓝牙异常处理
 * Created by jarLiao on 2016/10/19.
 */

public class DefaultBleExceptionHandler extends BleExceptionHandler {
    private Context context;

    public DefaultBleExceptionHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    protected void onConnectException(ConnectException e) {

    }

    @Override
    protected void onGattException(GattException e) {

    }

    @Override
    protected void onTimeOutException(TimeOutException e) {

    }

    @Override
    protected void onInitiatedException(InitiatedException e) {

    }

    @Override
    protected void onOtherException(ConnectException e) {

    }
}
