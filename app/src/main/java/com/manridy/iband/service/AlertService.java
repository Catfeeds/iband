package com.manridy.iband.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.manridy.applib.utils.LogUtil;
import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.IbandApplication;
import com.manridy.sdk.ble.BleCmd;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.exception.BleException;
import com.manridy.sdk.type.PhoneType;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * 提醒服务
 * Created by Administrator on 2016/8/15.
 */
public class AlertService extends Service {
    private String TAG = AlertService.class.getSimpleName();

    private IbandApplication iwaerApplication;
    private AlertReceiver alertReceiver;
    private List<byte[]> smsList = new ArrayList<>();
    private String smsContent;
    private int smsId = 1;
    private boolean smsConfirm;

    @Override
    public void onCreate() {
        super.onCreate();
        iwaerApplication = (IbandApplication) getApplication();
        initReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    private void initReceiver() {
        IntentFilter filter = getIntentFilter();//初始化监听广播
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        alertReceiver = new AlertReceiver();
        registerReceiver(alertReceiver,filter);
    }

    @NonNull
    private IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        return filter;
    }

    private class AlertReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.provider.Telephony.SMS_RECEIVED")){
                sms(context, intent);
            }else if(action.equals("android.intent.action.PHONE_STATE")){
                TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);//监听电话服务
                tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
            }else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if (state == BluetoothAdapter.STATE_OFF) {
                    EventBus.getDefault().post(new EventMessage(EventGlobal.STATE_CHANGE_BLUETOOTH_OFF));
                }else if (state == BluetoothAdapter.STATE_TURNING_ON) {
                    EventBus.getDefault().post(new EventMessage(EventGlobal.STATE_CHANGE_BLUETOOTH_ON_RUNING));
                }else if (state == BluetoothAdapter.STATE_ON) {
                    EventBus.getDefault().post(new EventMessage(EventGlobal.STATE_CHANGE_BLUETOOTH_ON));
                }
                Log.e(TAG, "onReceive() called with: ACTION_STATE_CHANGED, state = [" + state + "]");
            }
        }
    }


    //电话状态监听
    final PhoneStateListener listener=new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                boolean isPhoneAlert = (boolean) SPUtil.get(AlertService.this, AppGlobal.DATA_ALERT_PHONE,false);//来电提醒
                if (!isPhoneAlert) return;

                switch(state){
                    //电话等待接听
                    case TelephonyManager.CALL_STATE_RINGING:
                        phone(incomingNumber);
                        break;
                    //电话接听
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        iwaerApplication.service.watch.sendCmd(BleCmd.setInfoAlert(3));
                        LogUtil.i("PhoneReceiver", "CALL IN ACCEPT :" + incomingNumber);
                        break;
                    //电话挂机
                    case TelephonyManager.CALL_STATE_IDLE:
                        iwaerApplication.service.watch.sendCmd(BleCmd.setInfoAlert(3));
                        LogUtil.i("PhoneReceiver", "CALL IDLE");
                        break;
                }
            }
        };



    private void sms(Context context, Intent intent) {
        try {
            boolean isSmsAlert = (boolean) SPUtil.get(context, AppGlobal.DATA_ALERT_SMS,false);//短信提醒
            if (!isSmsAlert) return;
            Bundle bundle = intent.getExtras();
            if (bundle!=null) {
                Object pdusData[] = (Object[]) bundle.get("pdus");//将pdus里面的内容转化成Object[]数组
                SmsMessage[] msg = new SmsMessage[pdusData.length];//解析短信
                for (int i = 0; i < msg.length; i++) {
                    byte pdus[] = (byte[]) pdusData[i];
                    msg[i] = SmsMessage.createFromPdu(pdus);
                }
                StringBuffer content = new StringBuffer();//获取短信内容
                StringBuffer phoneNumber = new StringBuffer();//获取地址
                for (SmsMessage temp : msg) {//分析短信具体参数
                    content.append(temp.getMessageBody());
                    if (!temp.getOriginatingAddress().equals(phoneNumber.toString())) {
                        phoneNumber.append(temp.getOriginatingAddress());
                    }
                }
                smsId = smsId > 63 ? 1 : smsId++;
                smsConfirm = false;
                if (phoneNumber.toString().isEmpty()) {//号码是否为空
                    iwaerApplication.service.watch.setSmsAlertName(PhoneType.PHONE_NAME, smsId, "未知号码",smsBleCallback);//发送短信提醒
                }else {
                    String name = getSmsFromPhone(AlertService.this,phoneNumber.toString());
                    if (name == null ||name.isEmpty()) {
                        iwaerApplication.service.watch.setSmsAlertName(PhoneType.PHONE_NUMBER,smsId,phoneNumber.toString(),smsBleCallback);//发送短信提醒
                    }else{
                        iwaerApplication.service.watch.setSmsAlertName(PhoneType.PHONE_NUMBER,smsId,name,smsBleCallback);//发送短信提醒
                        System.out.println("发送者名称："+name);
                    }
                }
                smsContent = content.toString();
                smsList = getCmdList(smsContent);
                System.out.println("发送者号码："+phoneNumber.toString()+"  短信内容："+content.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
            SPUtil.put(AlertService.this,AppGlobal.DATA_ALERT_PHONE,false);
//                    EventBus.getDefault().post(new MessageEvent(ACTION_REFRESH_ALERT));
            Log.e(TAG, "短信提醒: 没有获得通讯录权限异常");
        }
    }

     BleCallback smsBleCallback = new BleCallback() {
        @Override
        public void onSuccess(Object o) {
            if (smsList.size()>0 && smsConfirm) {
                smsList.remove(0);
            }
            smsConfirm = true;
            if (smsList.size()>0) {
                iwaerApplication.service.watch.setSmsAlertContent(smsId,smsList.get(0),smsBleCallback);
            }else {
                Log.d(TAG, "短信发送完成");
            }
        }

        @Override
        public void onFailure(BleException exception) {
            Log.d(TAG, "onFailure() called with: exception = [" + exception.toString() + "]");
        }
    };

    private void phone(String incomingNumber) {
        String name;
        if (incomingNumber == null || incomingNumber.isEmpty()) {
            iwaerApplication.service.watch.sendCmd(BleCmd.setPhoneAlert(1,"未知号码"));
        }else{
            try {
                name = getSmsFromPhone(AlertService.this,incomingNumber);
                if (null == name) {
                    iwaerApplication.service.watch.sendCmd(BleCmd.setPhoneAlert(2,incomingNumber));
                }else {
                    iwaerApplication.service.watch.sendCmd(BleCmd.setPhoneAlert(1,name));
                }
                LogUtil.i("PhoneReceiver", "CALL IN RINGING :" + incomingNumber+"NAME : "+name);
            }catch (Exception e){
                e.printStackTrace();
                SPUtil.put(AlertService.this, AppGlobal.DATA_ALERT_PHONE,false);
//                                EventBus.getDefault().post(new MessageEvent(ACTION_REFRESH_ALERT));
                Log.e(TAG, "来电提醒: 没有获得通讯录权限异常");
            }
        }
    }

    public String getSmsFromPhone(Context context,String phoneNumber) {
        Uri personUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phoneNumber);
        Cursor cur = context.getContentResolver().query(personUri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME },
                null, null, null );
        if (cur != null && cur.moveToFirst()) {
            int nameIdx = cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cur.getString(nameIdx);
            cur.close();
            return name;
        }
        return null;
    }

    private List<byte[]> getCmdList(String content) throws UnsupportedEncodingException {
        List<byte[]> bytes = new ArrayList<>();
        byte[] contexts = content.getBytes("UnicodeBigUnmarked");//string转uicode编码 大端在前
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
    public void onDestroy() {
        super.onDestroy();
        if (alertReceiver != null) {
            unregisterReceiver(alertReceiver);
        }
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
