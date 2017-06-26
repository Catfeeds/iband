package com.manridy.sdk.exception.handler;


import com.manridy.sdk.exception.BleException;
import com.manridy.sdk.exception.ConnectException;
import com.manridy.sdk.exception.GattException;
import com.manridy.sdk.exception.InitiatedException;
import com.manridy.sdk.exception.OtherException;
import com.manridy.sdk.exception.TimeOutException;

/**
 * 蓝牙异常处理
 * Created by jarLiao on 2016/10/18.
 */

public abstract class BleExceptionHandler {


    /**
     * 处理异常
     * @param exception
     * @return
     */
    public BleExceptionHandler handlerException(BleException exception){
        if (exception != null) {
            if (exception instanceof ConnectException) {
                onConnectException((ConnectException) exception);
            }else if (exception instanceof GattException){
                onGattException((GattException) exception);
            }else if (exception instanceof TimeOutException){
                onTimeOutException((TimeOutException) exception);
            }else if (exception instanceof InitiatedException){
                onInitiatedException((InitiatedException) exception);
            }else if (exception instanceof OtherException){
                onOtherException((ConnectException) exception);
            }
        }
        return this;
    }

    /**
     * 连接失败
     * @param e
     */
    protected abstract void onConnectException(ConnectException e);

    /**
     * gatt异常
     * @param e
     */
    protected abstract void onGattException(GattException e);

    /**
     * 超时异常
     * @param e
     */
    protected abstract void onTimeOutException(TimeOutException e);

    /**
     * 初始化异常
     * @param e
     */
    protected abstract void onInitiatedException(InitiatedException e);

    /**
     * 其他
     * @param e
     */
    protected abstract void onOtherException(ConnectException e);
}
