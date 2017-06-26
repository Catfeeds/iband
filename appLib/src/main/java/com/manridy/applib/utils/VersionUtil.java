package com.manridy.applib.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * App版本信息工具类
 * Created by jarLiao on 2016/8/5.
 */
public class VersionUtil {
    /**
     * 得到版本名
     * @param context
     * @return
     */
    public static String getVersionName(Context context){
        return getPackageInfo(context).versionName;
    }

    /**
     * 得到版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context){
        return getPackageInfo(context).versionCode;
    }


    public static PackageInfo getPackageInfo(Context context){
        PackageInfo pi = null;
        PackageManager pm = context.getPackageManager();
        try {
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }
}
