package com.manridy.iband.view.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manridy.iband.IbandDB;
import com.manridy.iband.bean.ViewModel;
import com.manridy.iband.view.base.BaseActionActivity;
import com.manridy.iband.R;
import com.manridy.iband.adapter.ViewAdapter;
import com.manridy.sdk.ble.BleCmd;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.exception.BleException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 界面选择页面
 * Created by jarLiao on 17/5/4.
 */

public class ViewActivity extends BaseActionActivity {

    @BindView(R.id.rv_view)
    RecyclerView rvView;
    @BindView(R.id.tb_menu)
    TextView tbMenu;

    ViewAdapter viewAdapter;
    List<ViewModel> viewList = new ArrayList<>();

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);
        setTitleAndMenu("界面选择","保存");
        setStatusBarColor(Color.parseColor("#2196f3"));
    }

    @Override
    protected void initVariables() {
        initRecyclerView();

    }

    private void initRecyclerView() {
        viewList = IbandDB.getInstance().getView();
        if (viewList == null || viewList.size() == 0) {
            viewList = new ArrayList<>();
            viewList.add(new ViewModel(0,"待机", R.mipmap.selection_standby,true,false));
            viewList.add(new ViewModel(1,"计步", R.mipmap.selection_step,true));
            viewList.add(new ViewModel(2,"运动", R.mipmap.selection_sport,true));
            viewList.add(new ViewModel(3,"心率", R.mipmap.selection_heartrate,true));
            viewList.add(new ViewModel(4,"睡眠", R.mipmap.selection_sleep,true));
            viewList.add(new ViewModel(9,"闹钟", R.mipmap.selection_alarmclock,true));
            viewList.add(new ViewModel(7,"查找", R.mipmap.selection_find,true));
            viewList.add(new ViewModel(6,"信息", R.mipmap.selection_about,true));
            viewList.add(new ViewModel(5,"关机", R.mipmap.selection_turnoff,true));
        }else {
            Map<Integer,ViewModel> map = getMap();
            for (ViewModel viewModel : viewList) {
                int viewId = viewModel.getViewId();
                if (map.containsKey(viewId)) {
                    viewModel.setViewIcon(map.get(viewId).getViewIcon());
                }
            }
        }
        viewAdapter = new ViewAdapter(mContext,viewList);
        rvView.setLayoutManager(new GridLayoutManager(mContext,3));
        ((SimpleItemAnimator)rvView.getItemAnimator()).setSupportsChangeAnimations(false);//去掉默认动画解决瞬闪问题
        rvView.setAdapter(viewAdapter);
        viewAdapter.onAttachedToRecyclerView(rvView);//依附RecyclerView添加grid头部
        viewAdapter.addHeaderView(LayoutInflater.from(mContext).inflate(R.layout.hander_view,null));
    }

    @Override
    protected void initListener() {
        viewAdapter.setOnItemClickListener(new ViewAdapter.onItemClickListener() {
            @Override
            public void itemClick(int dataPosition,int position) {
                ViewModel viewModel = (ViewModel) viewAdapter.getItemData(dataPosition);
                viewModel.setSelect(!viewModel.isSelect());
                viewAdapter.notifyItemChanged(position);
            }
        });

        tbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showProgress("保存中...");
                int size = viewList.size();
                int[] onOffs = new int[size];
                int[] ids = new int[size];
                for (int i = 0; i < viewList.size(); i++) {
                    ids[i] = viewList.get(i).getViewId();
                    onOffs[i] = viewList.get(i).isSelect()? 1:0;
                }
                mIwaerApplication.service.watch.sendCmd(BleCmd.getWindowsSet(ids, onOffs), new BleCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        for (ViewModel viewModel : viewList) {
                            viewModel.save();
                        }
                        dismissProgress();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("保存成功");
                            }
                        });
                        finish();
                    }

                    @Override
                    public void onFailure(BleException exception) {
                        dismissProgress();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("保存失败");
                            }
                        });
                    }
                });
            }
        });
    }

    private static Map<Integer,ViewModel> getMap(){
        Map<Integer,ViewModel> map = new HashMap<>();
        map.put(0,new ViewModel(0,"待机", R.mipmap.selection_standby,true,false));
        map.put(1,new ViewModel(1,"计步", R.mipmap.selection_step,true));
        map.put(2,new ViewModel(2,"运动", R.mipmap.selection_sport,true));
        map.put(3,new ViewModel(3,"心率", R.mipmap.selection_heartrate,true));
        map.put(4,new ViewModel(4,"睡眠", R.mipmap.selection_sleep,true));
        map.put(5,new ViewModel(5,"关机", R.mipmap.selection_turnoff,true));
        map.put(6,new ViewModel(6,"信息", R.mipmap.selection_about,true));
        map.put(7,new ViewModel(7,"查找", R.mipmap.selection_find,true));
        map.put(8,new ViewModel(8,"血压", R.mipmap.selection_standby,true));
        map.put(9,new ViewModel(9,"闹钟", R.mipmap.selection_alarmclock,true));
        map.put(10,new ViewModel(10,"MAC二维码", R.mipmap.selection_standby,true));
        map.put(11,new ViewModel(10,"下载二维码", R.mipmap.selection_standby,true));
        map.put(12,new ViewModel(11,"自定义二维码", R.mipmap.selection_standby,true));
        map.put(13,new ViewModel(12,"跑步", R.mipmap.selection_standby,true));
        map.put(14,new ViewModel(13,"登山", R.mipmap.selection_standby,true));
        map.put(15,new ViewModel(14,"羽毛球", R.mipmap.selection_standby,true));
        map.put(16,new ViewModel(15,"篮球", R.mipmap.selection_standby,true));
        map.put(17,new ViewModel(16,"乒乓球", R.mipmap.selection_standby,true));
        return map;
    }

    public static List<ViewModel> getMenuData(int[] ids) {
        List<ViewModel> viewList = new ArrayList<>();
        Map<Integer,ViewModel> map = getMap();
        for (int i = 0; i < ids.length; i++) {
            viewList.add(map.get(ids[i]));
        }
        return viewList;
    }


    public void syncViewData(){
        mIwaerApplication.service.watch.sendCmd(BleCmd.getWindowsChild(128), new BleCallback() {
            @Override
            public void onSuccess(Object o) {
                Type type = new TypeToken<ArrayList<com.manridy.sdk.bean.View>>() {}.getType();
                List<com.manridy.sdk.bean.View> views = new Gson().fromJson(o.toString(),type);
                Map<Integer,ViewModel> map = getMap();
                viewList.clear();
                for (com.manridy.sdk.bean.View view : views) {
                    ViewModel viewModel = map.get(view.getId());
                    viewModel.setSelect(view.isSelect());
                    viewList.add(viewModel);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(BleException exception) {

            }
        });
    }

}
