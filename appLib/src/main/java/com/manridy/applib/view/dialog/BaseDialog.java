package com.manridy.applib.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.TimePicker;

import com.manridy.applib.R;


/**
 * 基础选择器
 * Created by Administrator on 2016/7/18.
 */
public class BaseDialog extends AlertDialog {


    public BaseDialog(Context context) {
        super(context);
        initBaseDialogTheme();
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        initBaseDialogTheme();
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initBaseDialogTheme();
    }

    //底部全屏样式
    protected void initBaseDialogTheme() {
        /*android:windowNoTitle*/
//        dialog_time_select.xml(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        /* android:windowBackground*/
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }


}
