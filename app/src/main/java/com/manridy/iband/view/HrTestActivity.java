package com.manridy.iband.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.manridy.iband.adapter.HistoryAdapter;
import com.manridy.iband.R;
import com.manridy.iband.ui.items.DataItems;
import com.manridy.iband.view.base.BaseActionActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 心率持续测量
 * Created by jarLiao on 17/5/11.
 */

public class HrTestActivity extends BaseActionActivity {


    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_unit)
    TextView tvUnit;
    @BindView(R.id.bt_test)
    Button btTest;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.di_data1)
    DataItems diData1;
    @BindView(R.id.di_data2)
    DataItems diData2;
    @BindView(R.id.di_data3)
    DataItems diData3;
    @BindView(R.id.rv_history)
    RecyclerView rvHistory;

    HistoryAdapter historyAdapter;
    List<HistoryAdapter.Item> itemList = new ArrayList<>();

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_test_hr);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        int color = Color.parseColor("#ef5350");
        setTitleBar("实时心率", color);
        setStatusBarColor(color);
        historyAdapter = new HistoryAdapter(getDataList());
        rvHistory.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvHistory.setAdapter(historyAdapter);
    }

    private List<HistoryAdapter.Item> getDataList() {
        itemList.add(new HistoryAdapter.Item("5月9日 13:46", "", "次/分钟", "78"));
        itemList.add(new HistoryAdapter.Item("5月9日 13:46", "", "次/分钟", "78"));
        itemList.add(new HistoryAdapter.Item("5月9日 13:46", "", "次/分钟", "78"));
        itemList.add(new HistoryAdapter.Item("5月9日 13:46", "", "次/分钟", "78"));
        itemList.add(new HistoryAdapter.Item("5月9日 13:46", "", "次/分钟", "78"));
        itemList.add(new HistoryAdapter.Item("5月9日 13:46", "", "次/分钟", "78"));
        itemList.add(new HistoryAdapter.Item("5月9日 13:46", "", "次/分钟", "78"));
        itemList.add(new HistoryAdapter.Item("5月9日 13:46", "", "次/分钟", "78"));
        itemList.add(new HistoryAdapter.Item("5月9日 13:46", "", "次/分钟", "78"));
        itemList.add(new HistoryAdapter.Item("5月9日 13:46", "", "次/分钟", "78"));
        itemList.add(new HistoryAdapter.Item("5月9日 13:46", "", "次/分钟", "78"));
        itemList.add(new HistoryAdapter.Item("5月9日 13:46", "", "次/分钟", "78"));
        return itemList;
    }


    @Override
    protected void initListener() {

    }



}
