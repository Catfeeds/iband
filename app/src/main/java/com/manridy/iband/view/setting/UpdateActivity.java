package com.manridy.iband.view.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.manridy.applib.utils.SPUtil;
import com.manridy.applib.utils.VersionUtil;
import com.manridy.iband.OnResultCallBack;
import com.manridy.iband.R;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.common.DomXmlParse;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.common.EventMessage;
import com.manridy.iband.service.HttpService;
import com.manridy.iband.ui.items.HelpItems;
import com.manridy.iband.view.OtaActivity;
import com.manridy.iband.view.base.BaseActionActivity;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 遥控拍照页面
 * Created by jarLiao on 17/5/4.
 */

public class UpdateActivity extends BaseActionActivity {

    @BindView(R.id.hi_update_soft)
    HelpItems hiUpdateSoft;
    @BindView(R.id.hi_update_firm)
    HelpItems hiUpdateFirm;
    String url = "http://39.108.92.15:12345";
    String version = "/version.xml";
    String firm;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFirmView();
    }

    @Override
    protected void initVariables() {
        registerEventBus();
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleBar("检查升级");
        hiUpdateSoft.setMenuContent("v"+ VersionUtil.getVersionName(mContext));

    }

    private void updateFirmView() {
        firm = (String) SPUtil.get(mContext, AppGlobal.DATA_VERSION_FIRMWARE,"");
        String mac = (String) SPUtil.get(mContext, AppGlobal.DATA_DEVICE_BIND_MAC,"");
        if (!firm.isEmpty() && !mac.isEmpty()) {
            hiUpdateFirm.setMenuContent("v"+firm);
            hiUpdateFirm.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.hi_update_soft, R.id.hi_update_firm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hi_update_soft:
                Beta.checkUpgrade(true,false);
                break;
            case R.id.hi_update_firm:
                getOTAVersion(url+version);
                break;
        }
    }

    private void getOTAVersion(final String versionUrl){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpService.getInstance().downloadXml(versionUrl, new OnResultCallBack() {
                    @Override
                    public void onResult(boolean result, Object o) {
                        if (result) {
                            if (o != null) {
                                List<DomXmlParse.Image> imageList = (List<DomXmlParse.Image>) o;
                                for (DomXmlParse.Image image : imageList) {
                                    if (image.id.equals("0001")) {
                                        if (image.least.compareTo(firm) > 0) {
                                            final String fileUrl = url+"/"+image.id+"/"+image.file;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    show(fileUrl);
                                                }
                                            });
                                        }else {
                                            EventBus.getDefault().post(new EventMessage(EventGlobal.MSG_OTA_TOAST,"您已经是最新版了"));
                                        }
                                    }
                                }
                            }
                        }else{
                            EventBus.getDefault().post(new EventMessage(EventGlobal.MSG_OTA_TOAST,"获取升级信息失败,请重新尝试"));
                        }
                    }
                });
            }
        }).start();
    }

    private void show(final String fileUrl){
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_normal,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        view.findViewById(R.id.goto_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.goto_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOTAFile(fileUrl);
                EventBus.getDefault().post(new EventMessage(EventGlobal.MSG_OTA_TOAST,"开始下载OTA文件"));
                dialog.dismiss();
            }
        });
        dialog.show();
    };

    private void getOTAFile(final String fileUrl){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpService.getInstance().downloadOTAFile(fileUrl, new OnResultCallBack() {
                    @Override
                    public void onResult(boolean result, Object o) {
                        if (result) {
                            EventBus.getDefault().post(new EventMessage(EventGlobal.MSG_OTA_TOAST,"OTA文件下载成功"));
                            startActivity(OtaActivity.class);
                        }else{
                            EventBus.getDefault().post(new EventMessage(EventGlobal.MSG_OTA_TOAST,"OTA文件下载失败"));

                        }
                    }
                });
            }
        }).start();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(EventMessage event){
        if (event.getWhat() == EventGlobal.MSG_OTA_TOAST) {
            showToast(event.getMsg());
        }
    }
}
