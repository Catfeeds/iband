package com.manridy.sdk.scan;

import android.bluetooth.BluetoothDevice;

/**
 * 指定时间扫描指定设备
 * Created by jarLiao on 2016/10/19.
 */

public abstract class TimeMacScanCallback extends TimeScanCallback {
    private String mac;//地址
//    private AtomicBoolean hasFound = new AtomicBoolean(false);//量子变量

    public TimeMacScanCallback(String mac, long timeOutMillis) {
        super(timeOutMillis,null);
        this.mac = mac;
        if (mac == null) {
            throw new IllegalArgumentException("start scan, mac can not be null!");
        }
    }

    @Override
    public void onFilterLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (mac.equalsIgnoreCase(device.getAddress())) {
            onDeviceFound(true,device);
            bluetoothLeManager.stopScan(TimeMacScanCallback.this);
        }
    }

    @Override
    public void onScanEnd() {
        onDeviceFound(false,null);
    }

    public abstract void onDeviceFound(boolean isFound,BluetoothDevice device);
}
