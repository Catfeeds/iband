package com.manridy.applib.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.manridy.applib.R;


/**
 * 文字选择器
 * Created by Administrator on 2016/7/18.
 */
public class NumDialog extends BaseDialog {

    private int num;
    private String[] strings;
    private NumDialogListener dialogListener;
    String title;

    public NumDialog(Context context, String[] data, String target,String title, NumDialogListener dialogListener ) {
        super(context);
        for (int i = 0; i < data.length; i++) {
            if (data[i].equals(target)) {
                num = i;
                break;
            }
        }
        this.title = title;
        this.strings = data;
        this.dialogListener = dialogListener;
    }

    public NumDialog(Context context, int theme) {
        super(context, theme);
    }

    protected NumDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_num_select);
        TextView ok = (TextView) findViewById(R.id.dialog_ok);
        TextView cancel = (TextView) findViewById(R.id.dialog_cancel);
        TextView tvTitle = (TextView) findViewById(R.id.dialog_title);
        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.np_target);
        tvTitle.setText(title);
        numberPicker.setDisplayedValues(strings);
        numberPicker.setMaxValue(strings.length-1);
        numberPicker.setMinValue(0);
        numberPicker.setValue(num);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.getNum(strings[numberPicker.getValue()]);
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
    public interface NumDialogListener {
        void getNum(String num);
    }

}
