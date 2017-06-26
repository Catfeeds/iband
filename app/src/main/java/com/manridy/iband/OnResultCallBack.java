package com.manridy.iband;

/**
 * 结果回调
 * Created by jarLiao.
 */

public abstract class OnResultCallBack<T> {
    public abstract void onResult(boolean result,T t);
}
