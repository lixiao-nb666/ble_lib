package com.newbee.ble_lib.receiver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;


import com.newbee.ble_lib.manager.child.BleConnectManager;
import com.newbee.ble_lib.manager.child.BlueToothGattManager;
import com.newbee.ble_lib.util.BleCheckUtil;
import com.newbee.ble_lib.util.BleConnectStatuUtil;
import com.nrmyw.ble_event_lib.bean.BleDeviceBean;

import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;

@SuppressLint("MissingPermission")
public class BleStatuBroadcastReceiver extends BroadcastReceiver {
    private final String tag="BlueBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
            sendChangedStatu(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1));
        }else if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
//            BleConnectStatuUtil.getInstance().sendConnected();
//            Log.w(tag,"BluetoothAdapter  initialized  111:22222");
        } else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
//            BleConnectStatuUtil.getInstance().sendDisconnected();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            String address=device.getAddress();
//            Log.w(tag,"BluetoothAdapter  initialized  111556677--88--"+ deviceName+"--"+address);
            BleConnectStatuUtil.getInstance().checkDisconnectedDevice(deviceName,address);
        }else if(action.equals(BluetoothDevice.ACTION_FOUND)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:42---"+device.getName());
            BleCheckUtil.checkTheBleCanUse(device);
        }




    }

    public void sendChangedStatu(int bluetoothAdapterStatu){
        if(bluetoothAdapterStatu==BluetoothAdapter.STATE_TURNING_ON){
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.OPENING);
        }else if(bluetoothAdapterStatu==BluetoothAdapter.STATE_ON ){
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.STATE_ON);
        }else if(bluetoothAdapterStatu==BluetoothAdapter.STATE_OFF){
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.STATE_OFF);
            Log.i("ble_is_open","ble_is_open_11111:to open ble");
            BleConnectManager.getInstance().openBluetooth();
        }
    }
}
