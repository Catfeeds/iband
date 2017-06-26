package com.manridy.iband.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.IbandDB;
import com.manridy.iband.bean.UserModel;
import com.manridy.iband.view.base.BaseActionActivity;
import com.manridy.iband.view.setting.AboutActivity;
import com.manridy.iband.view.setting.AlertActivity;
import com.manridy.iband.view.setting.CameraActivity;
import com.manridy.iband.ui.items.MenuItems;
import com.manridy.iband.R;
import com.manridy.iband.view.setting.FindActivity;
import com.manridy.iband.view.setting.LightActivity;
import com.manridy.iband.view.setting.TargetActivity;
import com.manridy.iband.view.setting.TimeActivity;
import com.manridy.iband.view.setting.UnitActivity;
import com.manridy.iband.view.setting.ViewActivity;
import com.manridy.iband.view.setting.WechatActivity;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.exception.BleException;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.manridy.iband.common.AppGlobal.DATA_USER_HEAD;

/**
 * 设置
 * Created by jarLiao on 17/5/4.
 */

public class SettingActivity extends BaseActionActivity {

    @BindView(R.id.iv_user_icon)
    SimpleDraweeView ivUserIcon;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.rl_user_info)
    RelativeLayout rlUserInfo;
    @BindView(R.id.iv_device_icon)
    ImageView ivDeviceIcon;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.tv_device_bind_state)
    TextView tvDeviceBindState;
    @BindView(R.id.tv_device_connect_state)
    TextView tvDeviceConnectState;
    @BindView(R.id.tv_device_battery)
    TextView tvDeviceBattery;
    @BindView(R.id.tv_un_bind)
    TextView tvUnBind;
    @BindView(R.id.sv_menu)
    ScrollView svMenu;
    @BindView(R.id.rl_device)
    RelativeLayout rlDevice;
    @BindView(R.id.rl_tab)
    LinearLayout rlTab;
    @BindView(R.id.menu_view)
    MenuItems menuView;
    @BindView(R.id.menu_camera)
    MenuItems menuCamera;
    @BindView(R.id.menu_find)
    MenuItems menuFind;
    @BindView(R.id.menu_alert)
    MenuItems menuAlert;
    @BindView(R.id.menu_wechat)
    MenuItems menuWechat;
    @BindView(R.id.menu_light)
    MenuItems menuLight;
    @BindView(R.id.menu_unit)
    MenuItems menuUnit;
    @BindView(R.id.menu_time)
    MenuItems menuTime;
    @BindView(R.id.menu_target)
    MenuItems menuTarget;
    @BindView(R.id.menu_about)
    MenuItems menuAbout;

    private String bindName;
    private int connectState;
    private int curBatteryNum;
    private int curBatteryState;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        registerEventBus();
    }

    @Override
    protected void initVariables() {
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleBar("设置");
        initUser();
        bindName = (String) SPUtil.get(mContext, AppGlobal.DATA_DEVICE_BIND_NAME, "");
        connectState = (int) SPUtil.get(mContext,AppGlobal.DATA_DEVICE_CONNECT_STATE,AppGlobal.DEVICE_UNCONNECT);
        curBatteryNum = (int) SPUtil.get(mContext,AppGlobal.DATA_BATTERY_NUM,-1);
        curBatteryState = (int) SPUtil.get(mContext,AppGlobal.DATA_BATTERY_STATE,-1);
        if (!bindName.isEmpty()){
            showBindDevice();
            showConnectState();
        }
    }

    private void initUser() {
        UserModel curUser = IbandDB.getInstance().getUser();
        if (curUser != null) {
            tvUserName.setText(curUser.getUserName());
        }
        String path = (String) SPUtil.get(mContext,DATA_USER_HEAD,"");
        File file = new File(Environment.getExternalStorageDirectory()+"/iwaer"+path);
        if (file.exists()) {
            ivUserIcon.setImageResource(R.mipmap.set_head);
            ivUserIcon.setImageURI("file://"+file.getPath());
        }
    }

    @Override
    protected void initListener() {
        mIwaerApplication.service.watch.getBatteryInfo(new BleCallback() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onFailure(BleException exception) {

            }
        });
    }

    @OnClick({R.id.menu_view, R.id.menu_camera, R.id.menu_find,
            R.id.menu_alert, R.id.menu_wechat, R.id.menu_light,
            R.id.menu_unit, R.id.menu_time, R.id.menu_target,
            R.id.menu_about,R.id.rl_user_info,R.id.rl_device,
            R.id.iv_user_icon})
    public void onClick(View view) {
        if (isFastDoubleClick()) return;
        switch (view.getId()) {
            case R.id.iv_user_icon:
                startActivity(UserActivity.class);
                break;
            case R.id.rl_user_info:
                startActivity(UserActivity.class);
                break;
            case R.id.rl_device:
                startActivity(DeviceActivity.class);
                break;
            case R.id.menu_view:
                startActivity(ViewActivity.class);
                break;
            case R.id.menu_camera:
                startActivity(CameraActivity.class);
                break;
            case R.id.menu_find:
                startActivity(FindActivity.class);
                break;
            case R.id.menu_alert:
                startActivity(AlertActivity.class);
                break;
            case R.id.menu_wechat:
                startActivity(WechatActivity.class);
                break;
            case R.id.menu_light:
                startActivity(LightActivity.class);
                break;
            case R.id.menu_unit:
                startActivity(UnitActivity.class);
                break;
            case R.id.menu_time:
                startActivity(TimeActivity.class);
                break;
            case R.id.menu_target:
                startActivity(TargetActivity.class);
                break;
            case R.id.menu_about:
                startActivity(AboutActivity.class);
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventMessage event){
        if (event.getWhat() == EventGlobal.DATA_CHANGE_USER) {
            initUser();
        }else if (event.getWhat() == EventGlobal.STATE_DEVICE_BIND) {
            bindName = (String) SPUtil.get(mContext, AppGlobal.DATA_DEVICE_BIND_NAME,"");
            showBindDevice();
        }else if (event.getWhat() == EventGlobal.STATE_DEVICE_UNBIND) {
            tvUnBind.setVisibility(View.VISIBLE);
        }else if (event.getWhat() == EventGlobal.STATE_DEVICE_CONNECT) {
            tvDeviceConnectState.setText("已连接");
            curBatteryNum = (int) SPUtil.get(mContext,AppGlobal.DATA_BATTERY_NUM,-1);
            curBatteryState = (int) SPUtil.get(mContext,AppGlobal.DATA_BATTERY_STATE,-1);
            showBattery();
            Log.d(TAG, "onEventMainThread() called with: event = [  已连接  ]");
        }else if (event.getWhat() == EventGlobal.STATE_DEVICE_DISCONNECT) {
            tvDeviceConnectState.setText("未连接");
            tvDeviceBattery.setText("");
            Log.d(TAG, "onEventMainThread() called with: event = [  未连接  ]");
        }else if (event.getWhat() == EventGlobal.STATE_DEVICE_CONNECTING) {
            tvDeviceConnectState.setText("连接中");
            tvDeviceBattery.setText("");
            Log.d(TAG, "onEventMainThread() called with: event = [  连接中  ]");
        }else if (event.getWhat() == EventGlobal.ACTION_BATTERY_NOTIFICATION){
            curBatteryNum = (int) SPUtil.get(mContext,AppGlobal.DATA_BATTERY_NUM,-1);
            curBatteryState = (int) SPUtil.get(mContext,AppGlobal.DATA_BATTERY_STATE,-1);
            showBattery();
        }
    }

    private void showBindDevice() {
        tvDeviceName.setText(bindName);
        tvDeviceBindState.setText("已绑定");
        tvUnBind.setVisibility(View.GONE);

    }

    private void showBattery() {
        String battery = "";
        if (curBatteryState == 1  && connectState ==1) {
            battery = "充电中";
        }else if(curBatteryNum != -1 && connectState ==1){
            battery = "剩余电量:"+curBatteryNum +"%";
        }
        tvDeviceBattery.setText(battery);
    }

    private void showConnectState(){
        String state = "未连接";
        if (connectState == 1) {
            state = "已连接";
        }else if (connectState == 2){
            state = "连接中";
        }
        tvDeviceConnectState.setText(state);
        showBattery();
    }
}