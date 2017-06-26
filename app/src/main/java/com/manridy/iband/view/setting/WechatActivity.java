package com.manridy.iband.view.setting;

import android.graphics.Color;
import android.os.Bundle;

import com.manridy.iband.view.base.BaseActionActivity;
import com.manridy.iband.R;

import butterknife.ButterKnife;

/**
 * 遥控拍照页面
 * Created by jarLiao on 17/5/4.
 */

public class WechatActivity extends BaseActionActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_wechat);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleBar("微信运动");
    }

    @Override
    protected void initListener() {

    }

}
