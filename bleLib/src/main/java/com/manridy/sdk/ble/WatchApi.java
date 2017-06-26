package com.manridy.sdk.ble;

import com.manridy.sdk.bean.Clock;
import com.manridy.sdk.bean.Sedentary;
import com.manridy.sdk.bean.User;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.type.AlertType;
import com.manridy.sdk.type.ClockType;
import com.manridy.sdk.type.FindType;
import com.manridy.sdk.type.InfoType;
import com.manridy.sdk.type.PhoneType;

import java.util.List;

/**
 * Created by jarLiao on 17/2/14.
 */

public interface WatchApi {

    void setTimeToNew(BleCallback bleCallback);

    void setClock(ClockType clockType, List<Clock> clock, BleCallback bleCallback);

    void getClockInfo(BleCallback bleCallback);

    void getSportInfo(InfoType infoType, BleCallback bleCallback);

    void setSportTarget(int target, BleCallback bleCallback);

    void clearSportInfo(BleCallback bleCallback);

    void getHeartRateInfo(InfoType infoType, BleCallback bleCallback);

    void getSleepInfo(InfoType infoType, BleCallback bleCallback);

    void getBloodPressureInfo(InfoType infoType, BleCallback bleCallback);

    void getBloodOxygenInfo(InfoType infoType, BleCallback bleCallback);

    void getGpsInfo(BleCallback bleCallback);

    void setUserInfo(User user, BleCallback bleCallback);

    void setInfoAlert(AlertType alertType, BleCallback bleCallback);

    void setPhoneAlert(PhoneType type, String phoneStr, BleCallback bleCallback);

    void setSmsAlertName(PhoneType type, int smsId, String phoneStr, BleCallback bleCallback);

    void setSmsAlertContent(int smsId, byte[] smsContent, BleCallback bleCallback);

    void setAppAlert(BleCallback bleCallback);

    void setLostAlert(boolean onOff,int time, BleCallback bleCallback);

    void setSedentaryAlert(Sedentary sedentary, BleCallback bleCallback);

    void setHeartRateTestOnOff(boolean onOff, BleCallback bleCallback);

    void setHeartRateAutoTestOnOff(boolean onOff, BleCallback bleCallback);

    void setHeartRateDynamicOnOff(boolean onOff, BleCallback bleCallback);

    void setShakeOnOff(boolean onOff, BleCallback bleCallback);

    void getFirmwareVersion(BleCallback bleCallback);

    void getBatteryInfo(BleCallback bleCallback);

    void findDevice(FindType findType, BleCallback bleCallback);

    void setDeviceName(String name, BleCallback bleCallback);

}
