package com.manridy.applib.common;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import android.view.Window;
import android.view.WindowManager;



/**
 * Dialog管理类
 * Created by jarLiao on 2016/8/9.
 */
public class DialogManage {
    private Context context;

    public DialogManage(Context context) {
        this.context = context;
    }

    public AlertDialog setTheme(AlertDialog alertDialog, int gravity){
        Window mWindow =alertDialog.getWindow();
        mWindow.setGravity(gravity);
        mWindow.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams mLayoutParams = mWindow.getAttributes();
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindow.setAttributes(mLayoutParams);
        return alertDialog;
    }



}
