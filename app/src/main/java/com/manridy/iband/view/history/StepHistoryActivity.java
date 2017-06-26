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
import com.manridy.applib.utils.CheckUtil;
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
 * 计步历史
 * Created by jarLiao on 17/5/11.
 */

public class StepHistoryActivity extends BaseActionActivity {


    @BindView(R.id.cv_history_step)
    CircularView cvHistoryStep;
    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.di_data1)
    DataItems diData1;
    @BindView(R.id.di_data2)
    DataItems diData2;
    @BindView(R.id.di_data3)
    DataItems diData3;
    @BindView(R.id.bc_history_step)
    BarChart bcHistoryStep;

    private Calendar mCalendar;
    private SimpleDateFormat mDateFormat;
    private List<String> days = new ArrayList<>();
    private List<DayBean> stepList = new ArrayList<>();
    private int stepNum = 0, stepMi = 0, stepKa = 0,stepCount = 0;
    private int unit;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_history_step);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        setTitleBar("计步记录");
        registerEventBus();
        unit = (int) SPUtil.get(mContext, AppGlobal.DATA_SETTING_UNIT,0);
        mCalendar = Calendar.getInstance();
        mDateFormat = new SimpleDateFormat("yyyy年MM月");
        initChartView(bcHistoryStep);
        initChartAxis(bcHistoryStep);
        EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_STEP_HISTORY));
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
                        EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_STEP_HISTORY));
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
//        yAxis.setAxisMaximum(24*60);
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
            sumList.add(new BarEntry(i+1,new float[]{dayData.get(i).getDaySum()}));
        }
        BarData barData = new BarData(getInitChartDataSet(sumList));
        chart.setData(barData);
        chart.notifyDataSetChanged();
        chart.moveViewToX(barData.getEntryCount());
    }

    private BarDataSet getInitChartDataSet(List<BarEntry> entryList) {
        BarDataSet set = new BarDataSet(entryList,"");//初始化折线数据源
        set.setColor(Color.parseColor("#8aff9800"));//折线颜色
        set.setBarBorderWidth(2f);//
        set.setBarBorderColor(Color.TRANSPARENT);
        set.setValueTextSize(12f);//折线值文字大小
        set.setDrawValues(false);
        return set;
    }

    private void setCircularView(){
        if (stepList == null || stepList.size()== 0) return;
        int target = (int) SPUtil.get(mContext, AppGlobal.DATA_SETTING_TARGET_STEP,8000);
        stepCount = stepCount == 0 ? 1:stepCount;
        String step = stepNum/stepCount+"";
        String miUnit = unit == 1 ? "英里":"公里";
        String state = miToKm(stepMi/stepCount,unit)+miUnit+"/" +stepKa/stepCount+"大卡";
        float progress = (stepNum/stepCount / (float)target) * 100;
        cvHistoryStep.setText(step)
                .setState(state)
                .setProgress(progress)
                .invaliDate();
    }

    private void setDataItem(){
        String miUnit = unit == 1 ? "英里":"公里";
        diData1.setItemData("总步数",stepNum+"");
        diData2.setItemData("总里程",miToKm(stepMi,unit),miUnit);
        diData3.setItemData("总热量",stepKa+"");
    }

    OnChartValueSelectedListener selectedListener = new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            if (stepList != null&&stepList.size()>=e.getX()) {
                DayBean dayBean = stepList.get((int)e.getX()>0?(int)e.getX()-1:0);
                int step = dayBean.getDaySum();
                int ka = dayBean.getDayMax();
                int mi = dayBean.getDayMin();
                String day = dayBean.getDay();
                String miUnit = unit == 1 ? "英里":"公里";
                diData1.setItemData(day,step+"");
                diData2.setItemData("里程",miToKm(mi,unit),miUnit);
                diData3.setItemData("卡路里",ka+"");
            }
        }

        @Override
        public void onNothingSelected() {
            setDataItem();
        }
    };

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroundEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.DATA_LOAD_STEP_HISTORY) {
            stepNum = stepKa = stepMi = stepCount =0;
            days = TimeUtil.getMonthToDay(mCalendar);
            stepList = IbandDB.getInstance().getMonthStep(days);
            for (DayBean dayBean : stepList) {
                stepMi += dayBean.getDayMin();
                stepKa +=dayBean.getDayMax();
                stepNum += dayBean.getDaySum();
                if (dayBean.getDayCount()!= 0) {
                    stepCount++;
                }
            }
            EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_STEP_HISTORY));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.REFRESH_VIEW_STEP_HISTORY) {
            setCircularView();
            updateChartView(bcHistoryStep,stepList);
            setDataItem();
        }
    }

    public String miToKm(int mi,int unit){
        if (unit == 1) {
            return String .format("%.1f", CheckUtil.kmToMi(mi/1000.0));
        }
        return String .format("%.1f",(mi/1000.0));
    }

}
