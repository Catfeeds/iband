package com.manridy.iband.common;

public class EventGlobal {
    //数据改变
    public static final int DATA_CHANGE_USER = 1000;
    public static final int DATA_CHANGE_MINUTE = 1100;
    public static final int DATA_CHANGE_MENU = 1200;
    public static final int DATA_CHANGE_UNIT = 1300;
    //运动数据加载
    public static final int DATA_LOAD_STEP = 1800;
    public static final int DATA_LOAD_SLEEP = 1801;
    public static final int DATA_LOAD_HR = 1802;
    public static final int DATA_LOAD_BP = 1803;
    public static final int DATA_LOAD_BO = 1804;
    public static final int DATA_LOAD_RUN = 1805;
    //运动历史数据加载
    public static final int DATA_LOAD_STEP_HISTORY = 1810;
    public static final int DATA_LOAD_SLEEP_HISTORY = 1811;
    public static final int DATA_LOAD_HR_HISTORY = 1812;
    public static final int DATA_LOAD_BP_HISTORY = 1813;
    public static final int DATA_LOAD_BO_HISTORY = 1814;
    //运动历史数据同步
    public static final int DATA_SYNC_HISTORY = 1900;
    //界面刷新
    public static final int REFRESH_VIEW_STEP = 2800;
    public static final int REFRESH_VIEW_SLEEP = 2801;
    public static final int REFRESH_VIEW_HR = 2802;
    public static final int REFRESH_VIEW_BP = 2803;
    public static final int REFRESH_VIEW_BO = 2804;
    public static final int REFRESH_VIEW_RUN = 2806;
    public static final int REFRESH_VIEW_ALL = 2805;
    //历史界面刷新
    public static final int REFRESH_VIEW_STEP_HISTORY  = 2810;
    public static final int REFRESH_VIEW_SLEEP_HISTORY  = 2811;
    public static final int REFRESH_VIEW_HR_HISTORY  = 2812;
    public static final int REFRESH_VIEW_BP_HISTORY  = 2813;
    public static final int REFRESH_VIEW_BO_HISTORY  = 2814;
    //设备状态改变
    public static final int STATE_DEVICE_BIND = 2000;
    public static final int STATE_DEVICE_UNBIND = 2001;
    public static final int STATE_DEVICE_CONNECT = 2002;
    public static final int STATE_DEVICE_DISCONNECT = 2003;
    public static final int STATE_DEVICE_CONNECTING = 2004;
    public static final int STATE_DEVICE_BIND_FAIL = 2005;
    //设备动作触发
    public static final int ACTION_CAMERA_EXIT = 2580;
    public static final int ACTION_CAMERA_CAPTURE = 2581;
    public static final int ACTION_FIND_PHONE_STOP = 1600;
    public static final int ACTION_FIND_PHONE_START = 1601;
    public static final int ACTION_FIND_WATCH_STOP = 1602;
    public static final int ACTION_BATTERY_NOTIFICATION = 1660;
    public static final int ACTION_HR_TESTING = 902;
    public static final int ACTION_HR_TESTED = 900;
    public static final int ACTION_CALL_END = 830;
    public static final int ACTION_CALL_RUN = 831;
    //通知栏动作
    public static final int ACTION_BLUETOOTH_OPEN = 2400;
    public static final int ACTION_DEVICE_CONNECT = 2401;
    //消息通知
    public static final int MSG_SEDENTARY_TOAST = 3000;
    public static final int MSG_CLOCK_TOAST = 3001;
    public static final int MSG_OTA_TOAST = 3002;
    //蓝牙状态改变
    public static final int STATE_CHANGE_BLUETOOTH_ON = 4000;
    public static final int STATE_CHANGE_BLUETOOTH_OFF = 4001;
    public static final int STATE_CHANGE_BLUETOOTH_ON_RUNING = 4002;
}