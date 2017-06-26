package com.manridy.iband.view.alert;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.manridy.applib.utils.SPUtil;
import com.manridy.applib.view.dialog.TimeDialog;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.IbandDB;
import com.manridy.iband.R;
import com.manridy.iband.bean.ClockModel;
import com.manridy.iband.ui.items.ClockItems;
import com.manridy.iband.view.base.BaseActionActivity;
import com.manridy.sdk.bean.Clock;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.exception.BleException;
import com.manridy.sdk.type.ClockType;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 闹钟提醒
 * Created by jarLiao on 17/5/4.
 */

public class ClockActivity extends BaseActionActivity {

    @BindView(R.id.ci_clock1)
    ClockItems ciClock1;
    @BindView(R.id.ci_clock2)
    ClockItems ciClock2;
    @BindView(R.id.ci_clock3)
    ClockItems ciClock3;
    @BindView(R.id.tb_menu)
    TextView tbMenu;

    List<ClockModel> clockList = new ArrayList<>();
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_clock);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        registerEventBus();
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleAndMenu("闹钟提醒", "保存");
        initClock();
        thread.start();
    }

    private void initClock() {
        clockList = IbandDB.getInstance().getClock();
        if (clockList == null || clockList.size()==0) {
            clockList = new ArrayList<>();
            clockList.add(new ClockModel("08:00",false));
            clockList.add(new ClockModel("08:30",false));
            clockList.add(new ClockModel("09:00",false));
        }
        ciClock1.setClockTime(clockList.get(0).getTime())
                .setClockOnOff(clockList.get(0).isClockOnOFF());
        ciClock2.setClockTime(clockList.get(1).getTime())
                .setClockOnOff(clockList.get(1).isClockOnOFF());
        ciClock3.setClockTime(clockList.get(2).getTime())
                .setClockOnOff(clockList.get(2).isClockOnOFF());
    }

    @Override
    protected void initListener() {
        tbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress("正在保存中...");
                List<Clock> clocks = new ArrayList<>();
                for (ClockModel model : clockList) {
                    clocks.add(new Clock(model.getTime(),model.isClockOnOFF()));
                }
                mIwaerApplication.service.watch.setClock(ClockType.SET_CLOCK, clocks, new BleCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        int openNum = 0;
                        for (ClockModel clockModel : clockList) {
                            if (clockModel.isClockOnOFF()) {
                                openNum++;
                            }
                            clockModel.save();
                        }
                        SPUtil.put(mContext, AppGlobal.DATA_ALERT_CLOCK,openNum >0);
                        eventSend(EventGlobal.MSG_CLOCK_TOAST, "保存成功");
                        eventSend(EventGlobal.DATA_CHANGE_MENU);
                        dismissProgress();
                        finish();
                    }

                    @Override
                    public void onFailure(BleException exception) {
                        eventSend(EventGlobal.MSG_CLOCK_TOAST, "保存失败");
                        dismissProgress();
                    }
                });
            }
        });
        ciClock1.setCheckClockListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clockList.get(0).setClockOnOFF(isChecked);
            }
        });
        ciClock2.setCheckClockListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clockList.get(1).setClockOnOFF(isChecked);
            }
        });
        ciClock3.setCheckClockListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clockList.get(2).setClockOnOFF(isChecked);
            }
        });
    }

    @OnClick({R.id.ci_clock1, R.id.ci_clock2, R.id.ci_clock3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ci_clock1:
                int[] time1 = getTimeInt(clockList.get(0).getTime());
                new TimeDialog(mContext, time1, "闹钟1", new TimeDialog.TimeDialogListener() {
                    @Override
                    public void getTime(String hour, String minute) {
                        String clockTime = hour + ":" + minute;
                        ciClock1.setClockTime(clockTime);
                        clockList.get(0).setTime(clockTime);
                    }
                }).show();
                break;
            case R.id.ci_clock2:
                int[] time2 = getTimeInt(clockList.get(1).getTime());
                new TimeDialog(mContext, time2, "闹钟2", new TimeDialog.TimeDialogListener() {
                    @Override
                    public void getTime(String hour, String minute) {
                        String clockTime = hour + ":" + minute;
                        ciClock2.setClockTime(clockTime);
                        clockList.get(1).setTime(clockTime);
                    }
                }).show();
                break;
            case R.id.ci_clock3:
                int[] time3 = getTimeInt(clockList.get(2).getTime());
                new TimeDialog(mContext, time3, "闹钟3", new TimeDialog.TimeDialogListener() {
                    @Override
                    public void getTime(String hour, String minute) {
                        String clockTime = hour + ":" + minute;
                        ciClock3.setClockTime(clockTime);
                        clockList.get(2).setTime(clockTime);
                    }
                }).show();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventMessage event) {
        if (event.getWhat() == EventGlobal.DATA_CHANGE_MINUTE) {
            ciClock1.setClockTime(clockList.get(0).getTime());
            ciClock2.setClockTime(clockList.get(1).getTime());
            ciClock3.setClockTime(clockList.get(2).getTime());
        }else if (event.getWhat() == EventGlobal.MSG_CLOCK_TOAST){
            showToast(event.getMsg());
        }
    }

    private int[] getTimeInt(String time) {
        String[] times = time.split(":");
        int[] ints = new int[times.length];
        for (int i = 0; i < times.length; i++) {
            ints[i] = Integer.parseInt(times[i]);
        }
        return ints;
    }

    boolean isExit;
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!isExit){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss");
                String s = simpleDateFormat.format(new Date());
                if (s.equals("00")) {
                    eventSend(EventGlobal.DATA_CHANGE_MINUTE);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isExit = true;
        thread.interrupt();
        thread = null;
    }
}
