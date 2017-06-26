package com.manridy.iband;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.manridy.iband.service.AlertService;
import com.manridy.iband.service.BleService;
import com.tencent.bugly.Bugly;

import org.litepal.LitePalApplication;

/**
 * Created by jarLiao on 17/5/16.
 */

public class IbandApplication extends Application {
    public BleService service;
    private static IbandApplication intance;

    @Override
    public void onCreate() {
        super.onCreate();
        intance = this;
        LitePalApplication.initialize(this);//初始化数据库
        Fresco.initialize(this);//初始化图片加载
        Intent bindIntent = new Intent(this,BleService.class);
        bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE);
        startService(new Intent(this,AlertService.class));
//        boolean appOnOff = (boolean) SPUtil.get(this, AppGlobal.DATA_ALERT_APP,false);
//        if (appOnOff) {
//
//            startService(new Intent(this,NotificationCollectorMonitorService.class));
//
////            startService(new Intent(this,NotificationService.class));
////            NotificationService.toggleNotificationListenerService(this);
//        }
        Bugly.init(getApplicationContext(), "33139ca6ea",false);
//        CrashHandler.getInstance().init(intance);
    }



    public static IbandApplication getIntance() {
        return intance;
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = ((BleService.LocalBinder) iBinder).service();
            service.init();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
