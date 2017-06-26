package com.manridy.sdk.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * 指定设备扫描
 * Created by jarLiao on 2016/10/18.
 */

public abstract class MacScanCallback implements BluetoothAdapter.LeScanCallback {
    private String mac;

    public MacScanCallback(String mac) {
        this.mac = mac;
        if (mac == null) {
            throw new IllegalArgumentException("start scan,mac can not be null!");
        }
    }

    public abstract void onMacScaned(BluetoothDevice device, int rssi, byte[] scanRecord);

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (mac.equalsIgnoreCase(device.getAddress())) {
            onMacScaned(device, rssi, scanRecord);
        }
    }


}
