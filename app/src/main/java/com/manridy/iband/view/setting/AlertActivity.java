package com.manridy.iband.view.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.ui.items.AlertMenuItems;
import com.manridy.iband.R;
import com.manridy.iband.view.base.BaseActionActivity;
import com.manridy.iband.view.alert.AlertMenuActivity;
import com.manridy.iband.view.alert.ClockActivity;
import com.manridy.iband.view.alert.PhoneActivity;
import com.manridy.iband.view.alert.SedentaryActivity;
import com.manridy.iband.view.alert.SmsActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 提醒功能页面
 * Created by jarLiao on 17/5/4.
 */

public class AlertActivity extends BaseActionActivity {

    @BindView(R.id.menu_phone)
    AlertMenuItems menuPhone;
    @BindView(R.id.menu_sms)
    AlertMenuItems menuSms;
    @BindView(R.id.menu_sedentary)
    AlertMenuItems menuSedentary;
    @BindView(R.id.menu_clock)
    AlertMenuItems menuClock;
    @BindView(R.id.bt_alert_more)
    Button btAlertMore;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_alert);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        registerEventBus();
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleBar("提醒功能");
        initState();
    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.menu_phone, R.id.menu_sms, R.id.menu_sedentary, R.id.menu_clock, R.id.bt_alert_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_phone:
                startActivity(PhoneActivity.class);
                break;
            case R.id.menu_sms:
                startActivity(SmsActivity.class);
                break;
            case R.id.menu_sedentary:
                startActivity(SedentaryActivity.class);
                break;
            case R.id.menu_clock:
                startActivity(ClockActivity.class);
                break;
            case R.id.bt_alert_more:
                startActivity(AlertMenuActivity.class);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventMessage event) {
        if (event.getWhat() == EventGlobal.DATA_CHANGE_MENU) {
            initState();
        }
    }

    private void initState() {
        boolean phoneEnable = (boolean) SPUtil.get(mContext, AppGlobal.DATA_ALERT_PHONE,false);
        boolean smsEnable = (boolean) SPUtil.get(mContext, AppGlobal.DATA_ALERT_SMS,false);
        boolean sedentaryEnable = (boolean) SPUtil.get(mContext, AppGlobal.DATA_ALERT_SEDENTARY,false);
        boolean clockEnable = (boolean) SPUtil.get(mContext, AppGlobal.DATA_ALERT_CLOCK,false);
        menuPhone.setAlertState(phoneEnable);
        menuSms.setAlertState(smsEnable);
        menuSedentary.setAlertState(sedentaryEnable);
        menuClock.setAlertState(clockEnable);
    }
}
