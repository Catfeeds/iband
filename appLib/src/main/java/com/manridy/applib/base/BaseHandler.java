package com.manridy.applib.base;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * handler基类 弱引用
 * Created by jarLiao on 2016/10/19.
 */

public abstract class BaseHandler<T> extends Handler {
    private WeakReference<T> mWeakReference;//弱引用对象

    public BaseHandler(T t) {
        super();
        mWeakReference = new WeakReference<T>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        T t = mWeakReference.get();
        if (t != null) {
            handleMessage(msg,t);
        }
    }

    public abstract void handleMessage(Message msg, T t);
}