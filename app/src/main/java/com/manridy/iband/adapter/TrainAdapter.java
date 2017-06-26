package com.manridy.iband.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.manridy.iband.R;
import com.manridy.iband.bean.StepModel;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 训练适配器
 */

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.MyViewHolder> {
    private List<StepModel> list;

    public TrainAdapter(List<StepModel> list) {
        this.list = list;
    }


    public void setItemList(List<StepModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_train, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_icon)
        ImageView itemIcon;
        @BindView(R.id.item_time)
        TextView itemTime;
        @BindView(R.id.item_type)
        TextView itemType;
        @BindView(R.id.item_min)
        TextView itemMin;
        @BindView(R.id.item_step)
        TextView itemStep;
        @BindView(R.id.item_ka)
        TextView itemKa;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(StepModel step) {
            itemIcon.setImageResource(R.mipmap.train_ic02);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String start = simpleDateFormat.format(step.getStepDate());
            String end = simpleDateFormat.format((step.getStepDate().getTime()+step.getStepTime()*60*1000));
            itemTime.setText(start+"~"+end);
            itemType.setText(step.getStepType()==1?"跑步":"慢走");
            itemMin.setText(step.getStepTime()+"分钟");
            itemStep.setText(step.getStepNum()+"步");
            itemKa.setText(step.getStepCalorie()+"千卡");
        }
    }



}
