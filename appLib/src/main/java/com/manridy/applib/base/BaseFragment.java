package com.manridy.applib.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manridy.applib.utils.ToastUtil;

/**
 * fragment基类
 * Created by jarLiao on 2016/10/24.
 */

public abstract class BaseFragment extends Fragment {
    protected String TAG = this.getClass().getSimpleName();
    protected Context mContext;
    protected View root;
    private ProgressDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = initView(inflater,container);
        initVariables();
        initListener();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            saveData();
        }
    }

    protected abstract View initView(LayoutInflater inflater, @Nullable ViewGroup container);

    protected abstract void initVariables();

    protected abstract void initListener();

    protected void initData(Bundle savedInstanceState){}

    protected void saveData(){}


    /**
     * 全局toast
     * @param msg 消息
     */
    protected void showToast(String msg){
        ToastUtil.showToast(mContext,msg);
    }

    /**
     * Snackbar
     * @param view 显示视图
     * @param msg 消息
     */
    protected void showSnackbar(View view, String msg){
        Snackbar.make(view,msg,Snackbar.LENGTH_SHORT).show();
    }

    public void showProgress(String msg) {
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(msg);
        dialog.show();
    }

    public void dismissProgress(){
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
