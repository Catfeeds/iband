package com.manridy.applib.base;

import android.content.Context;
import android.view.View;

/**
 * holder基类
 * Created by jarLiao on 2016/10/14.
 */

public abstract class BaseHolder<T> {
    protected View contentView;
    protected T data;

    public BaseHolder() {
        contentView = initView();
        contentView.setTag(this);
    }

    public void setData(T data) {
        this.data = data;
        refreshView(data);
    }

    public View getContentView() {
        return contentView;
    }

    protected abstract void refreshView(T data);

    protected abstract View initView();

}
