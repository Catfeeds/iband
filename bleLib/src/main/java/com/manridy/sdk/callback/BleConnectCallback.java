package com.manridy.sdk.callback;

import com.manridy.sdk.exception.BleException;

/**
 * 蓝牙连接回调
 * Created by jarLiao on 2016/10/21.
 */

public interface BleConnectCallback  {
    void onConnectSuccess();
    void onConnectFailure(BleException exception);
}
