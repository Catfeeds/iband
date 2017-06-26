package com.manridy.iband.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manridy.iband.R;
import com.manridy.iband.common.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 菜单适配器
 * Created by jarLiao on 17/5/4.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyViewHolder> {
    private List<DeviceModel> deviceList;
    private OnItemClickListener mOnItemClickListener;

    public DeviceAdapter(List<DeviceModel> itemList) {
        this.deviceList = itemList;
    }

    public void setOnItemClickListener(OnItemClickListener OnItemClickListener) {
        this.mOnItemClickListener = OnItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_device, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bindData(deviceList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_device_name)
        TextView tvDeviceName;
        @BindView(R.id.tv_device_mac)
        TextView tvDeviceMac;
        @BindView(R.id.cb_select)
        CheckBox cbSelect;
        @BindView(R.id.rl_device)
        RelativeLayout rlDevice;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(DeviceModel device, final int position) {
            String name = device.leDevice.getName();
            if (name == null || name.isEmpty()) {
                name = "UNKNOW DEVICE";
            }
            tvDeviceName.setText(name);
            tvDeviceMac.setText(device.leDevice.getAddress());
            cbSelect.setVisibility(device.isSelect ? View.VISIBLE : View.GONE);
            rlDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(position);
                    }
                }
            });
        }
    }

    public static class DeviceModel {
        public BluetoothDevice leDevice;
        public int rssi;
        public byte[] scanRecord;
        public boolean isSelect;

        public DeviceModel() {
        }

        public DeviceModel(BluetoothDevice leDevice, int rssi, byte[] scanRecord) {
            this.leDevice = leDevice;
            this.rssi = rssi;
            this.scanRecord = scanRecord;
        }

    }

}
