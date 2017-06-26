package com.manridy.iband.view.alert;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.manridy.applib.common.AppManage;
import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.IbandDB;
import com.manridy.iband.R;
import com.manridy.iband.adapter.AppAdapter;
import com.manridy.iband.bean.AppModel;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.common.OnItemClickListener;
import com.manridy.iband.service.NotificationService2;
import com.manridy.iband.ui.items.AlertBigItems;
import com.manridy.iband.view.base.BaseActionActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.weyye.hipermission.PermissionAdapter;
import me.weyye.hipermission.PermissionView;
import me.weyye.hipermission.PermissonItem;

/**
 * 应用提醒
 * Created by jarLiao on 17/5/4.
 */

public class AppActivity extends BaseActionActivity {
    public static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    @BindView(R.id.ai_alert)
    AlertBigItems aiAlert;
    @BindView(R.id.rv_app)
    RecyclerView rvApp;
    boolean onOff;
    List<AppModel> curAppList;
    AppAdapter mAppAdapter;
    List<AppAdapter.Menu> menuList;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_app);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleAndMenu("APP提醒", "保存");
        onOff = (boolean) SPUtil.get(mContext, AppGlobal.DATA_ALERT_APP,false);
        aiAlert.setAlertCheck(onOff);
        menuList = getMenuList();
        curAppList = IbandDB.getInstance().getAppList();
        if (curAppList != null && curAppList.size() > 0) {
            for (AppModel appModel : curAppList) {
                for (AppAdapter.Menu menu : menuList) {
                    if (appModel.getAppId() == menu.menuId) {
                        menu.menuCheck = appModel.isOnOff();
                    }
                }
            }
        }
        mAppAdapter = new AppAdapter(menuList);
        rvApp.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        rvApp.setAdapter(mAppAdapter);

    }

    @Override
    protected void initListener() {
        aiAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectApp(!onOff);
            }
        });

        mAppAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (onOff) {
                            AppAdapter.Menu menu = menuList.get(position);
                            menu.menuCheck = !menu.menuCheck;
                            mAppAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        findViewById(R.id.tb_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.put(mContext, AppGlobal.DATA_ALERT_APP,onOff);
                IbandDB.getInstance().saveAppList(menuList);
                showToast("保存成功");
                finish();
            }
        });
    }

    private void selectApp(boolean isChecked){
        if (!NotificationService2.isNotificationListenEnable(mContext)) {
            OpenNotifiactionDialog();
            return;
        }
        if (isChecked) {
            NotificationService2.startNotificationService(mContext);
                onOff = true;
            }else {
                NotificationService2.stopNotificationService(mContext);
                onOff = false;
            }
        aiAlert.setAlertCheck(onOff);

    }

    private List<AppAdapter.Menu> getMenuList(){
        List<AppAdapter.Menu> menuList = new ArrayList<>();
        menuList.add(new AppAdapter.Menu(4,"微信",R.mipmap.appremind_wechat));
        menuList.add(new AppAdapter.Menu(2,"QQ",R.mipmap.appremind_qq));
        menuList.add(new AppAdapter.Menu(5,"WhatsApp",R.mipmap.appremind_whatsapp));
        menuList.add(new AppAdapter.Menu(6,"Facebook",R.mipmap.appremind_facebook));
        return menuList;
    }

    public void OpenNotifiactionDialog(){
        PermissionView contentView = new PermissionView(this);
        List<PermissonItem> data = new ArrayList<>();
        data.add(new PermissonItem("通知","通知",R.mipmap.permission_ic_notice));
        contentView.setGridViewColum(data.size());
        contentView.setTitle("开启提醒");
        contentView.setMsg("为了您能正常使用应用提醒，需要获取通知权限");
        contentView.setGridViewAdapter(new PermissionAdapter(data));
        contentView.setStyleId(R.style.PermissionBlueStyle);
//        contentView.setFilterColor(mFilterColor);
        final AlertDialog bluetoothDialog = new AlertDialog.Builder(AppManage.getInstance().currentActivity())
                .setView(contentView)
                .create();
        contentView.setBtnOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothDialog != null && bluetoothDialog.isShowing())
                    bluetoothDialog.dismiss();
//                Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivityForResult(intent,10000);
                NotificationService2.startNotificationListenSettings(mContext);
                onOff = true;
            }
        });
        bluetoothDialog.setCanceledOnTouchOutside(false);
        bluetoothDialog.setCancelable(false);
        bluetoothDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bluetoothDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isEnable = NotificationService2.isNotificationListenEnable(mContext);
        aiAlert.setAlertCheck(isEnable && onOff);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
//        if (requestCode == 10000) {
//
//        }
//    }
}
