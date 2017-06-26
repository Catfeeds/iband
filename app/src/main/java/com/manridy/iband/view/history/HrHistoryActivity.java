package com.manridy.iband.view.history;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manridy.applib.utils.TimeUtil;
import com.manridy.applib.view.dialog.DateDialog;
import com.manridy.iband.IbandDB;
import com.manridy.iband.adapter.HistoryAdapter;
import com.manridy.iband.R;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.ui.items.DataItems;
import com.manridy.iband.view.base.BaseActionActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 心率、血压、血氧历史
 * Created by jarLiao on 17/5/11.
 */

public class HrHistoryActivity extends BaseActionActivity {


    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_unit)
    TextView tvUnit;
    @BindView(R.id.line)
    TextView line;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.di_data1)
    DataItems diData1;
    @BindView(R.id.di_data2)
    DataItems diData2;
    @BindView(R.id.di_data3)
    DataItems diData3;
    @BindView(R.id.rl_history)
    RelativeLayout rlHistory;
    @BindView(R.id.rv_history)
    RecyclerView rvHistory;

    HistoryAdapter historyAdapter;
    List<HistoryAdapter.Item> itemList = new ArrayList<>();

    int historyType;
    int color,lineColor;
    Calendar mCalendar;
    SimpleDateFormat mDateFormat;
    List<String> days;
    int dataAvg = 0, dataMax = 0, dataMin = 0;
    double boAvg,boMax,boMin;
    HistoryAdapter.Item curItem;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_history_hr);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        registerEventBus();
        historyType = getIntent().getIntExtra("history_type",0);
        mCalendar =Calendar.getInstance();
        mDateFormat = new SimpleDateFormat("yyyy年MM月");
        historyAdapter = new HistoryAdapter(itemList);
        rvHistory.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        rvHistory.setAdapter(historyAdapter);
        initHistoryTitle();
        initHistoryData();
    }

    @Override
    protected void initListener() {
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time;
                int[] times = new int[]{1999,07,01};
                if (tvDate.getText().equals("本月")) {
                    time = mDateFormat.format(new Date());
                }else {
                    time = tvDate.getText().toString();
                }
                if (time.length() >= 7) {
                    int year = Integer.parseInt(time.substring(0,4));
                    int month = Integer.parseInt(time.substring(6,7));
                    times = new int[]{year,month-1};
                }
                new DateDialog(mContext,times , "选择月份", new DateDialog.DateDialogListener() {
                    @Override
                    public void getTime(int year, int monthOfYear, int dayOfMonth) {
                        String time = year +"年"+TimeUtil.zero(monthOfYear+1)+"月";
                        mCalendar.set(year, monthOfYear, dayOfMonth);
                        if (time.equals(mDateFormat.format(new Date()))) {
                            tvDate.setText("本月");
                        }else {
                            tvDate.setText(time);
                        }
                        initHistoryData();
                    }
                }).show();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(EventMessage event){
        if (event.getWhat() == EventGlobal.REFRESH_VIEW_HR_HISTORY) {
            historyAdapter.setItemList(itemList);
            diData1.setItemData("平均心率", dataAvg +"","次/分",lineColor);
            diData2.setItemData("最低心率",dataMin +"","次/分",lineColor);
            diData3.setItemData("最高心率",dataMax +"","次/分",lineColor);
            if (curItem !=null){
                tvNum.setText(curItem.itemNum);
                tvTime.setText(curItem.itemName);
            }else{
                tvNum.setText("--");
                tvTime.setText("--月--日 --:--");
                diData1.setItemData("--");
                diData2.setItemData("--");
                diData3.setItemData("--");
            }
        }else if (event.getWhat() == EventGlobal.REFRESH_VIEW_BP_HISTORY) {
            historyAdapter.setItemList(itemList);
            String time = mDateFormat.format(mCalendar.getTime());
            diData1.setItemData("时间",time,"",lineColor);
            diData2.setItemData("平均收缩压",dataMax +"","mmHg",lineColor);
            diData3.setItemData("平均舒张压",dataMin +"","mmHg",lineColor);
            if (curItem !=null){
                tvNum.setText(curItem.itemNum);
                tvTime.setText(curItem.itemName);
            }else{
                tvNum.setText("--");
                tvTime.setText("--月--日 --:--");
                diData1.setItemData("--年--月");
                diData2.setItemData("--");
                diData3.setItemData("--");
            }
        }else if (event.getWhat() == EventGlobal.REFRESH_VIEW_BO_HISTORY) {
            historyAdapter.setItemList(itemList);
            String str = String.format("%.1f ", boAvg);
            diData1.setItemData("平均值",str+"","%",lineColor);
            diData2.setItemData("最低值", boMin +"","%",lineColor);
            diData3.setItemData("最高值", boMax +"","%",lineColor);
            if (curItem !=null){
                tvNum.setText(curItem.itemNum);
                tvTime.setText(curItem.itemName);
            }else{
                tvNum.setText("--");
                tvTime.setText("--月--日 --:--");
                diData1.setItemData("--");
                diData2.setItemData("--");
                diData3.setItemData("--");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroundEvent(EventMessage event){
        if (event.getWhat() == EventGlobal.DATA_LOAD_HR_HISTORY) {
            curItem = null;
            days = TimeUtil.getMonthToDay(mCalendar);
            itemList = IbandDB.getInstance().getMonthHeart(days);
            if (itemList.size()>0) {
                curItem = itemList.get(0);
                dataMin =Integer.valueOf(curItem.itemNum);
            }
            for (HistoryAdapter.Item item : itemList) {
                int num = Integer.valueOf(item.itemNum);
                dataAvg += num;
                dataMax = dataMax > num ? dataMax : num;
                dataMin = dataMin < num ? dataMin : num;
            }
            if (itemList.size() != 0) {
                dataAvg /= itemList.size();
            }
            EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_HR_HISTORY));
        }else if (event.getWhat() == EventGlobal.DATA_LOAD_BP_HISTORY) {
            curItem = null;
            days = TimeUtil.getMonthToDay(mCalendar);
            itemList = IbandDB.getInstance().getMonthBp(days);
            if (itemList.size()>0) {
                curItem = itemList.get(0);
            }
            for (HistoryAdapter.Item item : itemList) {
                String[] strs =item.itemNum.split("/");
                int hp = Integer.parseInt(strs[0]);
                int lp = Integer.parseInt(strs[1]);
                dataMax += hp;
                dataMin += lp;
            }
            if (itemList.size() != 0) {
                dataMax /= itemList.size();
                dataMin /= itemList.size();
            }
            EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_BP_HISTORY));
        }else if (event.getWhat() == EventGlobal.DATA_LOAD_BO_HISTORY) {
            curItem = null;
            days = TimeUtil.getMonthToDay(mCalendar);
            itemList = IbandDB.getInstance().getMonthBo(days);
            if (itemList.size()>0) {
                curItem = itemList.get(0);
                boMin = Double.valueOf(curItem.itemNum);
            }
            double dataSum = 0;
            for (HistoryAdapter.Item item : itemList) {
                double num =Double.valueOf(item.itemNum);
                dataSum += num;
                boMax = boMax > num ? boMax : num;
                boMin = boMin < num ? boMin : num;
            }
            if (itemList.size()!=0) {
                boAvg = dataSum / itemList.size();
            }
            EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_BO_HISTORY));
        }
    }

    private void initHistoryTitle() {
        switch (historyType) {
            case 0:
                color = Color.parseColor("#ef5350");
                lineColor = Color.parseColor("#26ef5350");
                setTitleBar("心率记录", color);
                setStatusBarColor(color);
                break;
            case 1:
                color = Color.parseColor("#43a047");
                lineColor = Color.parseColor("#2643a047");
                setTitleBar("血压记录", color);
                setStatusBarColor(color);
                ivIcon.setImageResource(R.mipmap.bloodpressure_ic02);
                tvUnit.setText("mmHg");
                break;
            case 2:
                color = Color.parseColor("#ff4081");
                lineColor = Color.parseColor("#26ff4081");
                setTitleBar("血氧记录", color);
                setStatusBarColor(color);
                ivIcon.setImageResource(R.mipmap.bloodoxygen_ic02);
                tvUnit.setText("%");
                break;
        }
    }

    private void initHistoryData(){
        switch (historyType) {
            case 0:
                EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_HR_HISTORY));
                break;
            case 1:
                EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_BP_HISTORY));
                break;
            case 2:
                EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_BO_HISTORY));
                break;
        }
    }

}
