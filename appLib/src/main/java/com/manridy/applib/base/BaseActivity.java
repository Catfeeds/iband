package com.manridy.applib.base;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.manridy.applib.R;
import com.manridy.applib.common.AppManage;
import com.manridy.applib.utils.ToastUtil;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;


/**
 * 抽象基类activity
 * 分成三部分:初始化布局、初始化变量、加载数据
 * 封装公共方法
 * Created by Administrator on 2016/10/14.
 */

public abstract class BaseActivity extends AppCompatActivity  implements SwipeBackActivityBase {
    protected final String TAG = this.getClass().getSimpleName();
    protected Context mContext;
    protected boolean immersiveMode = false;
    protected boolean isBack = false;
    private ProgressDialog dialog;
    private SwipeBackActivityHelper mHelper;
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        AppManage.getInstance().addActivity(this);
        getSupportActionBar().hide();
        initView(savedInstanceState);
        initVariables();
        initListener();
        loadData();
    }

    protected void initBack() {
        isBack = true;
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (isBack) {
            mHelper.onPostCreate();
        }
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }


    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void initVariables();

    protected abstract void initListener();

    protected void loadData(){}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManage.getInstance().finishActivity(this);
    }

    /**
     * 全局toast
     * @param msg 消息
     */
    protected void showToast(String msg){
        ToastUtil.showToast(mContext,msg);
    }

    /**
     * Snackbar
     * @param view 显示视图
     * @param msg 消息
     */
    protected void showSnackbar(View view, String msg){
        Snackbar.make(view,msg,Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 进度条
     * @param msg
     */
    public void showProgress(String msg) {
        if (!this.isFinishing()) {
            dialog = new ProgressDialog(mContext);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage(msg);
            dialog.show();
        }
    }

    public void showProgress(String msg,DialogInterface.OnCancelListener listener) {
        if (!this.isFinishing()) {
            dialog = new ProgressDialog(mContext);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage(msg);
            dialog.setOnCancelListener(listener);
            dialog.show();
        }
    }

    /**
     * 进度条消失
     */
    public void dismissProgress(){
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    /**
     * 沉浸式模式 隐藏状态栏和导航栏
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (immersiveMode) {
            if (hasFocus && Build.VERSION.SDK_INT >= 19) {
                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    /**
     * 设置沉浸式
     */
    public void setStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    /**
     * 设置状态栏颜色
     * @param color
     */
    public void setStatusBarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
//            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
//            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
//        }
    }

    /**
     * 解决字体大小跟随系统导致错乱
     * @return
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }

    /**
     * 检测是否拥有权限
     * @param permission
     * @return
     */
    public boolean checkPermission(String permission){
        if (ContextCompat.checkSelfPermission(mContext,permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * 检测是否拥有权限
     * @param permissions
     * @return
     */
    public boolean checkPermissions(String[] permissions){
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext,permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测是否勾选不再提示
     * @param permission
     * @return
     */
    public boolean checkShouldShow(String permission){
        return !ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permission);
    }

    /**
     * 请求权限
     * @param permissions
     * @param requestCode
     */
    public void requestPermissios(String[] permissions,int requestCode){
        ActivityCompat.requestPermissions((Activity) mContext,permissions,requestCode);
    }

    public void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }


    static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        return timeD <= 600;
    }
}
