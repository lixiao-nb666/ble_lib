package com.newbee.ble_lib.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;

public class BleStatuBroadcastReceiverDao {
    private static BleStatuBroadcastReceiverDao bleStatuBroadcastReceiverDao;
    private BleStatuBroadcastReceiver bleStatuBroadcastReceiver=new BleStatuBroadcastReceiver();


    private BleStatuBroadcastReceiverDao(){}

    public static BleStatuBroadcastReceiverDao getInstance(){
        if(null==bleStatuBroadcastReceiverDao){
            synchronized (BleStatuBroadcastReceiverDao.class){
                if(null==bleStatuBroadcastReceiverDao){
                    bleStatuBroadcastReceiverDao=new BleStatuBroadcastReceiverDao();
                }
            }
        }
        return bleStatuBroadcastReceiverDao;
    }

    private Context context;
    public void init(Context context){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //蓝牙状态广播
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED); //蓝牙连接广播
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //蓝牙断开广播
        filter.addAction( BluetoothDevice.ACTION_FOUND);//获取搜索到的蓝牙信息
        context.registerReceiver(bleStatuBroadcastReceiver,filter);
        this.context=context;
    }

    public void close(){
        context.unregisterReceiver(bleStatuBroadcastReceiver);
        bleStatuBroadcastReceiverDao=null;
    }

}
