package com.manridy.iband.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.dalimao.library.util.FloatUtil;
import com.manridy.applib.base.BaseActivity;
import com.manridy.applib.common.AppManage;
import com.manridy.applib.utils.CheckUtil;
import com.manridy.applib.utils.SPUtil;
import com.manridy.applib.utils.TimeUtil;
import com.manridy.iband.IbandApplication;
import com.manridy.iband.R;
import com.manridy.iband.SyncData;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.common.Utils;
import com.manridy.iband.service.NotificationService2;
import com.manridy.iband.ui.SimpleView;
import com.manridy.iband.view.model.BoFragment;
import com.manridy.iband.view.model.BpFragment;
import com.manridy.iband.view.model.HrFragment;
import com.manridy.iband.view.model.SleepFragment;
import com.manridy.iband.view.model.StepFragment;
import com.manridy.sdk.ble.BleCmd;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.callback.BleConnectCallback;
import com.manridy.sdk.exception.BleException;
import com.rd.PageIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.weyye.hipermission.PermissionAdapter;
import me.weyye.hipermission.PermissionView;
import me.weyye.hipermission.PermissonItem;

/**
 * 主页
 * Created by jarLiao on 17/5/4.
 */

public class MainActivity extends BaseActivity {

    @BindView(R.id.vp_model)
    ViewPager vpModel;
    @BindView(R.id.piv_dots)
    PageIndicatorView pivDots;
    @BindView(R.id.tb_title)
    TextView tbTitle;
    @BindView(R.id.tb_set)
    ImageView tbSet;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tb_sync)
    TextView tbSync;
    @BindView(R.id.prl_refresh)
    PullRefreshLayout prlRefresh;
    @BindView(R.id.view)
    TextView view;

    private FragmentPagerAdapter viewAdapter;
    private List<Fragment> viewList = new ArrayList<>();

    private IbandApplication iwaerApplication;
    private NotificationManager mNotificationManager;
    private Notification notification;
    private AlertDialog findPhone;
    private AlertDialog lostAlert;
    private Vibrator vibrator;
    private MediaPlayer mp;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!iwaerApplication.service.watch.isBluetoothEnable()) {
                playAlert(true, alertTime);
                String time = TimeUtil.getNowYMDHMSTime();
                showLostNotification(time);
                showLostAlert(time);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        int connectState = (int) SPUtil.get(mContext,AppGlobal.DATA_DEVICE_CONNECT_STATE,AppGlobal.DEVICE_UNCONNECT);
        if (connectState == 1) {
            long time = (long) SPUtil.get(mContext, AppGlobal.DATA_SYNC_TIME, 0L);
            if (time != 0 && tbSync != null) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                String str = format.format(new Date(time));
                tbSync.setText("上次同步" + str);
            }
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        EventBus.getDefault().register(this);
        iwaerApplication = (IbandApplication) getApplication();
        setStatusBar();
        initViewPager();
        initNotification();
        mSimpleView = new SimpleView(mContext.getApplicationContext());
        boolean appOnOff = (boolean) SPUtil.get(this, AppGlobal.DATA_ALERT_APP,false);
        if (appOnOff) {
            startService(new Intent(this,NotificationService2.class));
        }
    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mp = MediaPlayer.create(this, R.raw.alert);
        mp.setLooping(true);
    }

    private void initViewPager() {
        viewList.add(new StepFragment());
        viewList.add(new SleepFragment());
        viewList.add(new HrFragment());
        viewList.add(new BpFragment());
        viewList.add(new BoFragment());

        viewAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return viewList.get(position);
            }

            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
//              super.destroyItem(container, position, object);
            }
        };
        vpModel.setAdapter(viewAdapter);
        pivDots.setViewPager(vpModel);
        vpModel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        prlRefresh.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        prlRefresh.setEnabled(true);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void initListener() {
        tbSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SettingActivity.class));
            }
        });

        vpModel.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                float alpha;
                int index = position;
                if (positionOffset <= 0.5) {
                    alpha = 1 - (positionOffset * 2);
                } else {
                    alpha = (positionOffset * 2) - 1;
                    index++;
                }
                rlTitle.setAlpha(alpha);
                selectTitle(index);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        view.setBackgroundColor(Color.parseColor("#2196f3"));
                        break;
                    case 1:
                        view.setBackgroundColor(Color.parseColor("#673ab7"));
                        break;
                    case 2:
                        view.setBackgroundColor(Color.parseColor("#ef5350"));
                        break;
                    case 3:
                        view.setBackgroundColor(Color.parseColor("#43a047"));
                        break;
                    case 4:
                        view.setBackgroundColor(Color.parseColor("#ff4081"));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        SyncData.getInstance().setSyncAlertListener(new SyncData.OnSyncAlertListener() {
            @Override
            public void onResult(final boolean isSuccess) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prlRefresh.setRefreshing(false);
                        if (isSuccess) {
                            SPUtil.put(mContext,AppGlobal.DATA_SYNC_TIME,new Date().getTime());
                            tbSync.setText("同步完成");
                            EventBus.getDefault().post(new EventMessage(EventGlobal.REFRESH_VIEW_ALL));
                        } else {
                            tbSync.setText("同步失败");
                        }
                    }
                });
                Log.d(TAG, "onResult() called with: isSuccess = [" + isSuccess + "]");
            }

            @Override
            public void onProgress(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String str = progress == 0 ? "" : (progress + "%");
                        tbSync.setText("正在同步" + str);
                    }
                });
                Log.d(TAG, "onProgress() called with: progress = [" + progress + "]");
            }
        });

        prlRefresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final String mac = (String) SPUtil.get(mContext, AppGlobal.DATA_DEVICE_BIND_MAC, "");
                if (checkBindDevice(mac)) return;
                int state = (int) SPUtil.get(mContext, AppGlobal.DATA_DEVICE_CONNECT_STATE, AppGlobal.DEVICE_UNCONNECT);
                if (state != 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tbSync.setText("设备连接中");
                            connectDevice();
                        }
                    });
                }else{
                    EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_SYNC_HISTORY));
                }
            }
        });
    }

    private boolean checkBindDevice(String mac) {
        if (mac == null || mac.isEmpty()) {
            prlRefresh.setRefreshing(false);
            return true;
        }
        return false;
    }

    private void connectDevice(){
        iwaerApplication.service.initConnect(true,new BleConnectCallback() {
            @Override
            public void onConnectSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tbSync.setText("设备连接成功");
                        prlRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onConnectFailure(final BleException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prlRefresh.setRefreshing(false);
                        if (exception.getCode() ==  999) {
                            tbSync.setText("设备未绑定");
                        }else if (exception.getCode() ==  1000){
                            tbSync.setText("设备未找到");
                        }else{
                            tbSync.setText("设备连接失败");
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void loadData() {
        super.loadData();
        String mac = (String) SPUtil.get(mContext, AppGlobal.DATA_DEVICE_BIND_MAC, "");
        int state = (int) SPUtil.get(mContext, AppGlobal.DATA_DEVICE_CONNECT_STATE, AppGlobal.DEVICE_UNCONNECT);
        if (!iwaerApplication.service.watch.isBluetoothEnable()) {
            OpenBluetoothDialog();
        } else if (mac.isEmpty()) {
            showFloatView("未绑定设备", "绑定");
        } else if (state == 0) {
            showFloatView("设备未连接", "连接");
        } else if (state == 1) {
            tbSync.setText("设备已连接");
            EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_SYNC_HISTORY));
        } else if (state == 2) {
            tbSync.setText("设备连接中");
        }
    }

    private void selectTitle(int position) {
        switch (position) {
            case 0:
                tbTitle.setText("计步");
                break;
            case 1:
                tbTitle.setText("睡眠");
                break;
            case 2:
                tbTitle.setText("心率");
                break;
            case 3:
                tbTitle.setText("血压");
                break;
            case 4:
                tbTitle.setText("血氧");
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventMessage event) {
        if (event.getWhat() == EventGlobal.ACTION_FIND_PHONE_START) {
            //开启震动和铃声
            playAlert(true, alertTime);
            showFindPhoneNotification();
            showFindPhoneDialog();
        } else if (event.getWhat() == EventGlobal.ACTION_FIND_PHONE_STOP) {
            if (findPhone != null) {//隐藏窗口
                findPhone.dismiss();
            }
            if (notification != null) {//取消通知
                mNotificationManager.cancel(EventGlobal.ACTION_FIND_PHONE_START);
            }
            playAlert(false, alertTime);
        } else if (event.getWhat() == EventGlobal.STATE_DEVICE_DISCONNECT) {
            boolean isLostOn = (boolean) SPUtil.get(mContext, AppGlobal.DATA_ALERT_LOST, false);
            if (isLostOn) {
                handler.sendEmptyMessageDelayed(0, 15 * 1000);
            }
            tbSync.setText("设备已断开");
        } else if (event.getWhat() == EventGlobal.STATE_DEVICE_CONNECTING) {
            boolean isLostOn = (boolean) SPUtil.get(mContext, AppGlobal.DATA_ALERT_LOST, false);
            if (isLostOn) {
                handler.sendEmptyMessageDelayed(0, 15 * 1000);
            }
            tbSync.setText("设备连接中");
        } else if (event.getWhat() == EventGlobal.STATE_DEVICE_CONNECT) {
            handler.removeMessages(0);
            cancelLostAlert();
            tbSync.setText("设备已连接");
//            if (isFirstConnect) {
            EventBus.getDefault().post(new EventMessage(EventGlobal.DATA_SYNC_HISTORY));
//            }
//            isFirstConnect = false;
        } else if (event.getWhat() == EventGlobal.STATE_CHANGE_BLUETOOTH_ON) {
            if (bluetoothDialog != null && bluetoothDialog.isShowing()) {
                bluetoothDialog.dismiss();
            }
            iwaerApplication.service.watch.clearBluetoothLe();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    iwaerApplication.service.initConnect(false);
                }
            }, 1500);
            hideFloatView();
        } else if (event.getWhat() == EventGlobal.STATE_CHANGE_BLUETOOTH_OFF) {
            iwaerApplication.service.watch.clearBluetoothLe();
            OpenBluetoothDialog();
        } else if (event.getWhat() == EventGlobal.STATE_CHANGE_BLUETOOTH_ON_RUNING) {

        } else if (event.getWhat() == EventGlobal.ACTION_BLUETOOTH_OPEN) {
            iwaerApplication.service.watch.BluetoothEnable(AppManage.getInstance().currentActivity());
        } else if (event.getWhat() == EventGlobal.ACTION_DEVICE_CONNECT) {
            iwaerApplication.service.initConnect(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onBackgroundEvent(EventMessage event) {
        if (event.getWhat() == EventGlobal.DATA_SYNC_HISTORY) {
            SyncData.getInstance().sync();
        }
    }

    SimpleView mSimpleView;

    private void showFloatView(String str, String bt) {
        showFloatView(str, bt, false);
    }

    private void showFloatView(String str, String bt, boolean isEnd) {
        if (mSimpleView.isShow()) {
            mSimpleView.setContent(str, bt, isEnd);
        } else {
            mSimpleView = new SimpleView(mContext.getApplicationContext());
            Point point = new Point();
            point.x = 0;
            if (Utils.checkDeviceHasNavigationBar(mContext)) {
                point.y = Utils.getNavigationBarHeight(mContext);
            } else {
                point.y = 0;
            }
            mSimpleView.setContent(str, bt, isEnd);
            FloatUtil.showFloatView(mSimpleView, Gravity.BOTTOM, WindowManager.LayoutParams.TYPE_TOAST, point, null);
        }
    }

    private void hideFloatView() {
        mSimpleView.hideFloatView();
    }

    private void cancelLostAlert() {
        if (lostAlert != null) {//隐藏窗口
            lostAlert.dismiss();
        }
        if (notification != null) {//取消通知
            mNotificationManager.cancel(EventGlobal.STATE_DEVICE_DISCONNECT);
        }
        playAlert(false, alertTime);
    }


    long alertTime = 10000;
    boolean isPlayAlert;

    public synchronized void playAlert(boolean enable, long time) {
        try {
            if (enable) {
                long[] pattern = {100, 400, 100, 400}; // 停止 开启 停止 开启
                vibrator.vibrate(pattern, 0); //重复两次上面的pattern 如果只想震动一次，index设为-1
                mp.start();
                isPlayAlert = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playAlert(false, alertTime);
                    }
                }, time);
            } else {
                vibrator.cancel();
                if (mp.isPlaying()) {
                    mp.pause();
                }
                isPlayAlert = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFindPhoneDialog() {
        if (findPhone != null) {
            findPhone.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AppManage.getInstance().currentActivity());
        builder.setTitle("查找手机");
        builder.setMessage("设备正在查找手机...");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                iwaerApplication.service.watch.sendCmd(BleCmd.affirmFind(), new BleCallback() {
                    @Override
                    public void onSuccess(Object o) {

                    }

                    @Override
                    public void onFailure(BleException exception) {

                    }
                });
                playAlert(false, alertTime);
                dialog.dismiss();
            }
        });
        findPhone = builder.create();
        findPhone.setCanceledOnTouchOutside(false);
        findPhone.show();
    }

    private void showFindPhoneNotification() {
        if (CheckUtil.isAppBackground(mContext)) {
            if (notification != null) {
                mNotificationManager.cancel(EventGlobal.ACTION_FIND_PHONE_START);
            }
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
            mBuilder.setSmallIcon(R.mipmap.app_icon)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText("设备正在查找手机...")
                    .setTicker("设备正在查找手机...") //通知首次出现在通知栏，带上升动画效果的
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setLights(0xff0000ff, 300, 0);

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClass(this, AppManage.getInstance().currentActivity().getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            notification = mBuilder.build();
            mNotificationManager.notify(EventGlobal.ACTION_FIND_PHONE_START, notification);
        }
    }

    private void showLostNotification(String time) {
        if (CheckUtil.isAppBackground(mContext)) {//判断是否在后台
            if (notification != null) {//判断通知是否存在
                mNotificationManager.cancel(EventGlobal.STATE_DEVICE_DISCONNECT);
            }
            //创建通知
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
            mBuilder.setSmallIcon(R.mipmap.app_icon)
                    .setContentTitle("防丢提醒")
                    .setContentText("检测手环在" + time + "与手机断开连接,可能丢失!")
                    .setTicker("防丢提醒") //通知首次出现在通知栏，带上升动画效果的
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                    .setLights(0xff0000ff, 300, 0);
            //点击意图
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClass(this, AppManage.getInstance().currentActivity().getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            notification = mBuilder.build();
            mNotificationManager.notify(EventGlobal.STATE_DEVICE_DISCONNECT, notification);
        }
    }

    private void showLostAlert(String time) {
        //如果窗口存在先释放掉
        if (lostAlert != null) {
            lostAlert.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AppManage.getInstance().currentActivity());
        builder.setTitle("防丢提醒");
        builder.setMessage("检测手环在" + time + "与手机断开连接,可能丢失!");
        builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playAlert(false, alertTime);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("不再提醒", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SPUtil.put(mContext, AppGlobal.DATA_ALERT_LOST, false);
                playAlert(false, alertTime);
                dialog.dismiss();
            }
        });
        lostAlert = builder.create();
        lostAlert.show();
    }

    AlertDialog bluetoothDialog;
    public void OpenBluetoothDialog(){
        PermissionView contentView = new PermissionView(this);
        List<PermissonItem> data = new ArrayList<>();
        data.add(new PermissonItem("蓝牙","蓝牙",R.mipmap.permission_ic_bluetooth));
        contentView.setGridViewColum(data.size());
        contentView.setTitle("开启蓝牙");
        contentView.setMsg("为了您能正常使用iband，需要打开蓝牙");
        contentView.setGridViewAdapter(new PermissionAdapter(data));
        contentView.setStyleId(R.style.PermissionBlueStyle);
//        contentView.setFilterColor(mFilterColor);
        contentView.setBtnOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothDialog != null && bluetoothDialog.isShowing())
                    bluetoothDialog.dismiss();
                IbandApplication.getIntance().service.watch.BluetoothEnable(AppManage.getInstance().currentActivity());
            }
        });
        if (bluetoothDialog != null && bluetoothDialog.isShowing()) {
            return;
        }
        bluetoothDialog = new AlertDialog.Builder(AppManage.getInstance().currentActivity())
                .setView(contentView)
                .create();
        bluetoothDialog.setCanceledOnTouchOutside(false);
        bluetoothDialog.setCancelable(false);
        bluetoothDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bluetoothDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        moveTaskToBack(true);
    }
}
