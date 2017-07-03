package com.manridy.iband.view.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.R;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.ui.items.AlertBigItems;
import com.manridy.iband.view.base.BaseActionActivity;
import com.manridy.sdk.ble.BleCmd;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.exception.BleException;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 遥控拍照页面
 * Created by jarLiao on 17/5/4.
 */

public class WristActivity extends BaseActionActivity {


    @BindView(R.id.tb_menu)
    TextView tbMenu;
    @BindView(R.id.ai_alert)
    AlertBigItems aiAlert;
    private boolean onOff;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_wrist);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleAndMenu("翻腕亮屏", "保存");
        onOff = (boolean) SPUtil.get(mContext, AppGlobal.DATA_ALERT_WRIST, true);
        aiAlert.setAlertCheck(onOff);
    }

    @Override
    protected void initListener() {
        tbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress("正在保存中...");
                mIwaerApplication.service.watch.sendCmd(BleCmd.setWristOnOff(onOff ? 1 : 0), new BleCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        dismissProgress();
                        SPUtil.put(mContext, AppGlobal.DATA_ALERT_WRIST,onOff);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("保存成功");
                            }
                        });
                        finish();
                    }

                    @Override
                    public void onFailure(BleException exception) {
                        dismissProgress();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("保存失败");
                            }
                        });
                    }
                });


            }
        });

        aiAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOff = !onOff;
                aiAlert.setAlertCheck(onOff);
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
