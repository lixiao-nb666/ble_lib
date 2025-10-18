package com.newbee.ble_lib.util;

import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;

public class BleErrManager {

    public static void sendConnectErrMsg(String errStr){
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CONNECTING_ERR,errStr);
    }

    public static void sendErrMsg(){}

}
