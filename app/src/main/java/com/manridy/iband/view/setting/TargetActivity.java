package com.manridy.iband.view.setting;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.manridy.applib.utils.SPUtil;
import com.manridy.applib.view.dialog.NumDialog;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.R;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.view.base.BaseActionActivity;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.exception.BleException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 遥控拍照页面
 * Created by jarLiao on 17/5/4.
 */

public class TargetActivity extends BaseActionActivity {

    @BindView(R.id.tb_menu)
    TextView tbMenu;
    @BindView(R.id.tv_step)
    TextView tvStep;
    @BindView(R.id.tv_sleep)
    TextView tvSleep;

    int curStep,curSleep;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_target);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleAndMenu("目标设置", "保存");
        curStep = (int) SPUtil.get(mContext,AppGlobal.DATA_SETTING_TARGET_STEP,8000);
        curSleep = (int) SPUtil.get(mContext,AppGlobal.DATA_SETTING_TARGET_SLEEP,8);
        setUnderline(tvStep);
        setUnderline(tvSleep);
        tvStep.setText(curStep+"步");
        tvSleep.setText(curSleep+"小时");
    }

    @Override
    protected void initListener() {
        tbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress("正在保存中...");
                mIwaerApplication.service.watch.setSportTarget(curStep, new BleCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        SPUtil.put(mContext, AppGlobal.DATA_SETTING_TARGET_STEP,curStep);
                        SPUtil.put(mContext, AppGlobal.DATA_SETTING_TARGET_SLEEP,curSleep);
                        eventSend(EventGlobal.REFRESH_VIEW_STEP);
                        dismissProgress();
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
    }



    @OnClick({R.id.rl_step, R.id.rl_sleep})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_step:
                new NumDialog(mContext, getStepTargets(), curStep+"", "计步目标", new NumDialog.NumDialogListener() {
                    @Override
                    public void getNum(String num) {
                        curStep = Integer.valueOf(num);
                        tvStep.setText(num+"步");
                    }
                }).show();
                break;
            case R.id.rl_sleep:
                new NumDialog(mContext, getSleepTargets(), curSleep+"", "睡眠目标", new NumDialog.NumDialogListener() {
                    @Override
                    public void getNum(String num) {
                        curSleep = Integer.valueOf(num);
                        tvSleep.setText(num+"小时");
                    }
                }).show();
                break;
        }
    }

    private String[] getStepTargets(){
        String[] targets = new String[50];
        for (int i = 0;i<targets.length;i++){
            targets[i] =(i+1)*1000+"";
        }
        return targets;
    }

    private String[] getSleepTargets(){
        String[] targets = new String[24];
        for (int i = 0;i<targets.length;i++){
            targets[i] =(i+1)+"";
        }
        return targets;
    }

    public void setUnderline(TextView view){
        TextPaint textPaint = view.getPaint();
        textPaint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textPaint.setAntiAlias(true);
    }
}
