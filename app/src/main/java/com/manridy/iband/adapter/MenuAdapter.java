package com.manridy.iband.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.manridy.iband.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 菜单适配器
 * Created by jarLiao on 17/5/4.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {
    private List<Menu> menuList;

    public MenuAdapter(List<Menu> menuList) {
        this.menuList = menuList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bindData(menuList.get(position));
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class Menu {
        int menuIcon;
        String menuName;

        public Menu() {
        }

        public Menu(String menuName, int menuIcon) {
            this.menuName = menuName;
            this.menuIcon = menuIcon;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_menu_icon)
        ImageView ivMenuIcon;
        @BindView(R.id.tv_menu_name)
        TextView tvMenuName;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void  bindData(Menu menu){
            ivMenuIcon.setImageResource(menu.menuIcon);
            tvMenuName.setText(menu.menuName);
        }
    }


}
