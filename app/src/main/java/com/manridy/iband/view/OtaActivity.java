package com.manridy.iband.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.manridy.applib.utils.FileUtil;
import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.R;
import com.manridy.iband.SyncAlert;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.service.DfuService;
import com.manridy.iband.ui.CircularView;
import com.manridy.iband.view.base.BaseActionActivity;
import com.manridy.sdk.callback.BleConnectCallback;
import com.manridy.sdk.exception.BleException;

import butterknife.BindView;
import butterknife.ButterKnife;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;


/**
 * 关于
 * Created by jarLiao on 16/11/21.
 */

public class OtaActivity extends BaseActionActivity {
    @BindView(R.id.cv_ota)
    CircularView cvOta;
    @BindView(R.id.iv_ota)
    ImageView ivOta;
    @BindView(R.id.tv_ota_result)
    TextView tvOtaResult;
    @BindView(R.id.tv_ota_ok)
    TextView tvOtaOk;
    @BindView(R.id.tv_ota_progress)
    TextView tvOtaProgress;
    private DfuServiceController controller;

//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            Log.d(TAG, "handleMessage() called with: msg = [" + msg.what + "]");
//            if (msg.what <=100 && msg.what >0) {
//                tvOtaProgress.setText("已完成" + msg.what + "%");
//                cvOta.setProgress((float)msg.what)
//                        .invaliDate();
//            }else {
//                tvOtaProgress.setText("");
//                cvOta.setProgress(0)
//                        .invaliDate();
//            }
//            if (msg.what < 25){
//                handler.sendEmptyMessageDelayed(progress++,600);
//            }else if (msg.what == 75 ){
//                ivOta.setVisibility(View.GONE);
//                tvOtaResult.setVisibility(View.VISIBLE);
//                tvOtaResult.setText("重连设备中");
//                handler.sendEmptyMessageDelayed(progress++,1000);
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        connect();
//                    }
//                },2500);
//            }else if (msg.what >75 &&msg.what <100){
//                handler.sendEmptyMessageDelayed(progress++,1000);
//            }
//        }
//    };

    private void connect() {
        mIwaerApplication.service.initConnect(false,new BleConnectCallback() {
            @Override
            public void onConnectSuccess() {
                SyncAlert.getInstance(mContext).sync();
            }

            @Override
            public void onConnectFailure(BleException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("连接失败，请重新尝试！");
                    }
                });
            }
        });
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_device_ota);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        start();
    }

    @Override
    protected void initListener() {
        tvOtaOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvOtaResult.getText().toString().equals("升级失败")) {
                    finish();
                }else {
                    int state = (int) SPUtil.get(mContext, AppGlobal.DATA_DEVICE_CONNECT_STATE, AppGlobal.DEVICE_UNCONNECT);
                    if (state == 1) {
                        showProgress("正在同步设置...");
                        SyncAlert.getInstance(mContext).sync();
                    }else {
                        showProgress("正在连接设备...");
                        connect();
                    }
                }
            }
        });

        SyncAlert.getInstance(mContext).setSyncAlertListener(new SyncAlert.OnSyncAlertListener() {
            @Override
            public void onResult(final boolean isSuccess) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        showToast(isSuccess?"同步成功":"同步失败");
                        finish();
                    }
                });
            }
        });
    }


    private void start() {
        String mac = (String) SPUtil.get(mContext, AppGlobal.DATA_DEVICE_BIND_MAC,"");
        String name = (String) SPUtil.get(mContext,AppGlobal.DATA_DEVICE_BIND_NAME,"");
        final DfuServiceInitiator starter = new DfuServiceInitiator(mac)
                .setDeviceName(name)
                .setDisableNotification(true)
                .setKeepBond(true);
        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
        starter.setZip(null, FileUtil.getSdCardPath()+"/ota.zip");
        controller = starter.start(this,DfuService.class);
    }


    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
    }

    private DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            Log.d(TAG, "onDeviceConnecting() called with: deviceAddress = [" + deviceAddress + "]");
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            Log.d(TAG, "onDfuProcessStarting() called with: deviceAddress = [" + deviceAddress + "]");
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            super.onProgressChanged(deviceAddress, percent, speed, avgSpeed, currentPart, partsTotal);
            cvOta.setProgress((float) percent)
                    .invaliDate();
            tvOtaProgress.setText("已完成" + percent + "%");
            if (percent == 100) {
                cvOta.setProgress(0);
                ivOta.setVisibility(View.GONE);
                tvOtaProgress.setVisibility(View.GONE);
                tvOtaOk.setVisibility(View.VISIBLE);
                tvOtaResult.setVisibility(View.VISIBLE);
                tvOtaResult.setText("升级成功");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        EventBus.getDefault().post(new EventMessage()));
                    }
                }, 1000);
            }
            Log.d(TAG, "onProgressChanged() called with: deviceAddress = [" + deviceAddress + "], percent = [" + percent + "], speed = [" + speed + "], avgSpeed = [" + avgSpeed + "], currentPart = [" + currentPart + "], partsTotal = [" + partsTotal + "]");
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            super.onError(deviceAddress, error, errorType, message);
            cvOta.setProgress(0f).invaliDate();
            ivOta.setVisibility(View.GONE);
            tvOtaProgress.setVisibility(View.GONE);
            tvOtaOk.setVisibility(View.VISIBLE);
            tvOtaResult.setVisibility(View.VISIBLE);
            tvOtaResult.setTextColor(Color.parseColor("#e64a19"));
            tvOtaResult.setText("升级失败");
            Log.d(TAG, "onError() called with: deviceAddress = [" + deviceAddress + "], error = [" + error + "], errorType = [" + errorType + "], message = [" + message + "]");
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (controller != null) {
            controller.pause();
        }
    }
}
