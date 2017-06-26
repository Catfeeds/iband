package com.manridy.iband.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.manridy.iband.IbandDB;
import com.manridy.iband.R;
import com.manridy.iband.bean.BoModel;
import com.manridy.iband.bean.BpModel;
import com.manridy.iband.bean.HeartModel;
import com.manridy.iband.bean.SleepModel;
import com.manridy.iband.bean.StepModel;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.view.base.BaseActionActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jarLiao on 17/6/13.
 */

public class TestActivity extends BaseActionActivity {

    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    StringBuffer buffer;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        registerEventBus();
        buffer = new StringBuffer();
        setTitleBar("数据库");
    }

    @Override
    protected void initListener() {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                EventBus.getDefault().post(0);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsyncThread(Integer position){
        Log.d(TAG, "onEventAsyncThread() called with: position = [" + position + "]");
        buffer = new StringBuffer();
        switch (position) {
            case 0:
                List<StepModel> stepList = IbandDB.getInstance().getStepList();
                for (StepModel stepModel : stepList) {
                    buffer.append(stepModel.toString());
                    buffer.append("\n\r");
                }
                break;
            case 1:
                List<SleepModel> sleepList = IbandDB.getInstance().getSleepList();
                for (SleepModel stepModel : sleepList) {
                    buffer.append(stepModel.toString());
                    buffer.append("\n\r");
                }
                break;
            case 2:
                List<HeartModel> hrList = IbandDB.getInstance().getHrList();
                for (HeartModel stepModel : hrList) {
                    buffer.append(stepModel.toString());
                    buffer.append("\n\r");
                }
                break;
            case 3:
                List<BpModel> bpList = IbandDB.getInstance().getBpList();
                for (BpModel stepModel : bpList) {
                    buffer.append(stepModel.toString());
                    buffer.append("\n\r");
                }
                break;
            case 4:
                List<BoModel> boList = IbandDB.getInstance().getBoList();
                for (BoModel stepModel : boList) {
                    buffer.append(stepModel.toString());
                    buffer.append("\n\r");
                }
                break;
        }
        handler.sendEmptyMessage(0);
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            text.setText(buffer.toString());
        }
    };
}
