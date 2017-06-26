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
 * 帮助item
 * Created by jarLiao on 17/5/4.
 */

public class HelpItems extends RelativeLayout {
    private TextView menuContent;
    private TextView menuBt;
    public HelpItems(Context context) {
        super(context);
    }

    public HelpItems(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.item_help,this);
        TextView menuName = (TextView) view.findViewById(R.id.tv_menu_name);
        menuContent = (TextView) view.findViewById(R.id.tv_menu_content);
        menuBt = (TextView) view.findViewById(R.id.tv_menu_bt);
        ImageView menuArrows = (ImageView) view.findViewById(R.id.iv_menu_arrows);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.HelpItems);
        int color = typedArray.getColor(R.styleable.HelpItems_help_name_color,-1);
        int contentColor = typedArray.getColor(R.styleable.HelpItems_help_content_color,-1);
        int btColor = typedArray.getColor(R.styleable.HelpItems_help_bt_color,-1);
        String name = typedArray.getString(R.styleable.HelpItems_help_name);
        String content = typedArray.getString(R.styleable.HelpItems_help_content);
        String bt = typedArray.getString(R.styleable.HelpItems_help_bt);
        if (color != -1) {
            menuName.setTextColor(color);
        }
        if (contentColor != -1) {
            menuContent.setTextColor(contentColor);
        }
        if (btColor != -1) {
            menuBt.setTextColor(btColor);
        }
        if (content != null) {
            menuContent.setText(content);
            menuContent.setVisibility(VISIBLE);
        }
        if (bt != null) {
            menuBt.setText(bt);
            menuBt.setVisibility(VISIBLE);
            menuArrows.setVisibility(GONE);
        }
        menuName.setText(name);
        typedArray.recycle();
    }

    public String getMenuContent() {
        return menuContent.getText().toString();
    }

    public void setMenuContent(String content) {
        this.menuContent.setText(content);
    }

    public void setMenuUnit(String unit) {
        this.menuBt.setText(unit);
    }
}
