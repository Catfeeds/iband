package com.manridy.iband;

import android.content.Context;
import android.util.Log;

import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.bean.ClockModel;
import com.manridy.iband.bean.SedentaryModel;
import com.manridy.iband.bean.UserModel;
import com.manridy.iband.bean.ViewModel;
import com.manridy.iband.common.AppGlobal;
import com.manridy.sdk.Watch;
import com.manridy.sdk.bean.Clock;
import com.manridy.sdk.bean.Sedentary;
import com.manridy.sdk.bean.User;
import com.manridy.sdk.ble.BleCmd;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.exception.BleException;
import com.manridy.sdk.type.ClockType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarLiao on 17/5/25.
 */

public class SyncAlert {

    private Context mContext;
    private Watch watch;
    private int syncIndex;
    private int errorNum;
    private OnSyncAlertListener syncAlertListener;
    private static SyncAlert instance;

    public interface OnSyncAlertListener{
        void onResult(boolean isSuccess);
    }

    public void setSyncAlertListener(OnSyncAlertListener syncAlertListener) {
        this.syncAlertListener = syncAlertListener;
    }

    private SyncAlert(Context context) {
        mContext = context.getApplicationContext();
        watch = IbandApplication.getIntance().service.watch;
    }

    public static SyncAlert getInstance(Context context) {
        if (instance == null) {
            instance = new SyncAlert(context);
        }
        return instance;
    }

    public void sync(){
        syncIndex = errorNum = 0;
        send();
    }
//    同步时间>>获取版本号>>获取电量>>用户信息>>计步目标>>界面选择>>久坐提醒>>防丢提醒>>闹钟提醒>>亮度调节>>单位设置>>时间格式>>
    BleCallback bleCallback = new BleCallback() {
        @Override
        public void onSuccess(Object o) {
            parse(o);
            next();
        }

        @Override
        public void onFailure(BleException exception) {
            if (errorNum < 5) {
                send();
                errorNum++;
            }else {
                if (syncAlertListener != null) {
                    syncAlertListener.onResult(false);
                }
            }
            Log.d("syncalert", "onFailure() called with: errorNum = [" + errorNum + "]");
        }
    };

    private synchronized void next(){
        if (syncIndex < 12) {
            syncIndex++;
            send();
        }else {
            if (syncAlertListener != null) {
                syncAlertListener.onResult(true);
                Log.d("syncalert", "next() called onResult true");
            }
        }
        Log.d("syncalert", "next() called syncIndex == "+syncIndex);
    }

    private void send(){
        switch (syncIndex) {
            case 0:
                watch.setTimeToNew(bleCallback);
                break;
            case 1:
                watch.getFirmwareVersion(bleCallback);
                break;
            case 2:
                watch.getBatteryInfo(bleCallback);
                break;
            case 3:
                UserModel userModel = IbandDB.getInstance().getUser();
                if (userModel == null || userModel.getUserHeight() == null || userModel.getUserHeight() == null) {
                    userModel = new UserModel("170","65");
                }
                watch.setUserInfo(new User(userModel.getUserHeight(),userModel.getUserWeight()),bleCallback);
                break;
            case 4:
                int target = (int) SPUtil.get(mContext,AppGlobal.DATA_SETTING_TARGET_STEP,0);
                watch.setSportTarget(target == 0 ? 8000:target,bleCallback);
                break;
            case 5:
                List<ViewModel> viewList = IbandDB.getInstance().getView();
                if (viewList == null || viewList.size() == 0){
                    viewList = new ArrayList<>();
                    viewList.add(new ViewModel(0,"待机", R.mipmap.selection_standby,true,false));
                    viewList.add(new ViewModel(1,"计步", R.mipmap.selection_step,true));
                    viewList.add(new ViewModel(2,"运动", R.mipmap.selection_sport,true));
                    viewList.add(new ViewModel(3,"心率", R.mipmap.selection_heartrate,true));
                    viewList.add(new ViewModel(4,"睡眠", R.mipmap.selection_sleep,true));
                    viewList.add(new ViewModel(9,"闹钟", R.mipmap.selection_alarmclock,true));
                    viewList.add(new ViewModel(7,"查找", R.mipmap.selection_find,true));
                    viewList.add(new ViewModel(6,"信息", R.mipmap.selection_about,true));
                    viewList.add(new ViewModel(5,"关机", R.mipmap.selection_turnoff,true));
                }
                int size = viewList.size();
                int[] onOffs = new int[size];
                int[] ids = new int[size];
                for (int i = 0; i < viewList.size(); i++) {
                    ids[i] = viewList.get(i).getViewId();
                    onOffs[i] = viewList.get(i).isSelect()? 1:0;
                }
                 watch.sendCmd(BleCmd.getWindowsSet(ids, onOffs),bleCallback);
                break;
            case 6:
                SedentaryModel sedentaryModel = IbandDB.getInstance().getSedentary();
                if (sedentaryModel == null) {
                    sedentaryModel = new SedentaryModel(false, false, "09:00", "21:00");
                }
                Sedentary sedentary = new Sedentary(sedentaryModel.isSedentaryOnOff(), sedentaryModel.isSedentaryNap()
                        , sedentaryModel.getStartTime(), sedentaryModel.getStartTime());
                watch.setSedentaryAlert(sedentary,bleCallback);
                break;
            case 7:
                boolean lostOn = (boolean) SPUtil.get(mContext,AppGlobal.DATA_ALERT_LOST,false);
                watch.setLostAlert(lostOn,20,bleCallback);
                break;
            case 8:
                List<ClockModel> clockList = IbandDB.getInstance().getClock();
                if (clockList == null || clockList.size()==0) {
                    clockList = new ArrayList<>();
                    clockList.add(new ClockModel("08:00",false));
                    clockList.add(new ClockModel("08:30",false));
                    clockList.add(new ClockModel("09:00",false));
                }
                List<Clock> clocks = new ArrayList<>();
                for (ClockModel model : clockList) {
                    clocks.add(new Clock(model.getTime(),model.isClockOnOFF()));
                }
                watch.setClock(ClockType.SET_CLOCK,clocks,bleCallback);
                break;
            case 9:
                int light = (int) SPUtil.get(mContext,AppGlobal.DATA_SETTING_LIGHT,1);
                watch.sendCmd(BleCmd.setLight(light),bleCallback);
                break;
            case 10:
                int unitLength = (int) SPUtil.get(mContext, AppGlobal.DATA_SETTING_UNIT,0);
                watch.sendCmd(BleCmd.setUnit(unitLength),bleCallback);
                break;
            case 11:
                int unitTime = (int) SPUtil.get(mContext, AppGlobal.DATA_SETTING_UNIT_TIME,0);
                watch.sendCmd(BleCmd.setHourSelect(unitTime),bleCallback);
                break;
            case 12:
               boolean onOff = (boolean) SPUtil.get(mContext, AppGlobal.DATA_ALERT_WRIST, true);
                watch.sendCmd(BleCmd.setWristOnOff(onOff ? 1 : 0),bleCallback);
                break;

        }
    }

    private void parse(Object o){
        switch (syncIndex) {
            case 1:
                String version = parseJsonString(o,"firmwareVersion");
                SPUtil.put(mContext, AppGlobal.DATA_VERSION_FIRMWARE,version);
                break;
            case 2:
                parseBattery(o);
                break;
        }
    }

    public void parseBattery(Object o) {
        int battery = parseJsonInt(o,"battery");
        int batteryState = parseJsonInt(o,"batteryState");
        SPUtil.put(mContext, AppGlobal.DATA_BATTERY_NUM,battery);
        SPUtil.put(mContext, AppGlobal.DATA_BATTERY_STATE,batteryState);
    }

    private String parseJsonString(Object o,String key){
        String str = o.toString();
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(str);
            result = (String) jsonObject.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private int parseJsonInt(Object o,String key){
        String str = o.toString();
        int result = 0;
        try {
            JSONObject jsonObject = new JSONObject(str);
            result = (int) jsonObject.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
