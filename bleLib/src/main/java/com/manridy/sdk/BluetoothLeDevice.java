package com.manridy.sdk;

import android.bluetooth.BluetoothGatt;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 蓝牙Ble设备
 * Created by jarLiao on 2016/10/20.
 */

public class BluetoothLeDevice {
    private BluetoothGatt mBluetoothGatt;//蓝牙中央
    private AtomicBoolean isConnect;//连接状态
    private boolean isReConnect ;//是否重连

    public BluetoothGatt getmBluetoothGatt() {
        return mBluetoothGatt;
    }

    public void setmBluetoothGatt(BluetoothGatt mBluetoothGatt) {
        this.mBluetoothGatt = mBluetoothGatt;
    }

    public boolean IsConnect() {
        return isConnect.get();
    }

    public void setIsConnect(boolean isConnect) {
        this.isConnect.set(isConnect);
    }

    public BluetoothLeDevice() {
    }

    public boolean isReConnect() {
        return isReConnect;
    }

    public void setReConnect(boolean reConnect) {
        isReConnect = reConnect;
    }

    public BluetoothLeDevice(BluetoothGatt mBluetoothGatt, boolean isReConnect) {
        this.isConnect = new AtomicBoolean(false);
        this.mBluetoothGatt = mBluetoothGatt;
        this.isReConnect = isReConnect;
    }
}
