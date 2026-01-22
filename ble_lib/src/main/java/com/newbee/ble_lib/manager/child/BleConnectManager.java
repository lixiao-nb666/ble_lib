package com.newbee.ble_lib.manager.child;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.newbee.ble_lib.R;


import com.newbee.ble_lib.util.BleCheckUtil;
import com.newbee.ble_lib.util.BleConnectStatuUtil;
import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.util.BleByteUtil;

import java.util.List;


@SuppressLint("MissingPermission")
public class BleConnectManager {
    private static BleConnectManager bleManager;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothLeScanner bluetoothLeScanner;


    private BleConnectManager(){}

    public static BleConnectManager getInstance(){
        if(null==bleManager){
            synchronized (BleConnectManager.class){
                if(null==bleManager){
                    bleManager=new BleConnectManager();
                }
            }
        }
        return bleManager;
    }


    public void clear(){
        countCannotScandataNumb=0;
    }

    public void close(){
        if(null!=context){
            context=null;
        }
        if(null!=bluetoothLeScanner){
            bluetoothAdapter=null;
        }

        if(null!=bluetoothAdapter){
            bluetoothAdapter=null;
        }
        if(null!=bluetoothManager){
            bluetoothManager=null;
        }
        BlueToothGattManager.getInstance().close();
        bluetoothManager=null;
        bleManager=null;
    }



    public boolean checkCanScan(){
        if(null==bluetoothManager){
            return false;
        }
        if(null==bluetoothAdapter){
            return false;
        }
        if(null==bluetoothLeScanner){
            return false;
        }
        return bleIsOpen();
    }

    private Context context;
    public void havePermissionInitBle(Context context, PackageManager packageManager){
        countCannotScandataNumb=0;
        if(!BleCheckUtil.checkPhoneCanUseBle(packageManager)){
//            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.BLE_CAN_NOT_USE);
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,com.nrmyw.ble_event_lib.R.string.ble_statu_can_not_use);
            return;
        }

        bluetoothManager= (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if(null==bluetoothManager){
//            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.BLE_MANAGER_CAN_NOT_USE);
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,com.nrmyw.ble_event_lib.R.string.ble_statu_manager_can_not_use);
            return;
        }
        bluetoothAdapter=bluetoothManager.getAdapter();
        if(null==bluetoothAdapter){
//            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.BLE_ADAPTER_CAN_NOT_USE);
            Log.i("kankanadaptercannotuse","kankanadaptercannotuse---2");
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,com.nrmyw.ble_event_lib.R.string.ble_statu_adapter_can_not_use);
            return;
        }
        bluetoothLeScanner =bluetoothAdapter.getBluetoothLeScanner();
        if(null==bluetoothLeScanner){
            Log.i("kankanadaptercannotuse","kankanadaptercannotuse---3");
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,com.nrmyw.ble_event_lib.R.string.ble_statu_adapter_can_not_use);
            return;
        }
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.INIT);
        this.context=context;
        Log.i("ble_is_open","ble_is_open_11111");
        if(bleIsOpen()){
            Log.i("ble_is_open","ble_is_open_11111:open");
            startSearchBLE();
        }else{
            Log.i("ble_is_open","ble_is_open_11111:close");
            openBluetooth();
        }
    }

    /** 蓝牙是否打开 **/
    public boolean bleIsOpen(){
        if(null==bluetoothAdapter){
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }

    /** 打开蓝牙 **/

    public void openBluetooth(){
        if(null==bluetoothAdapter){
            return;
        }
        bluetoothAdapter.enable();
    }

    /** 关闭蓝牙 **/

    private void closeBluetooth(){
        if(null==bluetoothAdapter){
            return;
        }
        bluetoothAdapter.disable();
    }

    private ScanCallback scanCallback=new ScanCallback() {
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);

        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:41---"+result.getDevice().getName());
//            if(!nowUseListenScanData){
//                nowUseListenScanData=true;
//            }
            if(BleCheckUtil.checkTheBleCanUse(result)){
//                stopScan();
                //不能stopScan要不会导致，后续连接不上
            }

        }
    };

    /** 开始扫描 **/
//    long lastSearchBleTime;
//    private boolean nowUseListenScanData;
    private int countCannotScandataNumb;
    public void startSearchBLE(){
        Log.i("tryToConnectOldDevice","tryToConnectOldDevice111100");
//        if(null==bluetoothAdapter){
//            return;
//        }
//        long nowTime=System.currentTimeMillis();
//        if(lastSearchBleTime!=0&&){
//            Log.i("1111","1234kasjffdlks:000000000------:"+(nowTime-lastSearchBleTime)/1000+"S");
//
//        }
//        lastSearchBleTime=nowTime;

//
        if(null==bluetoothLeScanner){
            return;
        }

        Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:1:---111---"+countCannotScandataNumb);

        if(countCannotScandataNumb>=2){
            Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:1:---112");
            bluetoothLeScanner.startScan(scanCallback);
            bluetoothAdapter.startDiscovery();
        }else {
            Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:1:---113");
            bluetoothLeScanner.startScan(scanCallback);
        }

        Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:0");
        //bluetoothLeScanner.startScan(scanCallback)这个方法有点问题，搜索太久了，找不到数据,离谱大了
//        if(null!=BleConnectStatuUtil.getInstance().getNowUseBleDevice()){
//
//            tryToConnectOldDevice();
//
//        }else{
//            if(nowUseListenScanData){
//                Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:1:---112");
//                bluetoothLeScanner.startScan(scanCallback);
//            }else {
//                if(countCannotScandataNumb>=3){
//                    Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:1:---113");
//                    bluetoothAdapter.startDiscovery();
//
//                }else if(countCannotScandataNumb>=1){
//                    Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:1:---114");
//                    bluetoothLeScanner.startScan(scanCallback);
//                    bluetoothAdapter.startDiscovery();
//                }else {
//                    Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:1:---115");
//                    bluetoothLeScanner.startScan(scanCallback);
//                }
//            }


//        }



//        if(!nowUseListenScanData&&countCannotScandataNumb>=1){
//            if(countCannotScandataNumb>=1){
//
//            }
//            Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:2");
//
//        }



//        bluetoothAdapter.startDiscovery();
        countCannotScandataNumb++;
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEARCHING);

    }

    public void stopScan(){
        if(null==bluetoothLeScanner){
            return;
        }
        bluetoothLeScanner.stopScan(scanCallback);
    }


    public void checkAndToConnectOldDevice(){
        if(null!=BleConnectStatuUtil.getInstance().getNowUseBleDevice()){
            tryToConnectOldDevice();
        }
    }



    private void tryToConnectOldDevice(){
        try {
            Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:1:---114");
//            boolean canNotScanData=!nowUseListenScanData&&countCannotScandataNumb>1;
            if(!BleConnectStatuUtil.getInstance().checkCanUseOldDeviceAdress(true)){

                return;
            }
            Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:1:---115");
            connect(BleConnectStatuUtil.getInstance().getNowUseBleDevice().getAdress());
        }catch (Exception e){}
    }

    /**
     * 连接远程蓝牙
     */
    public boolean connect(String address) {
        if (null==bluetoothAdapter || TextUtils.isEmpty(address)) {
//            LG.e("BluetoothAdapter not initialized or unspecified address");
            //这里不能关闭，关闭之后重新连接部上
            Log.i("kankanadaptercannotuse","kankanadaptercannotuse---1");
            BleConnectStatuUtil.getInstance().setConnectErr(context.getResources().getString(com.nrmyw.ble_event_lib.R.string.ble_statu_adapter_can_not_use));
            return false;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
//            LG.e("Device not found. Unable to connect");
            BleConnectStatuUtil.getInstance().setConnectErr("Device not found");
            return false;
        }
        BlueToothGattManager.getInstance().initGatt(device,context);
//        LG.e("Trying to create a new connection:"+address);

        return true;
    }

    public void disconnect(){
//        if(null!=bluetoothAdapter){
//            bluetoothAdapter=null;
//        }
        BlueToothGattManager.getInstance().disconnect();
//        BleStatuSendUtil.sendDisconnected();
    }








}
