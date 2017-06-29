package com.manridy.iband.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manridy.iband.R;
import com.manridy.iband.common.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 应用提醒适配器
 * Created by jarLiao on 17/5/4.
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.MyViewHolder> {
    private List<Menu> menuList;
    private OnItemClickListener mOnItemClickListener;
    private boolean isEnable = true;

    public void setOnItemClickListener(OnItemClickListener OnItemClickListener) {
        this.mOnItemClickListener = OnItemClickListener;
    }

    public AppAdapter(List<Menu> menuList) {
        this.menuList = menuList;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bindData(menuList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class Menu {
        public int menuId;
        public String menuName;
        int menuIcon;
        public boolean menuCheck;

        public Menu() {
        }

        public Menu(int menuId, String menuName, int menuIcon) {
            this.menuId = menuId;
            this.menuName = menuName;
            this.menuIcon = menuIcon;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_menu_icon)
        ImageView ivMenuIcon;
        @BindView(R.id.tv_menu_name)
        TextView tvMenuName;
        @BindView(R.id.iv_menu_check)
        ImageView ivMenuCheck;
        @BindView(R.id.rl_menu_view)
        RelativeLayout rlMenuView;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void  bindData(Menu menu, final int position){
            ivMenuIcon.setImageResource(menu.menuIcon);
            tvMenuName.setText(menu.menuName);
            ivMenuCheck.setImageResource(menu.menuCheck? R.mipmap.appremind_ic_select : R.mipmap.appremind_ic_normal);
            rlMenuView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(position);
                    }
                }
            });
        }
    }


}
