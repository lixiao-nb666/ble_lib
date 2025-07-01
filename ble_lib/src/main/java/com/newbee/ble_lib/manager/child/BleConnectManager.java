package com.newbee.ble_lib.manager.child;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.newbee.ble_lib.R;
import com.newbee.ble_lib.event.statu.BleStatu;
import com.newbee.ble_lib.event.statu.BleStatuEventSubscriptionSubject;

import com.newbee.ble_lib.util.BleCheckUtil;
import com.newbee.ble_lib.util.BleConnectStatuUtil;


@SuppressLint("MissingPermission")
public class BleConnectManager {
    private static BleConnectManager bleManager;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;



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




    public void havePermissionInitBle(Context context, PackageManager packageManager){
        if(!BleCheckUtil.checkPhoneCanUseBle(packageManager)){
//            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.BLE_CAN_NOT_USE);
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,R.string.ble_statu_can_not_use);
            return;
        }
        bluetoothManager= (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if(null==bluetoothManager){
//            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.BLE_MANAGER_CAN_NOT_USE);
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,R.string.ble_statu_manager_can_not_use);
            return;
        }
        bluetoothAdapter=bluetoothManager.getAdapter();
        if(null==bluetoothAdapter){
//            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.BLE_ADAPTER_CAN_NOT_USE);
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,R.string.ble_statu_adapter_can_not_use);
            return;
        }

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

    /** 开始扫描 **/
    public void startSearchBLE(){
        if(null==bluetoothAdapter){
            return;
        }
//        bluetoothAdapter.stopLeScan(leScanCallback);
//        bluetoothAdapter.startLeScan(leScanCallback);
        bluetoothAdapter.startDiscovery();
    }




    private String mBluetoothDeviceAddress;
    /**
     * 连接远程蓝牙
     */
    public boolean connect(Context context,String address) {
        if (null==bluetoothAdapter || TextUtils.isEmpty(address)) {
//            LG.e("BluetoothAdapter not initialized or unspecified address");
            BleConnectStatuUtil.getInstance().setConnectErr(context.getResources().getString(R.string.ble_statu_adapter_can_not_use));
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
        mBluetoothDeviceAddress = address;
        return true;
    }

    public void disconnect(){
//        if(null!=bluetoothAdapter){
//            bluetoothAdapter=null;
//        }
        BlueToothGattManager.getInstance().disconnect();
//        BleStatuSendUtil.sendDisconnected();
    }



    public void close(Context context){
        if(null!=bluetoothAdapter){
            bluetoothAdapter=null;
        }
        if(null!=bluetoothManager){
            bluetoothManager=null;
        }
        BlueToothGattManager.getInstance().close();
        bluetoothManager=null;
    }




}
