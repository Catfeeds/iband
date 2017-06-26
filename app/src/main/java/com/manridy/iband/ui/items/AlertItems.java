package com.manridy.iband.ui.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manridy.iband.R;

/**
 * 提醒item
 * Created by jarLiao on 17/5/4.
 */

public class AlertItems extends RelativeLayout {
    ImageView alertImg;
    TextView alertContent;
    public AlertItems(Context context) {
        super(context);
    }

    public AlertItems(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.item_alert,this);
        TextView alertName = (TextView) view.findViewById(R.id.tv_menu_name);
        alertContent = (TextView) view.findViewById(R.id.tv_menu_content);
        alertImg = (ImageView) view.findViewById(R.id.iv_menu_img);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.AlertItems);
        int icon = typedArray.getResourceId(R.styleable.AlertItems_alert_img,-1);
        String name = typedArray.getString(R.styleable.AlertItems_alert_names);
        String text = typedArray.getString(R.styleable.AlertItems_alert_text);
        alertImg.setImageResource(icon);
        alertName.setText(name);
        if (text !=null) {
            alertContent.setText(text);
            alertContent.setVisibility(VISIBLE);
        }
        typedArray.recycle();
    }

    public void setAlertCheck(boolean check) {
        alertImg.setImageResource(check?R.mipmap.ic_on:R.mipmap.ic_off);
    }

    public void setAlertContent(String content) {
        alertContent.setText(content);
    }
}
