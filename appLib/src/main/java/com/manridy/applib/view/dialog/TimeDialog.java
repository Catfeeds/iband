package com.manridy.applib.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.manridy.applib.R;
import com.manridy.applib.utils.TimeUtil;


/**
 * 时间选择器
 * Created by Administrator on 2016/7/18.
 */
public class TimeDialog extends BaseDialog {
    private Context context;
    private int[] time;
    private TimeDialogListener dialogListener;
    String title;

    public TimeDialog(Context context, int[] time,String title, TimeDialogListener dialogListener ) {
        super(context);
        this.title = title;
        this.time = time;
        this.dialogListener = dialogListener;
        this.context = context;
    }

    public TimeDialog(Context context, int theme) {
        super(context, theme);
    }

    protected TimeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_time_select);
        TextView ok = (TextView) findViewById(R.id.dialog_ok);
        TextView cancel = (TextView) findViewById(R.id.dialog_cancel);
        TextView tvTitle = (TextView) findViewById(R.id.dialog_title);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.tp_time);
        tvTitle.setText(title);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(time[0]);
        timePicker.setCurrentMinute(time[1]);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.getTime(TimeUtil.zero(timePicker.getCurrentHour())
                        ,TimeUtil.zero(timePicker.getCurrentMinute()));
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
    public interface TimeDialogListener {
        void getTime(String hour, String minute);
    }

}
