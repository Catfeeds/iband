package com.manridy.iband.view.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaygoo.widget.RangeSeekbar;
import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.R;
import com.manridy.iband.view.base.BaseActionActivity;
import com.manridy.sdk.ble.BleCmd;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 遥控拍照页面
 * Created by jarLiao on 17/5/4.
 */

public class LightActivity extends BaseActionActivity {

    @BindView(R.id.tv_light_num)
    TextView tvLightNum;
    @BindView(R.id.rs_light)
    RangeSeekbar rsLight;
    @BindView(R.id.tv_reduce)
    TextView tvReduce;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.tb_back)
    ImageView tbBack;
    @BindView(R.id.tb_title)
    TextView tbTitle;
    int curLight;
    int oldLight = -1;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_light);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleBar("亮度调节");
        curLight = (int) SPUtil.get(mContext,AppGlobal.DATA_SETTING_LIGHT,1);
        curLight =  curLight>2 ? 1:curLight;
        rsLight.setValue(curLight);
        tvLightNum.setText(getLightText(curLight));
    }

    @Override
    protected void initListener() {
        rsLight.setOnRangeChangedListener(new RangeSeekbar.OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekbar rangeSeekbar, float v, float v1, boolean b) {
                Log.d(TAG, "onRangeChanged() called with: rangeSeekbar = [" + rangeSeekbar + "], v = [" + v + "], v1 = [" + v1 + "], b = [" + b + "]");
                if (!b){
                    curLight = (int)(v*2) ;
                    tvLightNum.setText(getLightText(curLight));
                }else {
                    curLight = (int)v;
                    tvLightNum.setText(getLightText(curLight));
                }
                if (curLight != oldLight) {
                    SPUtil.put(mContext, AppGlobal.DATA_SETTING_LIGHT,curLight);
                    mIwaerApplication.service.watch.sendCmd(BleCmd.setLight(curLight));
                }
                oldLight = curLight;
            }
        });
    }

    @OnClick({R.id.tv_reduce, R.id.tv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_reduce:
                curLight = curLight <= 0 ? 0 : curLight-1;
                rsLight.setValue(curLight);
                break;
            case R.id.tv_add:
                curLight = curLight >= 2 ? 2 : curLight+1;
                rsLight.setValue(curLight);
                break;
        }
    }


    private String getLightText(int curLight){
        String text = "中";
        switch (curLight) {
            case 0:
                text = "低";
                break;
            case 2:
                text = "高";
                break;
        }
        return text;
    }

}
