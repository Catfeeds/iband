package com.manridy.sdk.callback;


import com.manridy.sdk.exception.BleException;

/**
 * 蓝牙回调基类
 * Created by jarLiao on 2016/10/21.
 */

public interface BleCallback<T> {
    void onSuccess(T t);
    void onFailure(BleException exception);
}
