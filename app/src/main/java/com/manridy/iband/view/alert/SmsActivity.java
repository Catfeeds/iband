package com.manridy.iband.view.alert;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.manridy.applib.utils.SPUtil;
import com.manridy.iband.common.AppGlobal;
import com.manridy.iband.common.EventGlobal;
import com.manridy.iband.R;
import com.manridy.iband.ui.items.AlertBigItems;
import com.manridy.iband.view.base.BaseActionActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;

/**
 * 短信提醒
 * Created by jarLiao on 17/5/4.
 */

public class SmsActivity extends BaseActionActivity {

    @BindView(R.id.tb_menu)
    TextView tbMenu;
    @BindView(R.id.ai_alert)
    AlertBigItems aiAlert;
    boolean onOff;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sms);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables() {
        setStatusBarColor(Color.parseColor("#2196f3"));
        setTitleAndMenu("短信提醒", "保存");
        onOff = (boolean) SPUtil.get(mContext, AppGlobal.DATA_ALERT_SMS,false);
        aiAlert.setAlertCheck(onOff);
    }

    @Override
    protected void initListener() {
        aiAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PermissonItem> permissonItems = new ArrayList<PermissonItem>();
                permissonItems.add(new PermissonItem(Manifest.permission.RECEIVE_SMS, "短信", R.mipmap.permission_ic_message));
                permissonItems.add(new PermissonItem(Manifest.permission.READ_CONTACTS, "联系人", R.mipmap.permission_ic_contacts));
                HiPermission.create(mContext)
                        .title("开启提醒")
                        .msg("为了您能正常使用短信提醒，需要以下权限")
                        .style(R.style.PermissionBlueStyle)
                        .permissions(permissonItems)
                        .checkMutiPermission(permissionCallback);
            }
        });
        tbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.put(mContext, AppGlobal.DATA_ALERT_SMS,onOff);
                eventSend(EventGlobal.DATA_CHANGE_MENU);
                showToast("保存成功");
                finish();
            }
        });
    }

    PermissionCallback permissionCallback = new PermissionCallback() {
        @Override
        public void onClose() {

        }

        @Override
        public void onFinish() {
            onOff = !onOff;
            aiAlert.setAlertCheck(onOff);
        }

        @Override
        public void onDeny(String permisson, int position) {

        }

        @Override
        public void onGuarantee(String permisson, int position) {

        }
    };

}
