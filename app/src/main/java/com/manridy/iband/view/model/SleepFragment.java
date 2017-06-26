package com.manridy.iband.view.model;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manridy.applib.utils.SPUtil;
import com.manridy.applib.utils.TimeUtil;
import com.manridy.iband.IbandApplication;
import com.manridy.iband.IbandDB;
import com.manridy.iband.R;
import com.manridy.iband.bean.SleepModel;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.ui.ChartView;
import com.manridy.iband.ui.CircularView;
import com.manridy.iband.ui.items.DataItems;
import com.manridy.iband.view.base.BaseEventFragment;
import com.manridy.iband.view.history.SleepHistoryActivity;
import com.manridy.sdk.bean.Sleep;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.manridy.applib.base.BaseActivity.isFastDoubleClick;

/**
 * 睡眠
 * Created by jarLiao on 2016/10/24.
 */

public class SleepFragment extends BaseEventFragment {

    @BindView(R.id.di_data1)
    DataItems diData1;
    @BindView(R.id.di_data2)
    DataItems diData2;
    @BindView(R.id.di_data3)
    DataItems diData3;
    @BindView(R.id.cv_sleep)
    CircularView cvSleep;
    @BindView(R.id.chart_sleep)
    ChartView chartSleep;


    List<SleepModel> curSleeps;
    int[] colors;
    @BindView(R.id.tv_time_start)
    TextView tvTimeStart;
    @BindView(R.id.tv_time_end)
    TextView tvTimeEnd;

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        root = inflater.inflate(R.layout.fragment_sleep, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    protected void initVariables() {
        colors = new int[]{Color.parseColor("#8a512da8"), Color.parseColor("#8a9575cd"), Color.parseColor("#8affbc00")};
    }

    @Override
    protected void initListener() {
        chartSleep.setOnChartItemSelectListener(new ChartView.onChartItemSelectListener() {
            @Override
            public void onItemSelect(int position) {
                setSelectDataItem(position);
            }

            @Override
            public void onNoSelect() {
                setDataItem();
            }
        });
//        cvSleep.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                IbandApplication.getIntance().service.watch.sendCmd(new byte[]{(byte) 0xfc,0x0c,0x03});
//            }
//        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_SLEEP));
    }

    @OnClick({R.id.iv_history})
    public void onClick(View view) {
        if (isFastDoubleClick()) return;
        switch (view.getId()) {
            case R.id.iv_history:
                startActivity(SleepHistoryActivity.class);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.REFRESH_VIEW_SLEEP) {
            setCircularView();
            chartSleep.setChartData(colors, curSleeps).invaliDate();
            setDataItem();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroundEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.DATA_LOAD_SLEEP) {
            curSleeps = IbandDB.getInstance().getCurSleeps();
            List<SleepModel> sleepList = new ArrayList<>();
            if (curSleeps.size()>0) {
                SleepModel curSleep = curSleeps.get(0);
                for (int i = 1; i < curSleeps.size(); i++) {
                    SleepModel nextSleep = curSleeps.get(i);
                    if (curSleep.getSleepDataType() == nextSleep.getSleepDataType()) {
                        if (curSleep.getSleepDataType() == 1) {
                            nextSleep.setSleepDeep(curSleep.getSleepDeep()+nextSleep.getSleepDeep());
                        }else if (curSleep.getSleepDataType() == 2){
                            nextSleep.setSleepLight(curSleep.getSleepLight()+nextSleep.getSleepLight());
                        }
                        nextSleep.setSleepStartTime(curSleep.getSleepStartTime());
                    }else {
                        sleepList.add(curSleep);
                    }
                    curSleep = nextSleep;
                }
                sleepList.add(curSleep);
            }
            curSleeps = sleepList;
            EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_SLEEP));
        } else if (event.getWhat() == EventGlobal.REFRESH_VIEW_ALL) {
            EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_SLEEP));
        }
    }

    private void setCircularView() {
        if (curSleeps == null || curSleeps.size() == 0) return;
        int target = (int) SPUtil.get(mContext, AppGlobal.DATA_SETTING_TARGET_SLEEP, 8);
        int sleepSum = 0, sleepLight = 0, sleepDeep = 0;
        for (SleepModel curSleep : curSleeps) {
            sleepLight += curSleep.getSleepLight();
            sleepDeep += curSleep.getSleepDeep();
        }
        sleepSum += (sleepLight + sleepDeep);
        double dou = TimeUtil.getHourDouble(sleepLight) + TimeUtil.getHourDouble(sleepDeep);
        String sum = String .format("%.1f", dou);
        String state = "深睡" + TimeUtil.getHour(sleepDeep) + "/浅睡" + TimeUtil.getHour(sleepLight);
        float progress = (sleepSum / (float) (target * 60)) * 100;
        cvSleep.setText(sum)
                .setState(state)
                .setProgress(progress)
                .invaliDate();
//        cvSleep.setProgressWithAnimation(progress);
    }

    private void setSelectDataItem(int position) {
        if (curSleeps == null || curSleeps.size() == 0) return;
        SleepModel sleepModel = curSleeps.get(position);
        String start = sleepModel.getSleepStartTime();
        String end = sleepModel.getSleepEndTime();
        int type = sleepModel.getSleepDeep() > 0 ? 0 : 1;
        int min = type == 0 ? sleepModel.getSleepDeep() : sleepModel.getSleepLight();
        String title = type == 0 ? "深睡" : "浅睡";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-DD HH:mm");
        try {
            Date dateStart = simpleDateFormat.parse(start);
            Date dateEnd = simpleDateFormat.parse(end);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
            String startTime = simpleDateFormat2.format(dateStart);
            String endTime = simpleDateFormat2.format(dateEnd);
            String m = String.format("%.1f", ((double) min / 60));
            diData1.setItemData(title + "开始", startTime);
            diData2.setItemData(title + "结束", endTime);
            diData3.setItemData(title + "时长", m);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setDataItem() {
        if (curSleeps == null || curSleeps.size() == 0) return;
        String start = curSleeps.get(0).getSleepStartTime();
        String end = curSleeps.get(curSleeps.size() - 1).getSleepEndTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-DD HH:mm");
        try {
            Date dateStart = simpleDateFormat.parse(start);
            Date dateEnd = simpleDateFormat.parse(end);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
            String startTime = simpleDateFormat2.format(dateStart);
            String endTime = simpleDateFormat2.format(dateEnd);
            diData1.setItemData("昨晚入睡", startTime);
            diData2.setItemData("今天醒来", endTime);
            diData3.setItemData("清醒时长", "--", "小时");
            tvTimeStart.setText(startTime);
            tvTimeEnd.setText(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



}
