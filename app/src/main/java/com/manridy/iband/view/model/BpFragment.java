package com.manridy.iband.view.model;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.manridy.iband.IbandApplication;
import com.manridy.iband.IbandDB;
import com.manridy.iband.R;
import com.manridy.iband.bean.BpModel;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.ui.CircularView;
import com.manridy.iband.ui.items.DataItems;
import com.manridy.iband.view.base.BaseEventFragment;
import com.manridy.iband.view.history.HrHistoryActivity;
import com.manridy.sdk.callback.BleNotifyListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.manridy.applib.base.BaseActivity.isFastDoubleClick;

/**
 * 血压
 * Created by jarLiao on 2016/10/24.
 */

public class BpFragment extends BaseEventFragment {

    @BindView(R.id.di_data1)
    DataItems diData1;
    @BindView(R.id.di_data2)
    DataItems diData2;
    @BindView(R.id.di_data3)
    DataItems diData3;
    @BindView(R.id.cv_bp)
    CircularView cvBp;
    @BindView(R.id.bc_bp)
    BarChart bcBp;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.tv_end)
    TextView tvEnd;


    BpModel curBp;
    List<BpModel> curBpList;

    String time;
    int hp, lp;
    boolean isTestData = true;


    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        root = inflater.inflate(R.layout.fragment_bp, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    protected void initVariables() {
        initChartView(bcBp);
        initChartAxis(bcBp);
    }

    @Override
    protected void initListener() {
        IbandApplication.getIntance().service.watch.setBpNotifyListener(new BleNotifyListener() {
            @Override
            public void onNotify(Object o) {//上报不做保存处理
                curBp = new Gson().fromJson(o.toString(), BpModel.class);
                if (isTestData) {
                    curBpList = new ArrayList<>();
                }
                isTestData = false;
                curBpList.add(curBp);
                EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_BP));
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_BP));
    }

    @OnClick({R.id.iv_history})
    public void onClick(View view) {
        if (isFastDoubleClick()) return;
        switch (view.getId()) {
            case R.id.iv_history:
                Intent intent = new Intent(mContext, HrHistoryActivity.class);
                intent.putExtra("history_type", 1);
                startActivity(intent);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.REFRESH_VIEW_BP) {
            setCircularView();
            updateBarChartView(bcBp, curBpList);
            setDataItem();
        } else if (event.getWhat() == EventGlobal.ACTION_HR_TESTING) {
            cvBp.setTitle("测量中").invaliDate();
        } else if (event.getWhat() == EventGlobal.ACTION_HR_TESTED) {
            cvBp.setTitle("上次测量结果").invaliDate();
            isTestData = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroundEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.DATA_LOAD_BP) {
            curBp = IbandDB.getInstance().getLastBp();
            curBpList = IbandDB.getInstance().getLastsBp();
            Collections.reverse(curBpList); // 倒序排列
            EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_BP));
        } else if (event.getWhat() == EventGlobal.REFRESH_VIEW_ALL) {
            EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_BP));
        }
    }

    private void initChartView(BarChart chart) {
        chart.getDescription().setEnabled(false);//描述设置
        chart.setNoDataText("");//无数据时描述
        chart.setTouchEnabled(true);//可接触
        chart.setDrawMarkers(true);
        chart.setDrawBorders(true);  //是否在折线图上添加边框
        chart.setDragEnabled(false);//可拖拽
        chart.setScaleEnabled(false);//可缩放
        chart.setDoubleTapToZoomEnabled(false);//双击移动
        chart.setScaleYEnabled(false);//滑动
        chart.setDrawGridBackground(false);//画网格背景
        chart.setDrawBorders(false);  //是否在折线图上添加边框
        chart.setPinchZoom(false);//设置少量移动
        chart.setOnChartValueSelectedListener(selectedListener);
        chart.getLegend().setEnabled(false);
        chart.setMaxVisibleValueCount(7);
        chart.setData(new BarData());
        chart.setFitBars(true);
    }

    private void initChartAxis(BarChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);//显示x轴
        xAxis.setTextColor(Color.BLACK);//x轴文字颜色
        xAxis.setTextSize(12f);//x轴文字大小
        xAxis.setDrawGridLines(false);//取消网格线
        xAxis.setDrawAxisLine(false);//取消x轴底线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x轴位置
        xAxis.setDrawLabels(false);
//        xAxis.setAxisMinimum(1);//设置最小点
//        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelCount(7, false);
        xAxis.setGranularity(1f);//设置间隔

        YAxis yAxis = chart.getAxisLeft();
//        yAxis.setAxisMaximum(220f);
        yAxis.setAxisMinimum(0);//设置y轴最小点
        yAxis.setDrawAxisLine(false);//画坐标线
        yAxis.setDrawLabels(false);//画坐标下标
        yAxis.setDrawGridLines(false);//设置网格线
        yAxis.setDrawZeroLine(false);
        yAxis.setEnabled(true);//显示Y轴
        chart.getAxisRight().setEnabled(false);//不显示右侧
    }

    private void updateBarChartView(BarChart chart, List<BpModel> bpList) {
        if (bpList == null || bpList.size() <= 0) {
            return;
        }
        List<BarEntry> hpList = new ArrayList<>();
        List<BarEntry> lpList = new ArrayList<>();
        for (int i = 0; i < bpList.size(); i++) {
            BpModel bpModel = bpList.get(i);
            hpList.add(new BarEntry(i + 1, bpModel.getBpHp()));
            lpList.add(new BarEntry(i + 1, bpModel.getBpLp()));
        }
        BarData data = new BarData(getInitChartDataSet(lpList, Color.parseColor("#8a81c784"), "set1")
                , getInitChartDataSet(hpList, Color.parseColor("#8a4caf50"), "set2"));
        if (chart.getData() != null) {
            chart.clearValues();
        }
        data.setBarWidth(0.35f);
        data.groupBars(0, 0.4f, 0.00f);
        chart.setData(data);
        //判断数据数量
        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMinimum(7);
        chart.setVisibleXRangeMaximum(7);
        chart.moveViewToX(data.getEntryCount() - 7);
    }

    private BarDataSet getInitChartDataSet(List<BarEntry> entryList, int color, String label) {
        BarDataSet set = new BarDataSet(entryList, label);//初始化折线数据源
        set.setColor(color);//折线颜色
        set.setBarBorderWidth(0.4f);//
        set.setValueTextColor(Color.BLACK);//折线值文字颜色
        set.setBarBorderColor(Color.TRANSPARENT);
        set.setValueTextSize(12f);//折线值文字大小
        set.setDrawValues(false);
        return set;
    }

    private void setCircularView() {
        if (curBp == null) return;
        String text = curBp.getBpHp() + "/" + curBp.getBpLp();
        String state = curBp.getBpDate();
        float progress = (float) ((curBp.getBpLp() / 220.0) * 100);
        float progress2 = (float) ((curBp.getBpHp() / 220.0) * 100);

        cvBp.setText(text)
                .setState(state)
                .setProgress(progress)
                .setProgress2(progress2)
                .invaliDate();
//        cvBp.setProgressWithAnimation(progress);
    }


    private void setDataItem() {
        if (curBp != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = dateFormat.parse(curBp.getBpDate());
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
                time = dateFormat2.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            hp = curBp.getBpHp();
            lp = curBp.getBpLp();
        }
        if (curBpList.size() > 0) {
            String start = curBpList.get(0).getBpDate().substring(11, 19);
            String end = curBpList.get(curBpList.size() - 1).getBpDate().substring(11, 19);
            tvStart.setText(start);
            tvEnd.setText(end);
        }
        diData1.setItemData("时间", time);
        diData2.setItemData("收缩压", hp + "");
        diData3.setItemData("舒张压", lp + "");
    }

    OnChartValueSelectedListener selectedListener = new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            int x = Math.round(e.getX());
            if (e.getX()>0 && e.getX()<1){
                x = 0;
            }
            if (e.getX()>1 && e.getX()<2) {
                x = 2 ;
            }
            Log.d(TAG, "onValueSelected() called with: e = [" + e.getX() + "], h = [" + x + "]");
            if (curBpList != null && curBpList.size() >= x) {
                BpModel bpModel = curBpList.get(x > 0 ? x - 1 : 0);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String times = "00:00";
                try {
                    Date date = dateFormat.parse(bpModel.getBpDate());
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
                    times = dateFormat2.format(date);
                } catch (Exception e1) {

                }

                int hp = bpModel.getBpHp();
                int lp = bpModel.getBpLp();
                diData1.setItemData("时间", times);
                diData2.setItemData("收缩压", hp + "");
                diData3.setItemData("舒张压", lp + "");
            }
        }

        @Override
        public void onNothingSelected() {
            setDataItem();
        }
    };

}
