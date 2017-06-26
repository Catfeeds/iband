package com.manridy.iband.ui.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manridy.iband.R;

/**
 * 单位选择item
 * Created by jarLiao on 17/5/4.
 */

public class UnitItems extends RelativeLayout {
    ImageView menuIcon;
    TextView menuName;

    public UnitItems(Context context) {
        super(context);
    }

    public UnitItems(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.item_unit,this);
        menuIcon = (ImageView) view.findViewById(R.id.iv_unit_img);
        menuName = (TextView) view.findViewById(R.id.tv_unit_text);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.UnitItems);
        String name = typedArray.getString(R.styleable.UnitItems_unit_text);
        boolean isSelect = typedArray.getBoolean(R.styleable.UnitItems_unit_select,false);
        menuName.setText(name);
        selectView(isSelect);

        typedArray.recycle();
    }


    public void selectView(boolean isSelect){
        if (isSelect) {
            menuName.setTextColor(Color.parseColor("#de0196f3"));
            menuIcon.setImageResource(R.mipmap.ic_radiobuttonon_color);
        }else {
            menuName.setTextColor(Color.parseColor("#de000000"));
            menuIcon.setImageResource(R.mipmap.ic_radiobuttonoff);
        }
    }


}
