package com.manridy.sdk.callback;


/**
 * 蓝牙上报监听基类
 * Created by jarLiao on 2016/10/21.
 */

public interface BleHistoryListener<T> {
    void onHistory(T t);
}
