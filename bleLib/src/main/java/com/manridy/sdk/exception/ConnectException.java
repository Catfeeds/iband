package com.manridy.sdk.exception;

import android.bluetooth.BluetoothGatt;

/**
 * 蓝牙连接异常
 * Created by jarLiao on 2016/10/18.
 */

public class ConnectException extends BleException {
    private BluetoothGatt mBluetoothGatt;
    private int gattStatus;

    public ConnectException( BluetoothGatt mBluetoothGatt, int gattStatus) {
        super(ERROR_CODE_GATT, "Connect Exception Occurred! ");
        this.mBluetoothGatt = mBluetoothGatt;
        this.gattStatus = gattStatus;
    }

    public BluetoothGatt getmBluetoothGatt() {
        return mBluetoothGatt;
    }

    public void setmBluetoothGatt(BluetoothGatt mBluetoothGatt) {
        this.mBluetoothGatt = mBluetoothGatt;
    }

    public int getGattStatus() {
        return gattStatus;
    }

    public void setGattStatus(int gattStatus) {
        this.gattStatus = gattStatus;
    }
}
