package com.manridy.applib.common;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.lang.reflect.Field;
import java.util.List;

/**
 * App应用管理类
 * Created by jarLiao on 2016/6/27.
 */
public class AppInfoManage {

    public enum AppType {
        ALL_APP,
        USER_APP,
        SYSTEM_APP
    }

    private ActivityManager activityManager;
    private PackageManager packageManager;

    public AppInfoManage(Context context) {
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        packageManager = context.getPackageManager();
    }

    /**
     * 通过包名，获取完整的App信息
     * @param packageName 应用的包名
     * @return App信息
     */
    public ApplicationInfo querySimpleAppInfo(String packageName) {
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有的App信息列表
     * @return App信息列表
     */
    public List<ApplicationInfo> queryAllAppInfo() {
        return packageManager.getInstalledApplications(0);
    }

    /**
     * 查看位于前台的App
     * @return App的包名
     */
    public String queryFirstAppPackageName() {
        if (true) {
            List<ActivityManager.RunningTaskInfo> runningTaskInfoList =
                    activityManager.getRunningTasks(1);
            if (runningTaskInfoList.size() > 0) {
                return runningTaskInfoList.get(0).topActivity.getPackageName();
            }
        } else {
            try {
                Field processStateTopField = ActivityManager.class.getDeclaredField("PROCESS_STATE_TOP");
                int processStateTop = processStateTopField.getInt(activityManager);

                List<ActivityManager.RunningAppProcessInfo> infoList = activityManager.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo info : infoList) {
                    Field processStateField = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
                    int currentState = processStateField.getInt(info);
                    if (currentState == processStateTop) {
                        return info.processName;
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 查看位于前台的App
     * @return App的顶层Activity类名
     */
    public String queryFirstAppActivityName () {
        if (Build.VERSION.SDK_INT < 21) {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
            if (runningTaskInfos.size() > 0) {
                return runningTaskInfos.get(0).topActivity.getClassName();
            }
        }

        return null;
    }

    /**
     * 查看位于前台的App
     * @return 任务信息
     */
    public ComponentName queryTopComponentName () {
        if (Build.VERSION.SDK_INT < 21) {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
            if (runningTaskInfos.size() > 0) {
                return runningTaskInfos.get(0).topActivity;
            }
        }

        return null;
    }

    /**
     * 判断指定应用是否正在运行
     * @param packageName 要判断的任务
     * @return true 是; false 否
     */
    public boolean isPackageRunning (String packageName) {
        List<ActivityManager.RunningAppProcessInfo> infoList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infoList) {
            try {
                if (info.processName.equals(packageName)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

}
