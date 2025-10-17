package com.newbee.ble_lib.util;

import android.content.Context;

import com.newbee.ble_lib.manager.child.BleConnectManager;
import com.newbee.ble_lib.manager.child.BlueToothGattManager;
import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;

public class BleConnectStatuUtil {
    private static BleConnectStatuUtil util;
    private BleDeviceBean nowUseBleDevice;
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

    public BleDeviceBean getNowUseBleDevice() {
        return nowUseBleDevice;
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
        if(lastConnectTime!=0&&nowTime-lastConnectTime<2222){
//            sendConnectErrMsg("Connecting,Please wait for 2S !");
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CONNECTING,"Connecting,Please wait for 2S !");
            return;
        }
        lastConnectTime=nowTime;
        nowUseBleDevice=bleDeviceBean;
        BleConnectManager.getInstance().connect(context,address);
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CONNECTING);

    }

    public  void sendConnected(){
        isConnect=true;
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CONNECTED);
        BlueToothGattSendMsgManager.getInstance().clear();
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
        nowUseBleDevice=null;
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.DISCONNECTED);
        BlueToothGattSendMsgManager.getInstance().clear();
    }


}
