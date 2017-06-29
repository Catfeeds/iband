package com.manridy.iband.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.IbandApplication;
import com.manridy.iband.IbandDB;
import com.manridy.iband.bean.AppModel;
import com.manridy.iband.common.AppGlobal;
import com.manridy.sdk.ble.BleCmd;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.exception.BleException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * note: VERSION_CODE >= API_18
 * <p/>
 * manifest:
 * <service android:name=".service.NotificationService"
 *  android:label="@string/app_name"
 *  android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE">
 *  <intent-filter>
 *      <action android:name="android.service.notification.NotificationListenerService" />
 *  </intent-filter>
 * </service>
 *
 * @author MaTianyu
 * @date 2015-03-09
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService2 extends NotificationListenerService {
    private static final String TAG = "NotificationService";
    public static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private boolean cmdFirst = true;
    private int appAlert = -1;

    public static final String PAGE_NAME_QQ ="com.tencent.mobileqq";
    public static final String PAGE_NAME_WX ="com.tencent.mm";
    public static final String PAGE_NAME_WHATSAPP ="com.whatsapp";
    public static final String PAGE_NAME_FACEBOOK ="com.facebook.katana";

    public static final int APP_ID_QQ =2;
    public static final int APP_ID_WX =4;
    public static final int APP_ID_WHATSAPP =5;
    public static final int APP_ID_FACEBOOK =6;
    /*----------------- 静态方法 -----------------*/
    public synchronized static void startNotificationService(Context context) {
        context.startService(new Intent(context, NotificationService2.class));
    }

    public synchronized static void stopNotificationService(Context context) {
        context.stopService(new Intent(context, NotificationService2.class));
    }


    public static void startNotificationListenSettings(Context context) {
        Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
        if(!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static boolean isNotificationListenEnable(Context context) {
        return isNotificationListenEnable(context, context.getPackageName());
    }

    public static boolean isNotificationListenEnable(Context context, String pkgName) {
        final String flat = Settings.Secure.getString(context.getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*----------------- 生命周期 -----------------*/
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate..");
//        toggleNotificationListenerService(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand..");
        toggleNotificationListenerService();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy..");
    }

    /*----------------- 通知回调 -----------------*/
    int infoId = 1;
    List<byte[]> cmdList = new ArrayList<>();
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        cmdFirst = true;
        String packageName = sbn.getPackageName();
        Notification notification = sbn.getNotification();
        String content = String.valueOf(notification.tickerText);
        Log.i(TAG, sbn.toString());
        Log.i(TAG, content);
        boolean appOnOff = (boolean) SPUtil.get(this, AppGlobal.DATA_ALERT_APP,false);
        if (appOnOff && !content.equals("null")) {
            List<AppModel> appList = IbandDB.getInstance().getAppList();
            Map<Integer,AppModel> map = new HashMap<>();
            cmdList = getCmdList(content);
            appAlert = -1;
            for (AppModel appModel : appList) {
                map.put(appModel.getAppId(),appModel);
            }
            boolean qqAlert = map.containsKey(APP_ID_QQ) && map.get(APP_ID_QQ).isOnOff();
            boolean wxAlert = map.containsKey(APP_ID_WX) && map.get(APP_ID_WX).isOnOff();
            boolean whatsAlert = map.containsKey(APP_ID_WHATSAPP) && map.get(APP_ID_WHATSAPP).isOnOff();
            boolean facebookAlert = map.containsKey(APP_ID_FACEBOOK) && map.get(APP_ID_FACEBOOK).isOnOff();
            if (packageName.equals(PAGE_NAME_QQ) && qqAlert) {
                appAlert = APP_ID_QQ;
            }else if (packageName.equals(PAGE_NAME_WX) && wxAlert){
                appAlert = APP_ID_WX;
            }else if (packageName.equals(PAGE_NAME_WHATSAPP) && whatsAlert){
                appAlert = APP_ID_WHATSAPP;
            }else if (packageName.equals(PAGE_NAME_FACEBOOK) && facebookAlert){
                appAlert = APP_ID_FACEBOOK;
            }
            if (appAlert != -1) {
                infoId = infoId > 63 ? 1 : infoId++;
                IbandApplication.getIntance().service.watch.sendCmd(BleCmd.setAppAlertName(infoId,appAlert), AppleCallback);
            }
        }


//            Log.i(TAG, "tickerText : " + notification.tickerText);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                Bundle bundle = notification.extras;
//                for (String key : bundle.keySet()) {
//                    Log.i(TAG, key + ": " + bundle.get(key));
//                }
//            }
//
//        if (self != null && notificationListener != null) {
//            notificationListener.onNotificationPosted(sbn);
//        }
    }
    BleCallback AppleCallback = new BleCallback() {
        @Override
        public void onSuccess(Object o) {
            if (cmdList.size()>0 && !cmdFirst) {
                cmdList.remove(0);
            }
            cmdFirst = false;
            if (cmdList.size()>0 && appAlert!= -1) {
                IbandApplication.getIntance().service.watch.sendCmd(BleCmd.setAppAlertContext(infoId,appAlert,cmdList.get(0)), AppleCallback);
            }else {
                Log.d(TAG, "app提醒发送完成");
            }
        }

        @Override
        public void onFailure(BleException exception) {
            Log.d(TAG, "onFailure() called with: exception = [" + exception.toString() + "]");
        }
    };

    private List<byte[]> getCmdList(String content)  {
        List<byte[]> bytes = new ArrayList<>();
        byte[] contexts = new byte[0];//string转uicode编码 大端在前
        try {
            contexts = content.getBytes("UnicodeBigUnmarked");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int len = contexts.length;
        int index = 0;
        while (len >0){
            byte[] cmd = new byte[len>12?12:len];
            System.arraycopy(contexts,index*12,cmd,0,len>12?12:len);
            bytes.add(cmd);
            len = len>12?len-12:0;
            index++;
        }
        return bytes;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

    public void printCurrentNotifications() {
        StatusBarNotification[] ns = getActiveNotifications();
        for (StatusBarNotification n : ns) {
            Log.i(TAG, String.format("%20s",n.getPackageName()) + ": " + n.getNotification().tickerText);
        }
    }
    public static void toggleNotificationListenerService(Context context) {
        Log.e(TAG,"toggleNLS");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(context, NotificationService2.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(
                new ComponentName(context, NotificationService2.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private void toggleNotificationListenerService() {
        Log.e(TAG,"toggleNLS");
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this,NotificationService2.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(this,NotificationService2.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }
}