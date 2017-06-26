package com.manridy.applib.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 验证码广播
 * Created by jarLiao on 2016/8/4.
 * 使用:
 * SmsBroadcastReceiver broadcastReceiver = new SmsBroadcastReceiver(handler,6);
 * IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
 * registerReceiver(broadcastReceiver,intentFilter);
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = SmsBroadcastReceiver.class.getSimpleName();
    private Handler handler;
    private  int codeLength = 0;
    public static final int FIND_PHONE_CODE = 10000;

    /**
     *
     * @param handler 通过handler传递验证码
     * @param codeLength 验证码长度
     */
    public SmsBroadcastReceiver(Handler handler, int codeLength) {
        this.handler = handler;
        this.codeLength = codeLength;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();//得到短信内容
        Object[] objects = (Object[]) bundle.get("pdus");
        if (objects != null) {
            String phone = null;
            StringBuffer content = new StringBuffer();
            for (int i = 0; i < objects.length; i++) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) objects[i]);
                phone = sms.getDisplayOriginatingAddress();
                content.append(sms.getDisplayMessageBody());
            }
            Log.e(TAG, "phone:" + phone + "\ncontent:" + content.toString());
            checkCodeAndSend(content.toString());//得到内容验证码
        }
    }

    private void checkCodeAndSend(String content){
        Pattern pattern = Pattern.compile("\\d{"+codeLength+"}");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()){
            String code = matcher.group(0);
            Log.e(TAG, "短信中找到了符合规则的验证码:" + code);
            handler.obtainMessage(FIND_PHONE_CODE, code).sendToTarget();
            Log.e(TAG, "广播接收器接收到短信的时间:" + System.currentTimeMillis());
        } else {
            Log.e(TAG, "短信中没有找到符合规则的验证码");
        }
    }
}
