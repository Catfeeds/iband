package com.manridy.iband;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.service.AlertService;
import com.manridy.iband.service.BleService;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import org.litepal.LitePalApplication;

import static com.manridy.iband.common.AppGlobal.DEVICE_STATE_UNCONNECT;

/**
 * 应用全局
 * Created by jarLiao on 17/5/16.
 */

public class IbandApplication extends Application {
    private static final String TAG = IbandApplication.class.getSimpleName();
    private static IbandApplication intance;
    public BleService service;

    @Override
    public void onCreate() {
        super.onCreate();
        intance = this;
        SPUtil.put(this, AppGlobal.DATA_DEVICE_CONNECT_STATE, DEVICE_STATE_UNCONNECT);
        LitePalApplication.initialize(this);//初始化数据库
        Fresco.initialize(this);//初始化图片加载
        initBleSevrice();//初始化蓝牙服务
        initAlertService();//初始化提醒服务
        initBugly();//初始化bugly
//        CrashHandler.getInstance().init(intance);
    }

    private void initBleSevrice() {
        Intent bindIntent = new Intent(this,BleService.class);
        bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void initAlertService() {
        startService(new Intent(this,AlertService.class));
    }

    private void initBugly() {
        Bugly.init(getApplicationContext(), "33139ca6ea",false);
        Beta.initDelay = 2 * 1000;//延迟两秒检测版本信息
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
            Log.d(TAG, "onServiceDisconnected() called with: name = [" + name + "]");
        }
    };
}
