package com.newbee.ble_lib.util;

import android.content.Context;

import com.newbee.ble_lib.bean.BleDeviceBean;
import com.newbee.ble_lib.config.BlueToothGattConfig;
import com.newbee.ble_lib.event.statu.BleStatu;
import com.newbee.ble_lib.event.statu.BleStatuEventSubscriptionSubject;
import com.newbee.ble_lib.manager.child.BleConnectManager;

public class BleStatuSendUtil {

    public static void sendConnecting(Context context,BleDeviceBean bleDeviceBean, String address){
        BlueToothGattConfig.getInstance().setNowUseBleDevice(bleDeviceBean);
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CONNECTING);
        BleConnectManager.getInstance().connect(context,address);
    }

    public static void sendConnected(){
        BlueToothGattConfig.getInstance().setConnect(true);
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CONNECTED);
    }

    public static void sendDisconnected(){
        BlueToothGattConfig.getInstance().setConnect(false);
        BlueToothGattConfig.getInstance().setNowUseBleDevice(null);
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.DISCONNECTED);
    }


}
