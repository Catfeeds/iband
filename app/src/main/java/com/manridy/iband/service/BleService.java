package com.manridy.iband.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.manridy.applib.utils.LogUtil;
import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.SyncAlert;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.R;
import com.manridy.iband.view.MainActivity;
import com.manridy.sdk.Watch;
import com.manridy.sdk.callback.BleActionListener;
import com.manridy.sdk.callback.BleConnectCallback;
import com.manridy.sdk.callback.BleNotifyListener;
import com.manridy.sdk.exception.BleException;
import com.manridy.sdk.scan.TimeMacScanCallback;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.manridy.iband.common.AppGlobal.DEVICE_CONNECTED;
import static com.manridy.iband.common.AppGlobal.DEVICE_CONNECTING;
import static com.manridy.iband.common.AppGlobal.DEVICE_UNCONNECT;
import static com.manridy.iband.common.EventGlobal.ACTION_BATTERY_NOTIFICATION;
import static com.manridy.iband.common.EventGlobal.ACTION_CALL_END;
import static com.manridy.iband.common.EventGlobal.ACTION_CAMERA_CAPTURE;
import static com.manridy.iband.common.EventGlobal.ACTION_CAMERA_EXIT;
import static com.manridy.iband.common.EventGlobal.ACTION_FIND_PHONE_START;
import static com.manridy.iband.common.EventGlobal.ACTION_FIND_PHONE_STOP;
import static com.manridy.iband.common.EventGlobal.ACTION_FIND_WATCH_STOP;
import static com.manridy.iband.common.EventGlobal.ACTION_HR_TESTED;
import static com.manridy.iband.common.EventGlobal.ACTION_HR_TESTING;
import static com.manridy.sdk.BluetoothLeManager.ACTION_DATA_AVAILABLE;
import static com.manridy.sdk.BluetoothLeManager.ACTION_GATT_CONNECT;
import static com.manridy.sdk.BluetoothLeManager.ACTION_GATT_DISCONNECTED;
import static com.manridy.sdk.BluetoothLeManager.ACTION_GATT_RECONNECT;
import static com.manridy.sdk.BluetoothLeManager.ACTION_NOTIFICATION_ENABLE;
import static com.manridy.sdk.BluetoothLeManager.ACTION_SERVICES_DISCOVERED;

/**
 * 蓝牙后台服务
 * Created by jarLiao .
 */

public class BleService extends Service {
    private String TAG = "BleService";
    public Watch watch;

    public void init(){
        watch = Watch.getInstance(this);
        watch.setActionListener(actionListener);
        watch.setStepNotifyListener(notifyListener);
        watch.setRunNotifyListener(notifyListener);
        initBroadcast();
        initConnect(true);
    }

    public void initConnect(boolean isScan,final BleConnectCallback bleConnectCallback) {
        if (!watch.isBluetoothEnable()) {
            SPUtil.put(BleService.this,AppGlobal.DATA_DEVICE_CONNECT_STATE,DEVICE_UNCONNECT);
        }
        final String mac = (String) SPUtil.get(this, AppGlobal.DATA_DEVICE_BIND_MAC,"");
        if (mac==null || mac.isEmpty()) {
            bleConnectCallback.onConnectFailure(new BleException(999,"mac is null!"));
            return;
        }
        if (isScan) {
            scanAndConnect(mac,bleConnectCallback);
        }else{
            watch.connect(mac,true,bleConnectCallback);
        }
    }

    private void scanAndConnect(final String mac, final BleConnectCallback bleConnectCallback){
        watch.startScan(new TimeMacScanCallback(mac,3000) {
            @Override
            public void onDeviceFound(boolean isFound, BluetoothDevice device) {
                if (isFound) {
                    watch.connect(mac,true,bleConnectCallback);
                }else {
                    bleConnectCallback.onConnectFailure(new BleException(1000,"no find device!"));
                }
            }
        });
    }

    public void initConnect(boolean isScan){
        initConnect(isScan,new BleConnectCallback() {
            @Override
            public void onConnectSuccess() {
                Log.d(TAG, "onConnectSuccess() called");
            }

            @Override
            public void onConnectFailure(BleException exception) {
                Log.d(TAG, "onConnectFailure() called with: exception = [" + exception.toString() + "]");
            }
        });
    }

    private void showNotification(int connectState) {
        try {
            String state = getState(connectState);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.app_icon)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(state)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                    .setOngoing(false);//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);
            mBuilder.setContentIntent(pendingIntent);
            Notification notification = mBuilder.build();
            startForeground(1, notification);
        }catch (Exception e){
            e.toString();
        }
    }

    @NonNull
    private String getState(int connectState) {
        String state = "手环未连接";
        if (connectState == 1){
            state = "手环已连接";
        }else if (connectState == 2){
            state = "手环连接中";
        }
        return state;
    }

    public void stopNotification(){
        stopForeground(true);
    }

    public void initBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_GATT_CONNECT);
        filter.addAction(ACTION_GATT_DISCONNECTED);
        filter.addAction(ACTION_GATT_RECONNECT);
        filter.addAction(ACTION_SERVICES_DISCOVERED);
        filter.addAction(ACTION_NOTIFICATION_ENABLE);
        filter.addAction(ACTION_DATA_AVAILABLE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(bleReceiver,filter);
    }

    private BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case ACTION_GATT_CONNECT:
                    LogUtil.e(TAG,"蓝牙状态----蓝牙已连接");
//                    EventBus.getDefault().post(new EventMessage(EventGlobal.STATE_DEVICE_CONNECT));
                    break;
                case ACTION_GATT_RECONNECT:
                    LogUtil.e(TAG,"蓝牙状态----蓝牙重连中");
                    String mac = (String) SPUtil.get(BleService.this,AppGlobal.DATA_DEVICE_BIND_MAC,"");
                    if (mac!=null && !mac.isEmpty()) {
                        SPUtil.put(BleService.this, AppGlobal.DATA_DEVICE_CONNECT_STATE, DEVICE_CONNECTING);
                        showNotification(2);
                    }
                    EventBus.getDefault().post(new EventMessage(EventGlobal.STATE_DEVICE_CONNECTING));
                    break;
                case ACTION_GATT_DISCONNECTED:
                    LogUtil.e(TAG,"蓝牙状态----蓝牙已断开");
                    String mac2 = (String) SPUtil.get(BleService.this,AppGlobal.DATA_DEVICE_BIND_MAC,"");
                    if (mac2!=null && !mac2.isEmpty()) {
                        showNotification(0);
                    }
                    SPUtil.put(BleService.this,AppGlobal.DATA_DEVICE_CONNECT_STATE,DEVICE_UNCONNECT);
                    EventBus.getDefault().post(new EventMessage(EventGlobal.STATE_DEVICE_DISCONNECT));
                    break;
                case ACTION_SERVICES_DISCOVERED:
                    LogUtil.e(TAG,"蓝牙状态----发现服务");
                    break;
                case ACTION_NOTIFICATION_ENABLE:
                    LogUtil.e(TAG,"蓝牙状态----打开通知");
                    showNotification(1);
                    SPUtil.put(BleService.this,AppGlobal.DATA_DEVICE_CONNECT_STATE,DEVICE_CONNECTED);
                    EventBus.getDefault().post(new EventMessage(EventGlobal.STATE_DEVICE_CONNECT));
                    break;
                case ACTION_DATA_AVAILABLE:
                    final byte[] data = intent.getByteArrayExtra("BLUETOOTH_DATA");
//                    LogUtil.e(TAG,"蓝牙状态----数据:"+ BitUtil.parseByte2HexStr(data));
                    break;
            }
        }
    };

    BleActionListener actionListener = new BleActionListener() {
        @Override
        public void onAction(int type, Object o) {
            switch (type) {
                case ACTION_CAMERA_EXIT:
                    EventBus.getDefault().post(new EventMessage(ACTION_CAMERA_EXIT));
                    break;
                case ACTION_CAMERA_CAPTURE:
                    EventBus.getDefault().post(new EventMessage(ACTION_CAMERA_CAPTURE));
                    break;
                case ACTION_FIND_PHONE_START:
                    EventBus.getDefault().post(new EventMessage(ACTION_FIND_PHONE_START));
                    break;
                case ACTION_FIND_PHONE_STOP:
                    EventBus.getDefault().post(new EventMessage(ACTION_FIND_PHONE_STOP));
                    break;
                case ACTION_FIND_WATCH_STOP:
                    EventBus.getDefault().post(new EventMessage(ACTION_FIND_WATCH_STOP));
                    break;
                case ACTION_BATTERY_NOTIFICATION:
                    SyncAlert.getInstance(BleService.this).parseBattery(o);
                    EventBus.getDefault().post(new EventMessage(ACTION_BATTERY_NOTIFICATION));
                    break;
                case ACTION_HR_TESTED:
                    EventBus.getDefault().post(new EventMessage(ACTION_HR_TESTED));
                    break;
                case ACTION_HR_TESTING:
                    EventBus.getDefault().post(new EventMessage(ACTION_HR_TESTING));
                    break;
                case ACTION_CALL_END:
//                    rejectCall(BleService.this);
                    end(BleService.this);
//                    endCall(BleService.this);
                    break;
            }
        }
    };

    public void end(Context context){
        try {
            Method getITelephonyMethod =TelephonyManager.class
                    .getDeclaredMethod("getITelephony", (Class[]) null);
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);//监听电话服务
            getITelephonyMethod.setAccessible(true);
            ITelephony  mITelephony = (ITelephony) getITelephonyMethod.invoke(tm,
                        (Object[]) null);
            // 拒接来电
            mITelephony.endCall();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public boolean endCall(Context context) {
        boolean callSuccess = false;
        try {
        Method getITelephonyMethod =TelephonyManager.class
                .getDeclaredMethod("getITelephony", (Class[]) null);
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);//监听电话服务
            getITelephonyMethod.setAccessible(true);
        ITelephony telephonyService = (ITelephony) getITelephonyMethod.invoke(tm,
                (Object[]) null);
            if (telephonyService != null) {
                callSuccess = telephonyService.endCall();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        if (callSuccess == false) {
            Executor eS = Executors.newSingleThreadExecutor();
            eS.execute(new Runnable() {
                @Override
                public void run() {
                    disconnectCall();
                }
            });
            callSuccess = true;
        }
        return callSuccess;
    }

    private boolean disconnectCall() {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("service call phone 5 \n");
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }
        return true;
    }

    // 使用endCall挂断不了，再使用killCall反射调用再挂一次
    public static boolean killCall(Context context) {
        try {
            // Get the boring old TelephonyManager
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
       Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
        methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
        Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
        Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception ex) { // Many things can go wrong with reflection calls
            return false;
        }
        return true;
    }


    BleNotifyListener notifyListener = new BleNotifyListener() {
        @Override
        public void onNotify(Object o) {
            EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_SYNC_HISTORY));
        }
    };



    public class LocalBinder extends Binder {
        public BleService service(){
            return BleService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        IBinder result = null;
        if (null == result){
            result = new LocalBinder();
        }
        return result;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

}
