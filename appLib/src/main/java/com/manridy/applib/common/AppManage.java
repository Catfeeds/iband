package com.manridy.applib.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * Activity管理类
 * 用于管理activity和退出程序
 * Created by Administrator on 2016/10/14.
 */

public class AppManage {
    private static Stack<Activity> activityStack;
    private static AppManage instance;

    private AppManage(){
    }

    public static AppManage getInstance() {
        if (instance == null) {
            instance = new AppManage();
        }
        return instance;
    }

    /**
     * 添加activity到堆栈
     * @param activity
     */
    public void addActivity(Activity activity){
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 得到当前activity
     * @return
     */
    public Activity currentActivity(){
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束栈内最后一个activity
     */
    public void finishActivity(){
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定activity
     * @param activity
     */
    public void finishActivity(Activity activity){
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名activity
     * @param cls
     */
    public void finishActivity(Class<?> cls){
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有activity
     */
    public void finishAllActivity(){
        for (int i = 0; i < activityStack.size(); i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     * @param context
     */
    public void appExit(Context context){
        try {
            finishAllActivity();
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            mActivityManager.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
