package com.manridy.iband.view.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.manridy.applib.base.BaseActivity;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.IbandApplication;
import com.manridy.iband.R;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.exception.BleException;

import org.greenrobot.eventbus.EventBus;

/**
 * 基类
 * Created by jarLiao on 17/5/6.
 */

public abstract class BaseActionActivity extends BaseActivity {
    boolean isRegistEventBus;
    public IbandApplication mIwaerApplication;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mIwaerApplication = (IbandApplication) getApplication();
        super.onCreate(savedInstanceState);
        setStatusBar();
        initBack();
    }

    protected void registerEventBus() {
        isRegistEventBus = true;
        EventBus.getDefault().register(this);
    }

    public BleCallback mBleCallback = new BleCallback() {
        @Override
        public void onSuccess(Object o) {

        }

        @Override
        public void onFailure(BleException exception) {

        }
    };

    /**
     * 初始化TitleBar(默认)
     * 返回键 finish
     */
    protected void setTitleBar(){
        findViewById(R.id.tb_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化TitleBar(加标题)
     * @param title 标题
     * 返回键 finish
     */
    protected void setTitleBar(String title){
        findViewById(R.id.tb_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tb_title)).setText(title);
    }

    protected void setTitleBar(String title,int color){
        findViewById(R.id.tb_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tb_title)).setText(title);
        findViewById(R.id.rl_tab).setBackgroundColor(color);
    }

    protected void setTitleAndMenu(String title,int res){
        findViewById(R.id.tb_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tb_title)).setText(title);
        ImageView tbMenu = (ImageView)findViewById(R.id.tb_share);
        tbMenu.setImageResource(res);
        tbMenu.setVisibility(View.VISIBLE);
    }

    protected void setTitleAndMenu(String title,String menu){
        findViewById(R.id.tb_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tb_title)).setText(title);
        TextView tbMenu = (TextView)findViewById(R.id.tb_menu);
        tbMenu.setText(menu);
        tbMenu.setVisibility(View.VISIBLE);
    }

    /**
     * 启动Activity
     * @param cls 目标Activity
     */
    protected void startActivity(Class<?> cls){
        startActivity(new Intent(mContext,cls));
    }


    /**
     * 启动Activity(加启动类型)
     * @param cls 目标Activity
     * @param intentCode 类型码
     */
    protected void startActivity(Class<?> cls,int intentCode){
        Intent intent = new Intent(mContext,cls);
        intent.putExtra("intentCode",intentCode);
        startActivity(intent);
    }

    /**
     * 得到启动类型
     * @return 启动类型 默认0
     */
    protected int getStartType(){
        return getIntent().getIntExtra("intentCode",0);
    }

    public void eventSend(int what){
        EventBus.getDefault().post(new EventMessage(what));
    }

    public void eventSend(int what,String msg){
        EventBus.getDefault().post(new EventMessage(what,msg));
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegistEventBus) {
            EventBus.getDefault().unregister(this);
        }
    }
}
