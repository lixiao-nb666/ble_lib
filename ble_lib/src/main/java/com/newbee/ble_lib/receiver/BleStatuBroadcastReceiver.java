package com.newbee.ble_lib.receiver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.newbee.ble_lib.bean.BleDeviceBean;
import com.newbee.ble_lib.config.BlueToothGattConfig;
import com.newbee.ble_lib.event.statu.BleStatuEventSubscriptionSubject;

import com.newbee.ble_lib.event.statu.BleStatu;
import com.newbee.ble_lib.util.BleConnectStatuUtil;

@SuppressLint("MissingPermission")
public class BleStatuBroadcastReceiver extends BroadcastReceiver {
    private final String tag="BlueBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
            sendChangedStatu(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1));
        }else if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
            BleConnectStatuUtil.getInstance().sendConnected();
        } else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
            BleConnectStatuUtil.getInstance().sendDisconnected();
        }else if(action.equals(BluetoothDevice.ACTION_FOUND)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.i(tag,"kankanweiyizhi:"+device.getName()+"----"+device.getAddress());
            String deviceName = device.getName();
//            String deviceHardwareAddress = device.getAddress(); // MAC address
            BleDeviceBean bleDeviceBean=BlueToothGattConfig.getInstance().checkBleName(deviceName);
            String address=device.getAddress();
            if(null!=bleDeviceBean&&!TextUtils.isEmpty(address)){
                BleConnectStatuUtil.getInstance().sendConnecting(context,bleDeviceBean,address);
            }
        }




    }

    public void sendChangedStatu(int bluetoothAdapterStatu){
        if(bluetoothAdapterStatu==BluetoothAdapter.STATE_TURNING_ON){
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.OPENING);
        }else if(bluetoothAdapterStatu==BluetoothAdapter.STATE_ON ){
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.STATE_ON);
        }else if(bluetoothAdapterStatu==BluetoothAdapter.STATE_OFF){
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.STATE_OFF);
        }
    }
}
