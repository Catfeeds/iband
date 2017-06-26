package com.manridy.applib.base;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 适配器基类
 * Created by jarLiao on 2016/10/14.
 */

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter{
    private List<T> datas;

    public BaseAdapter(List<T> datas) {
        this.datas = datas;
    }

    public void addData(T t){
        if (datas != null) {
            if (!datas.contains(t)) {
                datas.add(t);
                notifyDataSetChanged();
            }
        }
    }

    public void clearData(){
        if (datas != null) {
            datas.clear();
            notifyDataSetChanged();
        }
    }

    public List<T> getData(){
        return datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder<T> holder;
        if (convertView == null) {
            holder = getHolder();
        }else{
            holder = (BaseHolder<T>) convertView.getTag();
        }
        T t = datas.get(position);
        holder.setData(t);

        return holder.getContentView();
    }

    protected abstract BaseHolder<T> getHolder();
}
