package com.manridy.iband.view.model;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.manridy.iband.IbandApplication;
import com.manridy.iband.IbandDB;
import com.manridy.iband.R;
import com.manridy.iband.bean.BoModel;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.manridy.applib.base.BaseActivity.isFastDoubleClick;

/**
 * 血氧
 * Created by jarLiao on 2016/10/24.
 */

public class BoFragment extends BaseEventFragment {

    @BindView(R.id.di_data1)
    DataItems diData1;
    @BindView(R.id.di_data2)
    DataItems diData2;
    @BindView(R.id.di_data3)
    DataItems diData3;
    @BindView(R.id.cv_bo)
    CircularView cvBo;
    @BindView(R.id.lc_bo)
    LineChart lcBo;

    BoModel curBo;
    List<BoModel> curBoList;
    float avgBo, maxBo, minBo;
    boolean isTestData = true;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.tv_end)
    TextView tvEnd;

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        root = inflater.inflate(R.layout.fragment_bo, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    protected void initVariables() {
        initChartView(lcBo);
        initChartAxis(lcBo);
    }

    @Override
    protected void initListener() {
        IbandApplication.getIntance().service.watch.setBoNotifyListener(new BleNotifyListener() {
            @Override
            public void onNotify(Object o) {//上报不做保存处理
                curBo = new Gson().fromJson(o.toString(), BoModel.class);
                if (isTestData) {
                    curBoList = new ArrayList<>();
                }
                isTestData = false;
                curBoList.add(curBo);
                EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_BO));
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_BO));

    }

    @OnClick({R.id.iv_history})
    public void onClick(View view) {
        if (isFastDoubleClick()) return;
        switch (view.getId()) {
            case R.id.iv_history:
                Intent intent = new Intent(mContext, HrHistoryActivity.class);
                intent.putExtra("history_type", 2);
                startActivity(intent);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.REFRESH_VIEW_BO) {
            setCircularView();
            updateChartView(lcBo, curBoList);
            setDataItem();
        } else if (event.getWhat() == EventGlobal.ACTION_HR_TESTING) {
            cvBo.setTitle("测量中").invaliDate();
        } else if (event.getWhat() == EventGlobal.ACTION_HR_TESTED) {
            cvBo.setTitle("上次测量结果").invaliDate();
            isTestData = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroundEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.DATA_LOAD_BO) {
            curBo = IbandDB.getInstance().getLastBo();
            curBoList = IbandDB.getInstance().getLastsBo();
            Collections.reverse(curBoList); // 倒序排列
            EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_BO));
        } else if (event.getWhat() == EventGlobal.REFRESH_VIEW_ALL) {
            EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_BO));
        }
    }

    private void initChartView(LineChart chart) {
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
        chart.getLegend().setEnabled(false);
        chart.setData(new LineData());

    }

    private void initChartAxis(LineChart chart) {
        //x轴坐标
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);//显示x轴
        xAxis.setTextColor(Color.BLACK);//x轴文字颜色
        xAxis.setTextSize(12f);//x轴文字大小
        xAxis.setDrawGridLines(false);//取消网格线
        xAxis.setDrawAxisLine(false);//取消x轴底线
        xAxis.setDrawLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x轴位置
        xAxis.setAxisMinimum(0);//设置最小点
        xAxis.setGranularity(1f);//设置间隔
        //Y轴坐标
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(90);//设置y轴最小点
        yAxis.setAxisMaximum(100f);
        yAxis.setDrawAxisLine(false);//画坐标线
        yAxis.setDrawLabels(false);//画坐标下标
        yAxis.setDrawGridLines(false);//设置网格线
        yAxis.setDrawZeroLine(false);
        yAxis.setEnabled(true);//显示Y轴
        chart.getAxisRight().setEnabled(false);//不显示右侧
    }

    private LineDataSet getInitChartDataSet() {
        LineDataSet set = new LineDataSet(null, "");//初始化折线数据源
        set.setColor(Color.parseColor("#deff4081"));//折线颜色
        set.setLineWidth(1.5f);//折线宽度
        set.setCircleRadius(3f);
        set.setCircleColorHole(Color.parseColor("#deff4081"));
        set.setCircleColor(Color.parseColor("#deff4081"));//设置圆点颜色
        set.setDrawHighlightIndicators(false);
        set.setDrawValues(false);//显示颜色值
        return set;
    }

    private void updateChartView(LineChart chart, List<BoModel> boModels) {
        if (boModels == null || boModels.size() <= 0) {
            return;
        }
        LineData data = new LineData(getInitChartDataSet());
        if (chart.getData() != null) {
            chart.clearValues();
        }
        chart.setData(data);
        //判断数据数量
        for (int i = 0; i < boModels.size(); i++) {
            data.addEntry(new Entry(i, Float.valueOf(boModels.get(i).getboRate())), 0);
        }
        // 像ListView那样的通知数据更新
//        mvHeart.setHeartList(heartList);
//        chart.setMarker(mvHeart);

        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMinimum(15);
        chart.setVisibleXRangeMaximum(15);
        chart.moveViewToX(data.getEntryCount());
    }

    private void setDataItem() {
        avgBo = maxBo = minBo = 0;
        if (curBoList.size() > 0) {
            minBo = Float.parseFloat(curBo.getboRate());
            for (BoModel heartModel : curBoList) {
                float hr = Float.parseFloat(heartModel.getboRate());
                avgBo += hr;
                maxBo = maxBo > hr ? maxBo : hr;
                minBo = minBo < hr ? minBo : hr;
            }
            avgBo /= curBoList.size();
            String start = curBoList.get(0).getboDate().substring(11, 19);
            String end = curBoList.get(curBoList.size() - 1).getboDate().substring(11, 19);
            tvStart.setText(start);
            tvEnd.setText(end);
        }
        diData1.setItemData("平均值", getOne(avgBo));
        diData2.setItemData("最低值", getOne(minBo));
        diData3.setItemData("最高值", getOne(maxBo));
    }

    private void setCircularView() {
        if (curBo == null) return;
        String text = curBo.getboRate();
        String state = curBo.getboDate();
        float progress = 0;
        if (curBo.getboRate() != null && !curBo.getboRate().isEmpty()) {
            progress = Float.parseFloat(curBo.getboRate());
        }
        cvBo.setText(text)
                .setState(state)
                .setProgress(progress)
                .invaliDate();
    }


    public String getOne(float f) {
        return String.format("%.1f", f);
    }

}
