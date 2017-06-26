package com.manridy.iband.view;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.manridy.applib.base.BaseActivity;
import com.manridy.applib.common.AppManage;
import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.R;
import com.manridy.iband.common.AppGlobal;

import java.util.ArrayList;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;

/**
 * 启动
 */
public class StartActivity extends BaseActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1500; //延迟
    private View view;
    private boolean isFirstOpen;
    @Override
    protected void initView(Bundle savedInstanceState) {
        isFirstOpen = (boolean) SPUtil.get(mContext, AppGlobal.DATA_APP_FIRST,true);
        // 如果是第一次启动，则先进入功能引导页
        if (isFirstOpen) {
            startActivity(new Intent(mContext, WelcomeActivity.class));
            finish();
            return;
        }
        view = View.inflate(this, R.layout.activity_start,null);
        setContentView(view);
    }

    @Override
    protected void initVariables() {
        if (!isFirstOpen) {
            startAnmin();
        }
    }

    private void initPermisson() {
        List<PermissonItem> permissonItems = new ArrayList<PermissonItem>();
        permissonItems.add(new PermissonItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储空间", R.mipmap.permission_ic_sd));
        permissonItems.add(new PermissonItem(Manifest.permission.ACCESS_FINE_LOCATION, "位置信息", R.mipmap.permission_ic_location));
        permissonItems.add(new PermissonItem(Manifest.permission.CAMERA, "拍照权限", R.mipmap.permission_ic_camera));
        HiPermission.create(mContext)
                .permissions(permissonItems)
//                .filterColor(Color.parseColor("#2196f3"))
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(permissionCallback);
    }

    private void startAnmin() {
        //渐变展示启动屏
        AlphaAnimation anima = new AlphaAnimation(0.2f,1.0f);
        anima.setDuration(SPLASH_DISPLAY_LENGTH);
        view.startAnimation(anima);
        anima.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0) {
                initPermisson();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationStart(Animation animation) {}

        });
    }

    @Override
    protected void initListener() {

    }

    PermissionCallback permissionCallback =  new PermissionCallback() {
        @Override
        public void onClose() {//未授权
            Log.i(TAG, "onClose");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppManage.getInstance().finishAllActivity();
                }
            },300);
        }

        @Override
        public void onFinish() {//所有授权成功
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(mContext,MainActivity.class));
                    finish();
                }
            },300);

        }

        @Override
        public void onDeny(String permisson, int position) {
            Log.i(TAG, "onDeny");
        }

        @Override
        public void onGuarantee(String permisson, int position) {
            Log.i(TAG, "onGuarantee");
        }
    };


    @Override
    public void onBackPressed() {
        // super.onBackPressed();  //不要调用父类的方法
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }





}
