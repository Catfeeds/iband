package com.manridy.iband.view.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.TextView;

import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.R;
import com.manridy.iband.adapter.ViewAdapter;
import com.manridy.iband.bean.ViewModel;
import com.manridy.iband.ui.items.UnitItems;
import com.manridy.iband.view.base.BaseActionActivity;
import com.manridy.sdk.ble.BleCmd;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.exception.BleException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 遥控拍照页面
 * Created by jarLiao on 17/5/4.
 */

public class TimeActivity extends BaseActionActivity {
    @BindView(R.id.tb_menu)
    TextView tbMenu;
    @BindView(R.id.unit_hour_24)
    UnitItems unitHour24;
    @BindView(R.id.unit_hour_12)
    UnitItems unitHour12;

    @BindView(R.id.rv_time)
    RecyclerView rvView;
    ViewAdapter viewAdapter;
    int curTime;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_time);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleAndMenu("时间格式", "保存");
        curTime = (int) SPUtil.get(mContext, AppGlobal.DATA_SETTING_UNIT_TIME, 0);
        selectTime();
    }

    private void initRecycler() {
        viewAdapter = new ViewAdapter(mContext, getMenuData());
        rvView.setLayoutManager(new GridLayoutManager(mContext, 3));
        rvView.setItemAnimator(new DefaultItemAnimator());//设置动画效果
        ((SimpleItemAnimator) rvView.getItemAnimator()).setSupportsChangeAnimations(false);
        rvView.setAdapter(viewAdapter);
        viewAdapter.setOnItemClickListener(new ViewAdapter.onItemClickListener() {
            @Override
            public void itemClick(int dataPosition, int position) {
                List<ViewModel> viewModels = (List<ViewModel>) viewAdapter.getData();
                for (int i = 0; i < viewModels.size(); i++) {
                    if (dataPosition == i) {
                        viewModels.get(i).setSelect(true);
                    } else {
                        viewModels.get(i).setSelect(false);
                    }
                }
                viewAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void initListener() {

        tbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress("正在保存中...");
                mIwaerApplication.service.watch.sendCmd(BleCmd.setHourSelect(curTime), new BleCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        SPUtil.put(mContext, AppGlobal.DATA_SETTING_UNIT_TIME,curTime);
                        dismissProgress();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("保存成功");
                            }
                        });
                        finish();
                    }

                    @Override
                    public void onFailure(BleException exception) {
                        dismissProgress();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("保存失败");
                            }
                        });
                    }
                });
            }
        });
    }

    private List<ViewModel> getMenuData() {
        List<ViewModel> viewList = new ArrayList<>();
        viewList.add(new ViewModel(0, "12时", R.mipmap.time_12, false));
        viewList.add(new ViewModel(1, "24时", R.mipmap.time_24, true));
        viewList.add(new ViewModel(2, "石英", R.mipmap.time_quartz, false));
        return viewList;
    }

    @OnClick({R.id.unit_hour_24, R.id.unit_hour_12})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.unit_hour_24:
                curTime = curTime == 0 ? 1:0;
                selectTime();
                break;
            case R.id.unit_hour_12:
                curTime = curTime == 0 ? 1:0;
                selectTime();
                break;
        }
    }

    private void selectTime() {
        boolean isSelect = curTime == 0;
        unitHour24.selectView(isSelect);
        unitHour12.selectView(!isSelect);
    }
}
