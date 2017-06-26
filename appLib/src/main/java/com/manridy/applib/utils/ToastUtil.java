package com.manridy.applib.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 * Created by jarLiao on 2016/8/10.
 */
public class ToastUtil {
    private static Toast toast;

    public static void showToast(Context context, String msg){
        if (toast == null) {
            toast = Toast.makeText(context,msg, Toast.LENGTH_SHORT);
        }else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void showToast(Context context, int res){
        if (toast == null) {
            toast = Toast.makeText(context,res, Toast.LENGTH_SHORT);
        }else {
            toast.setText(res);
        }
        toast.show();
    }


}
