package com.manridy.applib.view.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.manridy.applib.R;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * 日期选择器
 * Created by Administrator on 2016/7/18.
 */
public class DateDialog extends BaseDialog {
    int[] time;
    DateDialogListener dialogListener;
    String title;

    public DateDialog(Context context, int[] time, String title,DateDialogListener dialogListener ) {
        super(context);
        this.title = title;
        this.time = time;
        this.dialogListener = dialogListener;
    }

    public DateDialog(Context context, int theme) {
        super(context, theme);
    }

    protected DateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_date_select);
        TextView ok = (TextView) findViewById(R.id.dialog_ok);
        TextView cancel = (TextView) findViewById(R.id.dialog_cancel);
        TextView tvTitle = (TextView) findViewById(R.id.dialog_title);
        final DatePicker datePicker = (DatePicker) findViewById(R.id.dp_date);
        int day = 1;
        if (time.length == 2) {
            hideDay(datePicker);
        }else {
            day = time[2];
        }
        tvTitle.setText(title);
        datePicker.setMaxDate(new Date().getTime());//设置最大时间选择不超过当前日期
        datePicker.init(time[0], time[1], day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                dialogListener.getTime(year,monthOfYear,dayOfMonth);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.getTime(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    //设置时间选择监听
    public interface DateDialogListener{
        void getTime(int year, int monthOfYear, int dayOfMonth);
    }

    private void hideDay(DatePicker mDatePicker) {
        try {
            /* 处理android5.0以上的特殊情况 */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                if (daySpinnerId != 0) {
                    View daySpinner = mDatePicker.findViewById(daySpinnerId);
                    if (daySpinner != null) {
                        daySpinner.setVisibility(View.GONE);
                    }
                }
            } else {
                Field[] datePickerfFields = mDatePicker.getClass().getDeclaredFields();
                for (Field datePickerField : datePickerfFields) {
                    if ("mDaySpinner".equals(datePickerField.getName()) || ("mDayPicker").equals(datePickerField.getName())) {
                        datePickerField.setAccessible(true);
                        Object dayPicker = new Object();
                        try {
                            dayPicker = datePickerField.get(mDatePicker);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        ((View) dayPicker).setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
