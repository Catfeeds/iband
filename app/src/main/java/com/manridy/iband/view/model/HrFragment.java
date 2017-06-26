package com.manridy.iband.view.model;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.manridy.iband.bean.HeartModel;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.ui.CircularView;
import com.manridy.iband.ui.items.DataItems;
import com.manridy.iband.view.HrTestActivity;
import com.manridy.iband.view.base.BaseEventFragment;
import com.manridy.iband.view.history.HrHistoryActivity;
import com.manridy.sdk.ble.BleCmd;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.callback.BleNotifyListener;
import com.manridy.sdk.exception.BleException;

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
 * 心率
 * Created by jarLiao on 2016/10/24.
 */

public class HrFragment extends BaseEventFragment {

    @BindView(R.id.di_data1)
    DataItems diData1;
    @BindView(R.id.di_data2)
    DataItems diData2;
    @BindView(R.id.di_data3)
    DataItems diData3;
    @BindView(R.id.iv_test)
    TextView ivTest;
    @BindView(R.id.cv_hr)
    CircularView cvHr;
    @BindView(R.id.bt_test)
    Button btTest;
    @BindView(R.id.lc_hr)
    LineChart lcHr;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.tv_end)
    TextView tvEnd;

    HeartModel curHeart;
    List<HeartModel> curHeartList;
    int avgHr, maxHr, minHr;
    boolean isTestData = true;

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        root = inflater.inflate(R.layout.fragment_hr, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    protected void initVariables() {
        initChartView(lcHr);
        initChartAxis(lcHr);
    }

    @Override
    protected void initListener() {
        IbandApplication.getIntance().service.watch.setHrNotifyListener(new BleNotifyListener() {
            @Override
            public void onNotify(Object o) {//上报不做保存处理
                curHeart = new Gson().fromJson(o.toString(), HeartModel.class);
                if (isTestData) {
                    curHeartList = new ArrayList<>();
                }
                isTestData = false;
                curHeartList.add(curHeart);
                EventBus.getDefault().post(new EventMessage(EventGlobal.ACTION_HR_TESTING));
                EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_HR));
            }
        });

        btTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btTest.getText().equals("测量")) {
                    IbandApplication.getIntance().service.watch.sendCmd(BleCmd.setHrTest(2), new BleCallback() {
                        @Override
                        public void onSuccess(Object o) {
                            EventBus.getDefault().post(new EventMessage(EventGlobal.ACTION_HR_TESTING));
                        }

                        @Override
                        public void onFailure(BleException exception) {

                        }
                    });
                } else {
                    IbandApplication.getIntance().service.watch.sendCmd(BleCmd.setHrTest(0));
                    EventBus.getDefault().post(new EventMessage(EventGlobal.ACTION_HR_TESTED));
                }
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_HR));
    }


    @OnClick({R.id.iv_test, R.id.iv_history})
    public void onClick(View view) {
        if (isFastDoubleClick()) return;
        switch (view.getId()) {
            case R.id.iv_history:
                startActivity(HrHistoryActivity.class);
                break;
            case R.id.iv_test:
                startActivity(HrTestActivity.class);
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.REFRESH_VIEW_HR) {
            setCircularView();
            updateChartView(lcHr, curHeartList);
            setDataItem();
        } else if (event.getWhat() == EventGlobal.ACTION_HR_TESTING) {
            btTest.setText("停止");
            cvHr.setTitle("测量中").invaliDate();
        } else if (event.getWhat() == EventGlobal.ACTION_HR_TESTED) {
            btTest.setText("测量");
            cvHr.setTitle("上次测量结果").invaliDate();
            isTestData = true;
            EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_SYNC_HISTORY));
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroundEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.DATA_LOAD_HR) {
            curHeart = IbandDB.getInstance().getLastHeart();
            curHeartList = IbandDB.getInstance().getLastsHeart();
            Collections.reverse(curHeartList); // 倒序排列
            EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_HR));
        } else if (event.getWhat() == EventGlobal.REFRESH_VIEW_ALL) {
            EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_LOAD_HR));
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
//        yAxis.setAxisMinimum(0);//设置y轴最小点
        yAxis.setAxisMaximum(220f);
        yAxis.setDrawAxisLine(false);//画坐标线
        yAxis.setDrawLabels(false);//画坐标下标
        yAxis.setDrawGridLines(false);//设置网格线
        yAxis.setDrawZeroLine(false);
        yAxis.setEnabled(true);//显示Y轴
        chart.getAxisRight().setEnabled(false);//不显示右侧
    }

    private LineDataSet getInitChartDataSet() {
        LineDataSet set = new LineDataSet(null, "");//初始化折线数据源
        set.setColor(Color.parseColor("#deef5350"));//折线颜色
        set.setLineWidth(1.5f);//折线宽度
//        set.setValueTextColor(Color.BLACK);//折线值文字颜色
//        set.setDrawCircleHole(false);
        set.setCircleRadius(3f);
        set.setValueTextSize(12f);//折线值文字大小
        set.setCircleColorHole(Color.parseColor("#deef5350"));
        set.setCircleColor(Color.parseColor("#deef5350"));//设置圆点颜色
        set.setDrawHighlightIndicators(false);
        set.setDrawValues(false);//显示颜色值
        return set;
    }

    private void updateChartView(LineChart chart, List<HeartModel> heartList) {
        if (heartList == null || heartList.size() <= 0) {
            return;
        }
        LineData data = new LineData(getInitChartDataSet());
        if (chart.getData() != null) {
            chart.clearValues();
        }
        //判断数据数量
        for (int i = 0; i < heartList.size(); i++) {
            data.addEntry(new Entry(i, heartList.get(i).getHeartRate()), 0);
        }
        chart.setData(data);
        // 像ListView那样的通知数据更新
//        mvHeart.setHeartList(heartList);
//        chart.setMarker(mvHeart);
        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMinimum(15);
        chart.setVisibleXRangeMaximum(15);
        chart.moveViewToX(data.getEntryCount());
    }

    private void setDataItem() {
        avgHr = maxHr = minHr = 0;
        if (curHeartList.size() > 0) {
            minHr = curHeart.getHeartRate();
            for (HeartModel heartModel : curHeartList) {
                int hr = heartModel.getHeartRate();
                avgHr += hr;
                maxHr = maxHr > hr ? maxHr : hr;
                minHr = minHr < hr ? minHr : hr;
            }
            avgHr /= curHeartList.size();

            String start = curHeartList.get(0).getHeartDate().substring(11, 19);
            String end = curHeartList.get(curHeartList.size() - 1).getHeartDate().substring(11, 19);
            tvStart.setText(start);
            tvEnd.setText(end);
        }
        diData1.setItemData("平均心率", avgHr + "");
        diData2.setItemData("最低心率", minHr + "");
        diData3.setItemData("最高心率", maxHr + "");
    }

    private void setCircularView() {
        if (curHeart == null) return;
        String text = curHeart.getHeartRate() + "";
        float progress = (float) ((curHeart.getHeartRate() / 220.0) * 100);
//        cvHr.setProgressWithAnimation(progress);
        cvHr.setText(text)
                .setProgress(progress)
                .invaliDate();
    }


}
