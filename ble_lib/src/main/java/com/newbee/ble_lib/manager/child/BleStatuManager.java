package com.newbee.ble_lib.manager.child;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;

import com.newbee.ble_lib.receiver.BleStatuBroadcastReceiver;

 class BleStatuManager {
    private static BleStatuManager bleStatuManager;
    private BleStatuBroadcastReceiver bleStatuBroadcastReceiver=new BleStatuBroadcastReceiver();


    private BleStatuManager(){}

    public static BleStatuManager getInstance(){
        if(null==bleStatuManager){
            synchronized (BleStatuManager.class){
                if(null==bleStatuManager){
                    bleStatuManager=new BleStatuManager();
                }
            }
        }
        return bleStatuManager;
    }

    public void init(Context context){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //蓝牙状态广播
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED); //蓝牙连接广播
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //蓝牙断开广播
        filter.addAction( BluetoothDevice.ACTION_FOUND);//获取搜索到的蓝牙信息
        context.registerReceiver(bleStatuBroadcastReceiver,filter);
    }

    public void close(Context context){
        context.unregisterReceiver(bleStatuBroadcastReceiver);
        bleStatuManager=null;
    }

}
