package com.manridy.iband.view.history;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.manridy.applib.utils.SPUtil;
import com.manridy.applib.utils.TimeUtil;
import com.manridy.applib.view.dialog.DateDialog;
import com.manridy.iband.IbandDB;
import com.manridy.iband.R;
import com.manridy.iband.bean.DayBean;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.ui.CircularView;
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
 * 睡眠历史
 * Created by jarLiao on 17/5/11.
 */

public class SleepHistoryActivity extends BaseActionActivity {

    @BindView(R.id.cv_history_sleep)
    CircularView cvHistorySleep;
    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.di_data1)
    DataItems diData1;
    @BindView(R.id.di_data2)
    DataItems diData2;
    @BindView(R.id.di_data3)
    DataItems diData3;
    @BindView(R.id.bc_history_sleep)
    BarChart bcHistorySleep;

    private Calendar mCalendar;
    private SimpleDateFormat mDateFormat;
    private List<String> days = new ArrayList<>();
    private List<DayBean> sleepList = new ArrayList<>();
    private int sleepSum = 0,sleepLight = 0,sleepDeep = 0,sleepCount = 0;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_history_sleep);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        registerEventBus();
        setTitleBar("睡眠记录");
        mCalendar = Calendar.getInstance();
        mDateFormat = new SimpleDateFormat("yyyy年MM月");
        initChartView(bcHistorySleep);
        initChartAxis(bcHistorySleep);
        EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_SLEEP_HISTORY));
    }

    @Override
    protected void initListener() {
        tvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time;
                int[] times = new int[]{1999,07,01};
                if (tvMonth.getText().equals("本月")) {
                    time = mDateFormat.format(new Date());
                }else {
                    time = tvMonth.getText().toString();
                }
                if (time.length() >= 7) {
                    int year = Integer.parseInt(time.substring(0,4));
                    int month = Integer.parseInt(time.substring(6,7));
                    times = new int[]{year,month-1};
                }
                new DateDialog(mContext,times , "选择月份", new DateDialog.DateDialogListener() {
                    @Override
                    public void getTime(int year, int monthOfYear, int dayOfMonth) {
                        String time = year +"年"+ TimeUtil.zero(monthOfYear+1)+"月";
                        mCalendar.set(year, monthOfYear, dayOfMonth);
                        if (time.equals(mDateFormat.format(new Date()))) {
                            tvMonth.setText("本月");
                        }else {
                            tvMonth.setText(time);
                        }
                        EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_SLEEP_HISTORY));
                    }
                }).show();
            }
        });
    }

    private void initChartView(BarChart chart) {
        chart.getDescription().setEnabled(false);//描述设置
        chart.setNoDataText("无数据");//无数据时描述
        chart.setTouchEnabled(true);//可接触
        chart.setDrawMarkers(true);
        chart.setDrawBorders(true);  //是否在折线图上添加边框
        chart.setDragEnabled(false);//可拖拽
        chart.setScaleEnabled(false);//可缩放
        chart.setDoubleTapToZoomEnabled(false);//双击移动
        chart.setScaleYEnabled(true);//滑动
        chart.setDrawGridBackground(false);//画网格背景
        chart.setDrawBorders(false);  //是否在折线图上添加边框
        chart.setPinchZoom(false);//设置少量移动
        chart.setOnChartValueSelectedListener(selectedListener);
        chart.getLegend().setEnabled(false);
        chart.setData(new BarData());
    }


    private void initChartAxis(BarChart chart) {
        //x轴坐标
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);//显示x轴
        xAxis.setTextColor(Color.BLACK);//x轴文字颜色
        xAxis.setTextSize(12f);//x轴文字大小
        xAxis.setLabelCount(7,true);
        xAxis.setDrawGridLines(false);//取消网格线
        xAxis.setDrawAxisLine(false);//取消x轴底线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x轴位置
        xAxis.setAxisMinimum(1);//设置最小点
        xAxis.setGranularity(1f);//设置间隔
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int)value);
            }

        });
        //Y轴坐标
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0);//设置y轴最小点
//        yAxis.setAxisMaximum(12*60);
//        yAxis.setLabelCount(7,false);
        yAxis.setDrawAxisLine(false);//画坐标线
        yAxis.setDrawLabels(false);//画坐标下标
        yAxis.setDrawGridLines(false);//设置网格线
//        yAxis.setValueFormatter(new HourValueFormatter());
        yAxis.setDrawZeroLine(false);
        yAxis.setEnabled(true);//显示Y轴
        chart.getAxisRight().setEnabled(false);//不显示右侧
    }

    private void updateChartView(BarChart chart,List<DayBean> dayData){
        if (chart.getData() != null) {
            chart.clearValues();
        }
        if (dayData == null) {
            return;
        }
        List<BarEntry> sumList = new ArrayList<>();
        for (int i = 0; i < dayData.size(); i++) {
            sumList.add(new BarEntry(i+1,new float[]{dayData.get(i).getDayMax(),dayData.get(i).getDayMin()}));
        }
        BarData barData = new BarData(getInitChartDataSet(sumList));
        chart.setData(barData);
        chart.notifyDataSetChanged();
        chart.moveViewToX(barData.getEntryCount());
    }

    private BarDataSet getInitChartDataSet(List<BarEntry> entryList) {
        BarDataSet set = new BarDataSet(entryList,"");//初始化折线数据源
        set.setColors(new int[]{Color.parseColor("#8a512da8"),
                Color.parseColor("#8a9575cd")});//折线颜色
        set.setBarBorderWidth(2f);//
        set.setBarBorderColor(Color.TRANSPARENT);
        set.setValueTextSize(12f);//折线值文字大小
        set.setDrawValues(false);
        return set;
    }

    private void setCircularView(){
        if (sleepList == null || sleepList.size()== 0) return;
        int target = (int) SPUtil.get(mContext, AppGlobal.DATA_SETTING_TARGET_SLEEP,8);
        String deep = TimeUtil.getHour(sleepDeep);
        String light = TimeUtil.getHour(sleepLight);
        double dou = TimeUtil.getHourDouble(sleepLight) + TimeUtil.getHourDouble(sleepDeep);
        String sum = String .format("%.1f", dou);
        String state = "深睡"+deep+"/浅睡" + light;
        float progress = (sleepSum / (float)(target*60)) * 100;
        cvHistorySleep.setText(sum)
                .setState(state)
                .setProgress(progress)
                .invaliDate();
//        cvHistorySleep.setProgressWithAnimation(progress);
    }

    private void setDataItem(){
        String deep =  String .format("%.1f", (sleepDeep/60.0));
        String light =  String .format("%.1f", (sleepLight/60.0));
        double dou = TimeUtil.getHourDouble(sleepLight) + TimeUtil.getHourDouble(sleepDeep);
        String sum = String .format("%.1f", dou);
        diData1.setItemData("每日平均睡眠",sum);
        diData2.setItemData("每日平均深睡",deep);
        diData3.setItemData("每日平均浅睡",light);
    }

    OnChartValueSelectedListener selectedListener = new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            if (sleepList != null&&sleepList.size()>=e.getX()) {
                DayBean dayBean = sleepList.get((int)e.getX()>0?(int)e.getX()-1:0);
                int deep = dayBean.getDayMax();
                int light = dayBean.getDayMin();
                String strDeep =  String .format("%.1f", ((double)deep/60));
                String strLight =  String .format("%.1f", ((double)light/60));
                double dou = TimeUtil.getHourDouble(deep) + TimeUtil.getHourDouble(light);
                String sum = String .format("%.1f", dou);
                String day = dayBean.getDay();
                diData1.setItemData(day,sum);
                diData2.setItemData("深睡",strDeep);
                diData3.setItemData("浅睡",strLight);
            }
        }

        @Override
        public void onNothingSelected() {
            setDataItem();
        }
    };

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroundEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.DATA_LOAD_SLEEP_HISTORY) {
            sleepSum = sleepDeep = sleepLight = sleepCount =0;
            days = TimeUtil.getMonthToDay(mCalendar);
            sleepList = IbandDB.getInstance().getMonthSleep(days);
            for (DayBean dayBean : sleepList) {
                sleepLight += dayBean.getDayMin();
                sleepDeep +=dayBean.getDayMax();
                if (dayBean.getDayCount()!= 0) {
                    sleepCount++;
                }
            }
            if (sleepCount!=0) {
                sleepLight /= sleepCount;
                sleepDeep /= sleepCount;
            }
            sleepSum += (sleepLight + sleepDeep);
            EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_SLEEP_HISTORY));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.REFRESH_VIEW_SLEEP_HISTORY) {
            setCircularView();
            updateChartView(bcHistorySleep,sleepList);
            setDataItem();
        }
    }
}
