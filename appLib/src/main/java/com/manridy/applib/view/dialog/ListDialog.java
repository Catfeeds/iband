package com.manridy.applib.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.manridy.applib.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 列表选择器
 * Created by jarLiao on 2016/7/18.
 */
public class ListDialog extends BaseDialog {
    Context context;
    String[] strings;
    ListDialogListener listDialogListener;
    String title;

    public ListDialog(Context context, String[] strings,String title, ListDialogListener listDialogListener) {
        super(context);
        this.title = title;
        this.context = context;
        this.strings = strings;
        this.listDialogListener = listDialogListener;
    }

    protected ListDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_list_select);
        TextView ok = (TextView) findViewById(R.id.dialog_ok);
        TextView cancel = (TextView) findViewById(R.id.dialog_cancel);
        TextView tvTitle = (TextView) findViewById(R.id.dialog_title);
        ListView listView = (ListView) findViewById(R.id.dialog_list);
        tvTitle.setText(title);
        ok.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        SimpleAdapter adapter = new SimpleAdapter(context, getData(), R.layout.item_list, new String[]{"text"}, new int[]{R.id.item_text});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listDialogListener.onItemClick(ListDialog.this, position);
            }
        });
    }

    private List<Map<String, String>> getData() {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("text", strings[i]);
            mapList.add(map);
        }
        return mapList;
    }

    //设置时间选择监听
    public interface ListDialogListener {
        void onItemClick(ListDialog listDialog, int position);
    }

}
