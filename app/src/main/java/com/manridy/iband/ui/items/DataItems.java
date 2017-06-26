package com.manridy.iband.ui.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manridy.iband.R;

import butterknife.BindView;

/**
 * 数据展示表格item
 * Created by jarLiao on 17/5/4.
 */

public class DataItems extends RelativeLayout {

    TextView tvDataHint;
    TextView tvData;
    TextView tvUnit;
    TextView lineTop;
    TextView lineBottom;
    TextView lineLeft;
    TextView lineRight;

    public DataItems(Context context) {
        super(context);
    }

    public DataItems(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.item_data, this);
        tvDataHint = (TextView) view.findViewById(R.id.tv_data_hint);
        tvData = (TextView) view.findViewById(R.id.tv_data);
        tvUnit = (TextView) view.findViewById(R.id.tv_unit);
        lineTop = (TextView) view.findViewById(R.id.line_top);
        lineBottom = (TextView) view.findViewById(R.id.line_bottom);
        lineLeft = (TextView) view.findViewById(R.id.line_left);
        lineRight = (TextView) view.findViewById(R.id.line_right);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DataItems);
        String data = typedArray.getString(R.styleable.DataItems_data);
        String hint = typedArray.getString(R.styleable.DataItems_data_hint);
        String unit = typedArray.getString(R.styleable.DataItems_data_unit);
        int hintColor = typedArray.getColor(R.styleable.DataItems_data_hint_color, -1);
        int dataColor = typedArray.getColor(R.styleable.DataItems_data_color, -1);
        int lineColor = typedArray.getColor(R.styleable.DataItems_line_color, -1);
        int type = typedArray.getInt(R.styleable.DataItems_data_type,0);
        tvDataHint.setText(hint);
        tvData.setText(data);
        if (unit != null) {
            tvUnit.setText(unit);
            tvUnit.setVisibility(VISIBLE);
        }
        if (dataColor != -1) {
            tvUnit.setTextColor(dataColor);
            tvData.setTextColor(dataColor);
        }
        if (hintColor != -1) {
            tvDataHint.setTextColor(hintColor);
        }
        if (lineColor == -1) {
            lineColor = Color.parseColor("#26000000");
        }
        if (type == 0){
            lineLeft.setBackgroundColor(lineColor);
            lineRight.setVisibility(GONE);
        }else if (type == 1){
            lineLeft.setBackgroundColor(lineColor);
            lineRight.setBackgroundColor(lineColor);
        }else if (type == 2){
            lineLeft.setVisibility(GONE);
            lineRight.setBackgroundColor(lineColor);
        }
        lineTop.setBackgroundColor(lineColor);
        lineBottom.setBackgroundColor(lineColor);
        typedArray.recycle();
    }

    public void setItemData(String data){
        tvData.setText(data);
    }

    public void setItemData(String hint,String data){
        tvDataHint.setText(hint);
        tvData.setText(data);
    }

    public void setItemData(String hint,String data,String unit){
        tvDataHint.setText(hint);
        tvData.setText(data);
        tvUnit.setText(unit);
    }


    public void setItemData(String hint,String data,String unit,int lineColor){
        tvDataHint.setText(hint);
        tvData.setText(data);
        tvUnit.setText(unit);
        lineLeft.setBackgroundColor(lineColor);
        lineRight.setBackgroundColor(lineColor);
        lineTop.setBackgroundColor(lineColor);
        lineBottom.setBackgroundColor(lineColor);
    }

}
