package com.manridy.iband.view;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.manridy.applib.base.BaseActivity;
import com.manridy.applib.common.AppManage;
import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.R;
import com.manridy.iband.adapter.ViewPagerAdapter;
import com.manridy.iband.common.AppGlobal;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;

/**
 * 引导页
 */
public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.vp_view)
    ViewPager vpView;
    @BindView(R.id.piv_dots)
    PageIndicatorView pivDots;
    List<View> viewList;
    PagerAdapter pagerAdapter;
    // 引导页图片资源
    private static final int[] pics = {R.mipmap.launch_image_1,
            R.mipmap.launch_image_2, R.mipmap.launch_image_3};
    @BindView(R.id.bt_ok)
    Button btOk;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        viewList = getViewList();
        pagerAdapter = new ViewPagerAdapter(viewList);
        vpView.setAdapter(pagerAdapter);
        pivDots.setViewPager(vpView);
        initPermisson();
    }

    private List<View> getViewList() {
        List<View> viewList = new ArrayList<>();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(params);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(pics[i]);
            viewList.add(iv);
        }
        return viewList;
    }

    @Override
    protected void initListener() {
        vpView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == pics.length - 1) {
                    btOk.setVisibility(View.VISIBLE);
                }else {
                    btOk.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.put(mContext, AppGlobal.DATA_APP_FIRST,false);
                startActivity(new Intent(mContext,MainActivity.class));
                finish();
            }
        });
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
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    startActivity(new Intent(mContext,MainActivity.class));
//                    finish();
//                }
//            },300);

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
}
