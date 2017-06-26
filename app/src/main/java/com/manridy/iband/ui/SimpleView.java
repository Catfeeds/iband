package com.manridy.iband.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dalimao.library.DragView;
import com.dalimao.library.util.FloatUtil;
import com.manridy.applib.common.AppManage;
import com.manridy.iband.R;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.view.DeviceActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 通知悬浮窗
 */
public class SimpleView extends DragView {

    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.bt_test)
    Button btTest;
    View view;
    private Context mContext;
    private int infoType;
    private boolean isShow;
    private boolean isEnd;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isEnd) {
                hideFloatView();
            }
        }
    };

    public SimpleView(Context context) {
        super(context);
        this.mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.item_test, this);
        ButterKnife.bind(this);
        btTest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btTest.getText().toString().equals("开启")) {
                    EventBus.getDefault().post(new EventMessage(EventGlobal.ACTION_BLUETOOTH_OPEN));
                }else  if (btTest.getText().toString().equals("绑定")) {
                    hideFloatView();
                    try {
                        AppManage.getInstance().currentActivity().startActivity(new Intent(AppManage.getInstance().currentActivity()
                            ,DeviceActivity.class));
                    }catch (Exception e){
                        e.toString();
                    }
                }else  if (btTest.getText().toString().equals("连接")) {
                    EventBus.getDefault().post(new EventMessage(EventGlobal.ACTION_DEVICE_CONNECT));
                }else if (btTest.getText().toString().equals("同步")){
                    EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_SYNC_HISTORY));
                }
            }
        });
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (infoType == 0) {
                    hideFloatView();
//                }
            }
        });
    }

    public synchronized void hideFloatView(){
        FloatUtil.hideFloatView(mContext.getApplicationContext(), SimpleView.class, false);
        isShow = false;
    }

    public synchronized void setContent(String content, String bt,boolean isEnd) {
        tvTitle.setText(content);
        this.isEnd = isEnd;
        if (bt != null) {
            btTest.setVisibility(VISIBLE);
            btTest.setText(bt);
            infoType = 1;
        } else {
            btTest.setVisibility(GONE);
            infoType = 0;
        }
        if (isEnd) {
            handler.sendEmptyMessageDelayed(0,3000);
        }
        isShow = true;
    }

    public boolean isShow() {
        return isShow;
    }
}