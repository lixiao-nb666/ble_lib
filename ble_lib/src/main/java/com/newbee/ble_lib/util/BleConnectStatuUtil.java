package com.newbee.ble_lib.util;

import android.content.Context;

import com.newbee.ble_lib.R;
import com.newbee.ble_lib.config.BlueToothGattConfig;
import com.newbee.ble_lib.manager.child.BleConnectManager;
import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;

public class BleConnectStatuUtil {
    private static BleConnectStatuUtil util;
    private boolean isConnect;
    private BleConnectStatuUtil(){
        isConnect=false;
    }

    public static BleConnectStatuUtil getInstance(){
        if(null==util){
            synchronized (BleConnectStatuUtil.class){
                if(null==util){
                    util=new BleConnectStatuUtil();
                }
            }
        }
        return util;
    }

    public boolean isConnect() {
        return isConnect;
    }

    private long lastConnectTime;
    public void sendConnecting(Context context, BleDeviceBean bleDeviceBean, String address){
        if(isConnect){
            sendConnectErrMsg("Now is connected,Can not connect other !");
            return;
        }
        long nowTime=System.currentTimeMillis();
        if(lastConnectTime!=0&&nowTime-lastConnectTime<4000){
            sendConnectErrMsg("Connecting,Please wait for 4S !");
            return;
        }
        lastConnectTime=nowTime;
        BlueToothGattConfig.getInstance().setNowUseBleDevice(bleDeviceBean);
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CONNECTING);
        BleConnectManager.getInstance().connect(context,address);
    }

    public  void sendConnected(){
        isConnect=true;
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CONNECTED);
    }

    public void setConnectErr(String errStr){
        lastConnectTime=0;
        sendConnectErrMsg(errStr);
    }

    private void sendConnectErrMsg(String errStr){

        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,com.nrmyw.ble_event_lib.R.string.ble_statu_connecting_err,errStr);
    }

    public void sendDisconnected(){
        lastConnectTime=0;
        isConnect=false;
        BlueToothGattConfig.getInstance().setNowUseBleDevice(null);
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.DISCONNECTED);
    }


}
