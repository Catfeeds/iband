package com.manridy.iband.view.setting;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.manridy.applib.utils.VersionUtil;
import com.manridy.iband.IbandApplication;
import com.manridy.iband.IbandDB;
import com.manridy.iband.R;
import com.manridy.iband.ui.items.HelpItems;
import com.manridy.iband.view.TestActivity;
import com.manridy.iband.view.base.BaseActionActivity;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.android.internal.telephony.ITelephony;


/**
 * 遥控拍照页面
 * Created by jarLiao on 17/5/4.
 */

public class AboutActivity extends BaseActionActivity {

    @BindView(R.id.hi_update)
    HelpItems hiUpdate;
    @BindView(R.id.hi_help)
    HelpItems hiHelp;
    @BindView(R.id.hi_about)
    HelpItems hiAbout;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleBar("关于");
        tvVersion.setText("当前版本:V"+VersionUtil.getVersionName(mContext));
    }

    @Override
    protected void initListener() {
        hiUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UpdateActivity.class);
            }
        });

        hiHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        hiHelp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                IbandApplication.getIntance().service.watch.sendCmd(new byte[]{(byte) 0xfc,0x0c,0x03});
                return true;
            }
        });
        hiAbout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(TestActivity.class);
                return true;
            }
        });

    }



}
