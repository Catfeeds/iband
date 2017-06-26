package com.manridy.sdk.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;

import com.manridy.sdk.BluetoothLeManager;

import java.util.ArrayList;
import java.util.List;


/**
 * 指定时间扫描
 * Created by jarLiao on 2016/10/19.
 */

public abstract class TimeScanCallback implements BluetoothAdapter.LeScanCallback {
    protected Handler handler = new Handler(Looper.getMainLooper());//主线程
    protected long timeOutMillis;//超时时间
    protected String filter;//过滤关键
    protected BluetoothLeManager bluetoothLeManager;//ble管理器
    protected List<BluetoothDevice> deviceList = new ArrayList<>();

    public TimeScanCallback(long timeOutMillis,String filter) {
        this.timeOutMillis = timeOutMillis;
        this.filter = filter;
    }

    public abstract void onScanEnd();

    public abstract void onFilterLeScan(BluetoothDevice device, int rssi, byte[] scanRecord);

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (!deviceList.contains(device)) {//筛选重复
            deviceList.add(device);
            if (filter == null || filter.isEmpty()) {//是否过滤
                onFilterLeScan(device, rssi, scanRecord);
            }else{
                if (device.getName()!= null && device.getName().indexOf(filter) >-1) {//是否包含过滤名称
                    onFilterLeScan(device, rssi, scanRecord);
                }
            }
        }
    }

    public void notifyScanStated(){
        if (timeOutMillis > 0){
            removeHandlerMsg();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothLeManager.stopScan(TimeScanCallback.this);
                    onScanEnd();
                    deviceList.clear();
                }
            },timeOutMillis);
        }
    }

    public void removeHandlerMsg(){
        handler.removeCallbacksAndMessages(null);
    }


    public long getTimeOutMillis() {
        return timeOutMillis;
    }

    public TimeScanCallback setTimeOutMillis(long timeOutMillis) {
        this.timeOutMillis = timeOutMillis;
        return this;
    }

    public BluetoothLeManager getBluetoothLeManager() {
        return bluetoothLeManager;
    }

    public TimeScanCallback setBluetoothLeManager(BluetoothLeManager bluetoothLeManager) {
        this.bluetoothLeManager = bluetoothLeManager;
        return this;
    }
}
