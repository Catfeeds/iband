package com.manridy.iband.ui.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manridy.iband.R;
import com.manridy.iband.view.setting.AlertActivity;

/**
 * 提醒表格item
 * Created by jarLiao on 17/5/4.
 */

public class AlertMenuItems extends RelativeLayout {
    TextView alertState;

    public AlertMenuItems(Context context) {
        super(context);
    }

    public AlertMenuItems(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.item_alert_menu,this);
        ImageView alertIcon = (ImageView) view.findViewById(R.id.iv_alert_icon);
        TextView alertName = (TextView) view.findViewById(R.id.tv_alert_name);
        alertState = (TextView) view.findViewById(R.id.tv_alert_state);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.AlertMenuItems);
        int icon = typedArray.getResourceId(R.styleable.AlertMenuItems_alert_icon,-1);
        String name = typedArray.getString(R.styleable.AlertMenuItems_alert_name);
        alertIcon.setImageResource(icon);
        alertName.setText(name);
        typedArray.recycle();
    }

    public void setAlertState(boolean isEnable){
        alertState.setText(isEnable?"已开启":"未开启");
    }
}
