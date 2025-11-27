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

    public void close(){
        if(null!=context){
            context=null;
        }
        if(null!=bluetoothAdapter){
            bluetoothAdapter=null;
        }
        if(null!=bluetoothManager){
            bluetoothManager=null;
        }
        BlueToothGattManager.getInstance().close();
        bluetoothManager=null;
    }


    private Context context;
    public void havePermissionInitBle(Context context, PackageManager packageManager){
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
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,com.nrmyw.ble_event_lib.R.string.ble_statu_adapter_can_not_use);
            return;
        }
        bluetoothLeScanner =bluetoothAdapter.getBluetoothLeScanner();
        if(null==bluetoothLeScanner){
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,com.nrmyw.ble_event_lib.R.string.ble_statu_adapter_can_not_use);
            return;
        }
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.INIT);
        this.context=context;
        if(bleIsOpen()){
            startSearchBLE();
        }else{
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

            if(BleCheckUtil.checkTheBleCanUse(result)){
                stopScan();
            }

        }
    };

    /** 开始扫描 **/
//    long lastSearchBleTime;
    public void startSearchBLE(){

//        if(null==bluetoothAdapter){
//            return;
//        }
//        long nowTime=System.currentTimeMillis();
//        if(lastSearchBleTime!=0&&){
//            Log.i("1111","1234kasjffdlks:000000000------:"+(nowTime-lastSearchBleTime)/1000+"S");
//
//        }
//        lastSearchBleTime=nowTime;

//        bluetoothAdapter.startDiscovery();
        if(null==bluetoothLeScanner){
            return;
        }
        bluetoothLeScanner.startScan(scanCallback);
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEARCHING);
        tryToConnectOldDevice();
    }

    public void stopScan(){
        if(null==bluetoothLeScanner){
            return;
        }
        bluetoothLeScanner.stopScan(scanCallback);
    }






    private void tryToConnectOldDevice(){
        try {
            if(!BleConnectStatuUtil.getInstance().checkCanUseOldDeviceAdress()){
                return;
            }
            if(null==BleConnectStatuUtil.getInstance().getNowUseBleDevice()||TextUtils.isEmpty(BleConnectStatuUtil.getInstance().getNowUseBleDevice().getAdress())){
                return;
            }
            connect(BleConnectStatuUtil.getInstance().getNowUseBleDevice().getAdress());
        }catch (Exception e){}
    }

    /**
     * 连接远程蓝牙
     */
    public boolean connect(String address) {
        if (null==bluetoothAdapter || TextUtils.isEmpty(address)) {
//            LG.e("BluetoothAdapter not initialized or unspecified address");

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
