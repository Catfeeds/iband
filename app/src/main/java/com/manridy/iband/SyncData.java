package com.manridy.iband;

import com.google.gson.Gson;
import com.manridy.applib.utils.LogUtil;
import com.manridy.iband.bean.BoModel;
import com.manridy.iband.bean.BpModel;
import com.manridy.iband.bean.HeartModel;
import com.manridy.iband.bean.SleepModel;
import com.manridy.iband.bean.StepModel;
import com.manridy.sdk.Watch;
import com.manridy.sdk.ble.BleCmd;
import com.manridy.sdk.ble.BleParse;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.callback.BleHistoryListener;
import com.manridy.sdk.exception.BleException;
import com.manridy.sdk.type.InfoType;

/**
 *
 * Created by jarLiao on 17/5/25.
 */

public class SyncData {

    private Watch watch;
    private int syncIndex;
    private int progressIndex;
    private int progressSum;
    private int errorNum;
    private int stepSum,runSum,sleepSum,hrSum,bpSum,boSum;
    private OnSyncAlertListener syncAlertListener;
    private static SyncData instance;
    private Gson mGson;
    private boolean isRun;

    public interface OnSyncAlertListener{
        void onResult(boolean isSuccess);

        void onProgress(int progress);
    }

    public void setSyncAlertListener(OnSyncAlertListener syncAlertListener) {
        this.syncAlertListener = syncAlertListener;
    }

    private SyncData() {
        watch = IbandApplication.getIntance().service.watch;
        mGson = new Gson();
    }

    public static SyncData getInstance() {
        if (instance == null) {
            instance = new SyncData();
        }
        return instance;
    }

    public synchronized void sync(){
        if (isRun) return;
        isRun = true;
        progressSum = progressIndex = syncIndex = errorNum = 0;
        stepSum = sleepSum = hrSum = bpSum = boSum = 0;
        BleParse.getInstance().setStepHistoryListener(new BleHistoryListener() {
            @Override
            public void onHistory(Object o) {
                StepModel historyStep = mGson.fromJson(o.toString(), StepModel.class);
                if (historyStep.getHisLength() != 0) {
                    historyStep.save();
                    progressIndex++;
                    int progress = (int) (((double)progressIndex / progressSum)*100);
                    syncAlertListener.onProgress(progress);
                }
                boolean is = historyStep.getHisLength() == (historyStep.getHisCount()+1);
                if (is|| historyStep.getHisLength() == 0) {
                    next();
                }
            }
        });
        BleParse.getInstance().setRunHistoryListener(new BleHistoryListener() {
            @Override
            public void onHistory(Object o) {
                StepModel historyStep = mGson.fromJson(o.toString(), StepModel.class);
                if (historyStep.getHisLength() != 0) {
                    historyStep.save();
                    progressIndex++;
                    int progress = (int) (((double)progressIndex / progressSum)*100);
                    syncAlertListener.onProgress(progress);
                }
                boolean is = historyStep.getHisLength() == (historyStep.getHisCount()+1);
                if (is|| historyStep.getHisLength() == 0) {
                    next();
                }
            }
        });
        BleParse.getInstance().setSleepHistoryListener(new BleHistoryListener() {
            @Override
            public void onHistory(Object o) {
                SleepModel historySleep = mGson.fromJson(o.toString(), SleepModel.class);
                if (historySleep.getSleepLength() != 0) {
                    historySleep.save();
                    progressIndex++;
                    int progress = (int) (((double)progressIndex / progressSum)*100);
                    syncAlertListener.onProgress(progress);
                }
                boolean is = historySleep.getSleepLength() == (historySleep.getSleepNum()+1);
                if (is || historySleep.getSleepLength() == 0) {
                    next();
                }
            }
        });
        BleParse.getInstance().setHrHistoryListener(new BleHistoryListener() {
            @Override
            public void onHistory(Object o) {
                HeartModel historyHr = mGson.fromJson(o.toString(), HeartModel.class);
                if (historyHr.getHeartLength() != 0) {
                    historyHr.save();
                    progressIndex++;
                    int progress = (int) (((double)progressIndex / progressSum)*100);
                    syncAlertListener.onProgress(progress);
                }
                boolean is = historyHr.getHeartLength() == (historyHr.getHeartNum()+1);
                if (is || historyHr.getHeartLength() == 0) {
                    next();
                }

            }
        });
        BleParse.getInstance().setBpHistoryListener(new BleHistoryListener() {
            @Override
            public void onHistory(Object o) {
                BpModel historyBp = mGson.fromJson(o.toString(), BpModel.class);
                if (historyBp.getBpLength() != 0) {
                    historyBp.save();
                    progressIndex++;
                    int progress = (int) (((double)progressIndex / progressSum)*100);
                    syncAlertListener.onProgress(progress);
                }
                boolean is = historyBp.getBpLength() == (historyBp.getBpNum()+1);
                if (is || historyBp.getBpLength() == 0) {
                    next();
                }

            }
        });
        BleParse.getInstance().setBoHistoryListener(new BleHistoryListener() {
            @Override
            public void onHistory(Object o) {
                BoModel historyBo = mGson.fromJson(o.toString(), BoModel.class);
                if (historyBo.getboLength() != 0) {
                    historyBo.save();
                    progressIndex++;
                    int progress = (int) (((double)progressIndex / progressSum)*100);
                    syncAlertListener.onProgress(progress);
                }
                boolean is = historyBo.getboLength() == (historyBo.getboNum()+1);
                if (is || historyBo.getboLength() == 0) {
                    next();
                    progressSum = -1;
                }
            }
        });
        send();
    }

    BleCallback bleCallback = new BleCallback() {
        @Override
        public void onSuccess(Object o) {
            if (syncAlertListener != null) {
                if (progressSum == 0) {
                    syncAlertListener.onProgress(0);
                }
            }
            parse(o);
        }

        @Override
        public void onFailure(BleException exception) {
            if (errorNum < 5) {
                send();
                errorNum++;
            }else {
                if (syncAlertListener != null) {
                    syncAlertListener.onResult(false);
                    isRun = false;
                    LogUtil.d("SyncData", "next() called onResult false");
                }
            }
            LogUtil.d("SyncData", "onFailure() called with: errorNum = [" + errorNum + "]");
        }
    };

    private synchronized void next(){
        syncIndex++;
        LogUtil.d("SyncData", "next() called syncIndex == "+syncIndex);
        if (syncIndex < 14) {
            send();
        }else {
            if (syncAlertListener != null) {
                syncAlertListener.onResult(true);
                isRun = false;
                LogUtil.d("SyncData", "next() called onResult true");
            }
        }
    }
    //计步历史条数>>睡眠历史条数>>心率历史条数>>血压历史条数>>血氧历史条数
    //计步当前>>计步历史>>睡眠历史>>心率历史>>血压历史>>血氧历史
    private synchronized void send(){
        switch (syncIndex) {
            case 0:
                watch.setTimeToNew(bleCallback);
                break;
            case 1:
                watch.sendCmd(BleCmd.getStepSectionNum(),bleCallback);
                break;
            case 2:
                watch.sendCmd(BleCmd.getRunHistoryNum(),bleCallback);
                break;
            case 3:
                watch.getSleepInfo(InfoType.HISTORY_NUM,bleCallback);
                break;
            case 4:
                watch.getHeartRateInfo(InfoType.HISTORY_NUM,bleCallback);
                break;
            case 5:
                watch.getBloodPressureInfo(InfoType.HISTORY_NUM,bleCallback);
                break;
            case 6:
                watch.getBloodOxygenInfo(InfoType.HISTORY_NUM,bleCallback);
                break;
            case 7:
                watch.getSportInfo(InfoType.CURRENT_INFO,bleCallback);
                break;
            case 8:
                if (stepSum != 0) {
                    watch.sendCmd(BleCmd.getStepSectionHistroy(),bleCallback);
                }else {
                    next();
                }
                break;
            case 9:
                if (runSum != 0){
                    watch.sendCmd(BleCmd.getRunHistoryData(),bleCallback);
                }else {
                    next();
                }
                break;
            case 10:
                if (sleepSum != 0) {
                    watch.getSleepInfo(InfoType.HISTORY_INFO,bleCallback);
                }else {
                    next();
                }
                break;
            case 11:
                if (hrSum != 0){
                    watch.getHeartRateInfo(InfoType.HISTORY_INFO,bleCallback);
                }else {
                    next();
                }
                break;
            case 12:
                if (bpSum != 0) {
                    watch.getBloodPressureInfo(InfoType.HISTORY_INFO,bleCallback);
                }else {
                    next();
                }
                break;
            case 13:
                if (boSum != 0) {
                    watch.getBloodOxygenInfo(InfoType.HISTORY_INFO,bleCallback);
                }else {
                    next();
                }
                break;
        }
    }

    private synchronized void parse(Object o){
        switch (syncIndex) {
            case 0:
                next();
                break;
            case 1:
                StepModel stepLength = mGson.fromJson(o.toString(), StepModel.class);
                stepSum = stepLength.getHisLength();
                progressSum += stepSum;
                next();
                break;
            case 2:
                StepModel runLength = mGson.fromJson(o.toString(), StepModel.class);
                runSum = runLength.getHisLength();
                progressSum += runSum;
                next();
                break;
            case 3:
                SleepModel sleepLength = mGson.fromJson(o.toString(), SleepModel.class);
                sleepSum = sleepLength.getSleepLength();
                progressSum += sleepSum;
                next();
                break;
            case 4:
                HeartModel hrLength = mGson.fromJson(o.toString(), HeartModel.class);
                hrSum = hrLength.getHeartLength();
                progressSum += hrSum;
                next();
                break;
            case 5:
                BpModel bpLength = mGson.fromJson(o.toString(), BpModel.class);
                bpSum = bpLength.getBpLength();
                progressSum += bpSum;
                next();
                break;
            case 6:
                BoModel boLength = mGson.fromJson(o.toString(), BoModel.class);
                boSum = boLength.getboLength();
                progressSum += boSum;
                next();
                break;
            case 7:
                StepModel curStep = mGson.fromJson(o.toString(), StepModel.class);
                saveCurStep(curStep);
                next();
                break;
        }
    }

    public static void saveCurStep(StepModel curStep) {
        StepModel dbStep = IbandDB.getInstance().getCurStep();
        if (dbStep == null) {
            curStep.save();
        }else{
            dbStep.setStepNum(curStep.getStepNum());
            dbStep.setStepMileage(curStep.getStepMileage());
            dbStep.setStepCalorie(curStep.getStepCalorie());
            dbStep.save();
        }
    }

}
