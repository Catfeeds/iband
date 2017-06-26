package com.manridy.iband.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manridy.iband.R;
import com.manridy.iband.bean.ViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 菜单适配器
 * Created by jarLiao on 17/5/4.
 */

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.MyViewHolder> {
    private List<ViewModel> viewList;
    private Context mContext;
    private onItemClickListener mItemClickListener;
    private RecyclerView mRecyclerView;

    private View VIEW_HEADER;
    private View VIEW_FOOTER;

    //Type
    private int TYPE_NORMAL = 1000;
    private int TYPE_HEADER = 1001;
    private int TYPE_FOOTER = 1002;


    public ViewAdapter(Context mContext,List<ViewModel> viewList) {
        this.mContext = mContext.getApplicationContext();
        this.viewList = viewList;
    }



    public interface onItemClickListener{
        void itemClick(int dataPosition,int viewPosition);
    }

    public void setOnItemClickListener(onItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        try {
            if (mRecyclerView == null && mRecyclerView != recyclerView) {
                mRecyclerView = recyclerView;
            }
            ifGridLayoutManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new MyViewHolder(VIEW_FOOTER,TYPE_FOOTER);
        } else if (viewType == TYPE_HEADER) {
            return new MyViewHolder(VIEW_HEADER,TYPE_HEADER);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            return new MyViewHolder(v,TYPE_NORMAL);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (!isHeaderView(position) && !isFooterView(position)) {
            holder.bindData(viewList.get(getItemDataPosition(position)), position);
        }
    }

    @Override
    public int getItemCount() {
        int count = (viewList == null ? 0 : viewList.size());
        if (VIEW_FOOTER != null) {
            count++;
        }

        if (VIEW_HEADER != null) {
            count++;
        }
        return count;
    }

    public int getItemDataPosition(int position){
        if (haveFooterView()) {
            position --;
        }
        if (haveHeaderView()) {
            position --;
        }
        return position;
    }



    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return TYPE_HEADER;
        } else if (isFooterView(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    public void addHeaderView(View headerView) {
        if (haveHeaderView()) {
            throw new IllegalStateException("hearview has already exists!");
        } else {
            //避免出现宽度自适应
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(params);
            VIEW_HEADER = headerView;
            ifGridLayoutManager();
            notifyItemInserted(0);
        }

    }

    public void addFooterView(View footerView) {
        if (haveFooterView()) {
            throw new IllegalStateException("footerView has already exists!");
        } else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            footerView.setLayoutParams(params);
            VIEW_FOOTER = footerView;
            ifGridLayoutManager();
            notifyItemInserted(getItemCount() - 1);
        }
    }

    private boolean haveHeaderView() {
        return VIEW_HEADER != null;
    }

    public boolean haveFooterView() {
        return VIEW_FOOTER != null;
    }

    private boolean isHeaderView(int position) {
        return haveHeaderView() && position == 0;
    }

    private boolean isFooterView(int position) {
        return haveFooterView() && position == getItemCount() - 1;
    }

    private void ifGridLayoutManager() {
        if (mRecyclerView == null) return;
        final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager.SpanSizeLookup originalSpanSizeLookup =
                    ((GridLayoutManager) layoutManager).getSpanSizeLookup();
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeaderView(position) || isFooterView(position)) ?
                            ((GridLayoutManager) layoutManager).getSpanCount() :
                            1;
                }
            });
        }
    }

    public Object getItemData(int position){
        return viewList.get(position);
    }

    public Object getData(){
        return viewList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_view_icon)
        ImageView ivViewIcon;
        @BindView(R.id.tv_view_name)
        TextView tvViewName;
        @BindView(R.id.ll_view)
        LinearLayout llView;

        public MyViewHolder(View itemView,int viewType) {
            super(itemView);
            if (viewType == TYPE_NORMAL) {
                ButterKnife.bind(this,itemView);
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void  bindData(ViewModel viewModel, final int position){
            ivViewIcon.setImageResource(viewModel.getViewIcon());

            tvViewName.setText(viewModel.getViewName());
            Drawable mDrawable = mContext.getResources().getDrawable(viewModel.isSelect() ? R.mipmap.selection_tick_color : R.mipmap.selection_tick );
            mDrawable.setBounds(0, 0, mDrawable.getMinimumWidth(), mDrawable.getMinimumHeight());
            tvViewName.setCompoundDrawables(null, null, mDrawable, null);
            llView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.itemClick(getItemDataPosition(position),position);
                    }
                }
            });
            llView.setEnabled(viewModel.isEnable());
            tvViewName.setEnabled(viewModel.isEnable());
         }
    }


}
