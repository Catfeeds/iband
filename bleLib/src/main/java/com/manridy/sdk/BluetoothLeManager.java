package com.manridy.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import com.manridy.sdk.ble.BleParse;
import com.manridy.sdk.callback.BleCallback;
import com.manridy.sdk.callback.BleConnectCallback;
import com.manridy.sdk.common.BitUtil;
import com.manridy.sdk.common.LogUtil;
import com.manridy.sdk.exception.BleException;
import com.manridy.sdk.exception.GattException;
import com.manridy.sdk.exception.InitiatedException;
import com.manridy.sdk.exception.OtherException;
import com.manridy.sdk.exception.TimeOutException;
import com.manridy.sdk.scan.TimeMacScanCallback;
import com.manridy.sdk.scan.TimeScanCallback;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.manridy.sdk.exception.BleException.ERROR_CODE_WRITE;

/**
 * 低功耗蓝牙操作类 BLE
 * Created by Administrator on 2016/10/18.
 */

public class BluetoothLeManager {
    private static final String TAG = BluetoothLeManager.class.getSimpleName();
    private AtomicBoolean isScaning = new AtomicBoolean(false);

    private static final int CONNECT_TIME_OUT = 10000;
    private static final int DISCONNECT_TIME_OUT = 5000;

    public Context mContext;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    public List<BluetoothLeDevice> bluetoothLeDevices = new ArrayList<>();
    private mBluetoothGattCallback mBluetoothGattCallback;
    private BleConnectCallback connectCallback;

    private UUID service = UUID.fromString("f000efe0-0451-4000-0000-00000000b000");
    private UUID notify = UUID.fromString("f000efe3-0451-4000-0000-00000000b000");
    private UUID write = UUID.fromString("f000efe1-0451-4000-0000-00000000b000");

    public static final String ACTION_GATT_CONNECT = "ACTION_GATT_CONNECT";//蓝牙连接
    public static final String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";//蓝牙断开
    public static final String ACTION_GATT_RECONNECT = "ACTION_GATT_RECONNECT";//蓝牙断开
    public static final String ACTION_SERVICES_DISCOVERED = "ACTION_SERVICES_DISCOVERED";//蓝牙服务发现
    public static final String ACTION_NOTIFICATION_ENABLE ="ACTION_NOTIFICATION_ENABLE";//蓝牙通知开启
    public static final String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";//收到蓝牙数据

    public BluetoothLeManager(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    /**
     * 蓝牙BLE支持状态
     * @return true 支持
     */
    public boolean BluetoothLeSupport(){
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 蓝牙开启状态
     * @return true 开启
     */
    public boolean isBluetoothEnable(){
        if (null == mBluetoothAdapter || !mBluetoothAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

    /**
     * 开启蓝牙
     */
    public void BluetoothEnable(Context context){
        if (!isBluetoothEnable()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableIntent);
        }
    }

     /*********BLE搜索********/
    /**
     * 周期搜索附近的ble设备
     */
    public synchronized boolean startScan(TimeScanCallback callback){
        callback.setBluetoothLeManager(this).notifyScanStated();//开始倒计时暂停
        boolean suc = mBluetoothAdapter.startLeScan(callback);
        if (suc){
            isScaning.set(true);
        }else {
            callback.removeHandlerMsg();
        }
        return suc;
    }

    /**
     * 暂停搜索
     */
    public synchronized void stopScan(BluetoothAdapter.LeScanCallback callback){
        if (callback instanceof TimeScanCallback) {
            ((TimeScanCallback) callback).removeHandlerMsg();
        }
        mBluetoothAdapter.stopLeScan(callback);
        isScaning.set(false);
    }

    /**
     * 查找指定设备
     */
    public boolean findDevice(TimeMacScanCallback callback){
        return startScan(callback);
    }

    /**
     * 扫描状态
     */
    public boolean isScaning() {
        return isScaning.get();
    }

    /********BLE连接********/

    public void connect(String mac,boolean isReConnect,BleConnectCallback bleCallback){
        connect(getDevice(mac),isReConnect,bleCallback);
    }

    /**
     * 连接BLE设备
     * @param device 扫描返回设备
     * @param isReConnect 意外断开是否重连
     */
    public synchronized void connect(final BluetoothDevice device, final boolean isReConnect,BleConnectCallback connectCallback){
        this.connectCallback = connectCallback;
        if (null == device || mBluetoothAdapter == null) {
            LogUtil.e(TAG, "connect device or bluetoothAdapter is null" );
        }
        mBluetoothGattCallback = new mBluetoothGattCallback();
        int index = -1;
        for (int i = 0; i < bluetoothLeDevices.size(); i++) {
            if (bluetoothLeDevices.get(i).getmBluetoothGatt().getDevice().getAddress().equals(device.getAddress())) {
                index = i;
            }
        }
        if (index != -1) {
            BluetoothGatt getmBluetoothGatt = bluetoothLeDevices.get(index).getmBluetoothGatt();
            closeBluetoothGatt(getmBluetoothGatt);
            removeBluetoothLe(getmBluetoothGatt);
            LogUtil.e(TAG, "connect bluetoothGatt remove index is"+index);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BluetoothGatt gatt = device.connectGatt(mContext,false,mBluetoothGattCallback);
                    bluetoothLeDevices.add(new BluetoothLeDevice(gatt,isReConnect));
                }
            },1000);
        }else{
            BluetoothGatt gatt = device.connectGatt(mContext,false,mBluetoothGattCallback);
            bluetoothLeDevices.add(new BluetoothLeDevice(gatt,isReConnect));
        }
        handler.postDelayed(connectTimeoutRunnable,CONNECT_TIME_OUT);
    }
    Runnable connectTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (connectCallback != null) {
                connectCallback.onConnectFailure(new TimeOutException());
                connectCallback = null;
            }
        }
    };
    /**
     * 断开Ble设备连接
     * @param leDevice 蓝牙中央
     */
    BleCallback disConnectCallback;
    public synchronized void disconnect(String mac,BleCallback disConnectCallback){
        BluetoothLeDevice leDevice = getBluetoothLeDevice(mac);
        if (leDevice == null) {
            disConnectCallback.onFailure(new OtherException("disconnect leDevice is null!"));
            return;
        }
        BluetoothGatt gatt = leDevice.getmBluetoothGatt();
        if (gatt == null) {
            disConnectCallback.onFailure(new OtherException("disconnect gatt is null!"));
            return;
        }
        this.disConnectCallback = disConnectCallback;
        handler.postDelayed(disConnectTimeoutRunnable,DISCONNECT_TIME_OUT);
        gatt.disconnect();
    }

    Runnable disConnectTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (disConnectCallback != null) {
                disConnectCallback.onFailure(new TimeOutException());
                disConnectCallback = null;
            }
        }
    };

    /**
     * 重连设备
     * @param leDevice 设备
     */
    private synchronized void reConnect(BluetoothLeDevice leDevice){
        if (leDevice == null) return;
            BluetoothGatt gatt = leDevice.getmBluetoothGatt();
        if (gatt == null) return;
            leDevice.getmBluetoothGatt().connect();
            broadcastUpdate(ACTION_GATT_RECONNECT,null,gatt.getDevice().getAddress());
            LogUtil.e(TAG, "reConnect: device is" + leDevice.getmBluetoothGatt().getDevice().getAddress());
    }

    /**
     * 设备连接状态
     * @param leDevice 设备
     * @return
     */
    public boolean isConnect(BluetoothLeDevice leDevice){
        if (leDevice != null) {
            return leDevice.IsConnect();
        }
        throw new IllegalArgumentException("no find gatt");
    }

    public BluetoothDevice getDevice(String mac){
        return mBluetoothAdapter.getRemoteDevice(mac);
    }

    /********Notification and WriteData********/
    /**
     * 订阅蓝牙消息通知
     * @param gatt 蓝牙中央
     * @param service 蓝牙服务id
     * @param characteristic 蓝牙特征id
     * @return
     */
    private synchronized boolean enableNotification(BluetoothGatt gatt,UUID service,UUID characteristic){
        if (gatt == null) {
            LogUtil.e(TAG, "enableNotification BluetoothGatt is null" );
            return false;
        }
        BluetoothGattService gattServer = gatt.getService(service);
        if (gattServer == null) {
            LogUtil.e(TAG, "enableNotification BluetoothGattService is null");
            return false;
        }
        BluetoothGattCharacteristic gattCharacteristic = gattServer.getCharacteristic(characteristic);
        if (gattCharacteristic == null) {
            LogUtil.e(TAG, "enableNotification BluetoothGattCharacteristic is null");
            return false;
        }
        boolean status = gatt.setCharacteristicNotification(gattCharacteristic,true);
        if (status) {
            List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
            for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
                gattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(gattDescriptor);
            }
            broadcastUpdate(ACTION_NOTIFICATION_ENABLE,null,gatt.getDevice().getAddress());
            handler.removeCallbacks(connectTimeoutRunnable);
            if (connectCallback != null) {
                connectCallback.onConnectSuccess();
                connectCallback = null;
            }
        }else{
            if (connectCallback != null) {
                connectCallback.onConnectFailure(new GattException(3));
                connectCallback = null;
            }
            LogUtil.e(TAG, "enableNotification status is false");
            return false;
        }
        return true;
    }

    /**
     * 写入特征值数据（默认特征值）
     * @param gatt 蓝牙中央
     * @param value 值
     * @return
     */
    protected boolean writeCharacteristic(BluetoothGatt gatt, byte[] value, BleCallback bleCallback){
        return writeCharacteristic(gatt,service,write,value,bleCallback);
    }

    /**
     * 写入特征值数据
      * @param gatt 蓝牙中央
     * @param service 蓝牙服务id
     * @param characteristic 蓝牙特征值id
     * @param value 值
     * @return
     */
    BleCallback bleCallback;
    public synchronized boolean writeCharacteristic(BluetoothGatt gatt,UUID service,UUID characteristic,byte[] value, BleCallback bleCallback){
        if (gatt == null) {
            LogUtil.e(TAG, "writeCharacteristic BluetoothGatt is null" );
            bleCallback.onFailure(new OtherException("writeCharacteristic BluetoothGatt is null"));
            return false;
        }
        BluetoothGattService gattServer = gatt.getService(service);
        if (gattServer == null) {
            LogUtil.e(TAG, "writeCharacteristic BluetoothGattService is null");
            bleCallback.onFailure(new OtherException("writeCharacteristic BluetoothGattService is null"));
            return false;
        }
        BluetoothGattCharacteristic gattCharacteristic = gattServer.getCharacteristic(characteristic);
        if (gattCharacteristic == null) {
            LogUtil.e(TAG, "writeCharacteristic BluetoothGattCharacteristic is null");
            bleCallback.onFailure(new OtherException("writeCharacteristic BluetoothGattCharacteristic is null"));
            return false;
        }
        if (value.length == 0 || value.length>20) {
            LogUtil.e(TAG, "writeCharacteristic value count is error ");
            bleCallback.onFailure(new OtherException("writeCharacteristic value count is error "));
            return false;
        }
        gattCharacteristic.setValue(value);
        boolean status = gatt.writeCharacteristic(gattCharacteristic);
        if (!status) {
            LogUtil.e(TAG, "writeCharacteristic status is false");
            bleCallback.onFailure(new BleException(ERROR_CODE_WRITE,"writeCharacteristic is false"));
        }else{
            this.bleCallback = bleCallback;
        }
        return status;
    }

    /********BluetoothGatt********/
    /**
     * 蓝牙BLE回调
     */
     class mBluetoothGattCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            try{
                super.onConnectionStateChange(gatt, status, newState);
                LogUtil.e(TAG, "onConnectionStateChange: connected device is "+gatt.getDevice().getAddress()+",status is "+status );
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    BluetoothLeDevice bluetoothLeDevice = getBluetoothLeDevice(gatt);
                    if (bluetoothLeDevice != null){
                        bluetoothLeDevice.setIsConnect(true);
                    }
                    gatt.discoverServices();
                    broadcastUpdate(ACTION_GATT_CONNECT,null,gatt.getDevice().getAddress());
                }else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                    BluetoothLeDevice bluetoothLeDevice = getBluetoothLeDevice(gatt);
                    if (bluetoothLeDevice != null){
                        bluetoothLeDevice.setIsConnect(false);
                        if (disConnectCallback != null) {
                            handler.removeCallbacks(disConnectTimeoutRunnable);
                            gatt.close();
                            removeBluetoothLe(gatt);
                            refreshDeviceCache(gatt);
                            disConnectCallback.onSuccess(null);
                            disConnectCallback = null;
                            mBluetoothGattCallback = null;
                            broadcastUpdate(ACTION_GATT_DISCONNECTED,null,gatt.getDevice().getAddress());
                        }else{
                            if (bluetoothLeDevice.isReConnect()) {
                                reConnect(bluetoothLeDevice);
                            }
                        }
                    }
                    LogUtil.e(TAG, "onConnectionStateChange: disconnected device is"+gatt.getDevice().getAddress());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                enableNotification(gatt,service,notify);
                broadcastUpdate(ACTION_SERVICES_DISCOVERED,null,gatt.getDevice().getAddress());
            }else{
                if (connectCallback != null) {
                    connectCallback.onConnectFailure(new GattException(status));
                    connectCallback = null;
                }
                LogUtil.e(TAG, "onServicesDiscovered: error code is"+status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
//            LogUtil.e(TAG, "onCharacteristicWrite: gatt is"+ gatt.getDevice().getAddress() );
            LogUtil.e(TAG,"写入数据; "+ BitUtil.parseByte2HexStr(characteristic.getValue()));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
//            LogUtil.e(TAG, "onCharacteristicChanged: gatt is " +gatt.getDevice().getAddress() );
            LogUtil.e(TAG,"返回数据; "+ BitUtil.parseByte2HexStr(characteristic.getValue()));
            BleParse.getInstance().setBleParseData(characteristic.getValue(),bleCallback);
            bleCallback = null;
            broadcastUpdate(ACTION_DATA_AVAILABLE,characteristic.getValue(),gatt.getDevice().getAddress());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    /********bluetoothLeManager********/
    /**
     * 删除蓝牙BLE设备
     * @param gatt
     */
    private void removeBluetoothLe(BluetoothGatt gatt){
        if (gatt != null) {
            for (BluetoothLeDevice mBluetoothLeGatt : bluetoothLeDevices) {
                if (mBluetoothLeGatt.getmBluetoothGatt().getDevice().getAddress().equals(gatt.getDevice().getAddress())){
                    bluetoothLeDevices.remove(mBluetoothLeGatt);
                    LogUtil.e(TAG, "removeBluetoothLeGatt: true"+"size is "+bluetoothLeDevices.size());
                    return;
                }
            }
        }
    }

    /**
     * 得到蓝牙BLE设备
     * @param gatt
     * @return
     */
    private BluetoothLeDevice getBluetoothLeDevice(BluetoothGatt gatt){
        if (gatt != null) {
            for (BluetoothLeDevice mBluetoothLeGatt : bluetoothLeDevices) {
                if (mBluetoothLeGatt.getmBluetoothGatt().getDevice().getAddress().equals(gatt.getDevice().getAddress())){
                    return mBluetoothLeGatt;
                }
            }
        }
        return null;
    }

    /**
     * 得到蓝牙BLE设备
     * @param mac
     * @return
     */
    public BluetoothLeDevice getBluetoothLeDevice(String mac){
        if (mac != null) {
            for (BluetoothLeDevice mBluetoothLeGatt : bluetoothLeDevices) {
                if (mBluetoothLeGatt.getmBluetoothGatt().getDevice().getAddress().equals(mac)){
                    return mBluetoothLeGatt;
                }
            }
        }
        return null;
    }

    /********Other********/
    /**
     * 刷新蓝牙设备缓存
     * @param gatt
     * @return
     */
    public boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null) {
                final boolean success = (Boolean) refresh.invoke(gatt);
                LogUtil.e(TAG, "Refreshing result: " + success);
                return success;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "An exception occured while refreshing device"+e);
        }
        return false;
    }

    /**
     * 关闭蓝牙中央
     * @param gatt
     */
    public void closeBluetoothGatt(BluetoothGatt gatt){
        if (gatt != null) {
            refreshDeviceCache(gatt);
            gatt.close();
        }
    }

    /**
     * 关闭所有蓝牙BLE设备
     */
    public void closeALLBluetoothLe(){
        for (BluetoothLeDevice mBluetoothLeGatt : bluetoothLeDevices) {
            closeBluetoothGatt(mBluetoothLeGatt.getmBluetoothGatt());
        }
        bluetoothLeDevices.clear();
    }

    public void clearBluetoothLe(){
        bluetoothLeDevices.clear();
    }

    /*******广播*******/
    private void broadcastUpdate(String action,byte[] data,String mac) {
        final Intent intent = new Intent(action);
        if (data != null){
            intent.putExtra("BLUETOOTH_DATA",data);
        }
        if (mac != null){
            intent.putExtra("BLUETOOTH_MAC",mac);
        }
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
