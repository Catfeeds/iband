package com.manridy.iband.view.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manridy.applib.base.BaseFragment;
import com.manridy.iband.R;

import org.greenrobot.eventbus.EventBus;

/**
 * 基类
 * Created by jarLiao on 2016/10/28.
 */

public abstract class BaseEventFragment extends BaseFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Log.i(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        Log.i(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
    }



    public String getSafetyString(int resId){
        if (isAdded()) {
            return getResources().getString(resId);
        }
        return "";
    }

    public float getSafetyDimension(int resId){
        if (isAdded()) {
            return getResources().getDimension(resId);
        }
        return 10;
    }

    /**
     * 启动Activity
     * @param cls 目标Activity
     */
    protected void startActivity(Class<?> cls){
        startActivity(new Intent(mContext,cls));

    }

//    public abstract void onMainEvent(MessageEvent event);

//    public abstract void onBackgroundEvent(MessageEvent event);

}
