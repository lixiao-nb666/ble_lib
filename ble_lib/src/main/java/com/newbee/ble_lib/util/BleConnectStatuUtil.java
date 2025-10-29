package com.newbee.ble_lib.util;

import android.content.Context;
import android.text.TextUtils;

import com.newbee.ble_lib.manager.child.BleConnectManager;
import com.newbee.ble_lib.manager.child.BlueToothGattManager;
import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;

import java.security.spec.ECField;

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
    private String lastAddress;
    public void sendConnecting(Context context, BleDeviceBean bleDeviceBean, String address){
        if(null!=bleDeviceBean&&isConnect){
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.NONE,"Now is connected,Can not connect other !");
//            setConnectErr("Now is connected,Can not connect other !");
            return;
        }
        long nowTime=System.currentTimeMillis();
        if(BlueToothGattManager.getInstance().isConnecting()){
//            sendConnectErrMsg("Connecting,Please wait for 2S !");
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.NONE,"Connecting,Please wait !");
            return;
        }
        lastConnectTime=nowTime;
        nowUseBleDevice=bleDeviceBean;
        lastAddress=address;
        BleConnectManager.getInstance().connect(address);
    }

    public  void sendConnected(){
        isConnect=true;
        BlueToothGattSendMsgManager.getInstance().clear();
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CONNECTED);

    }

    public void setConnectErr(String errStr){
        lastConnectTime=0;
        BleErrManager.sendConnectErrMsg(errStr);
    }



    public synchronized void sendDisconnected(){

        if(!isConnect&&null==nowUseBleDevice){
            return;
        }
        lastConnectTime=0;
        isConnect=false;
        nowUseBleDevice=null;
        BlueToothGattSendMsgManager.getInstance().clear();
        BlueToothGattManager.getInstance().checkIsDisConnecting();
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.DISCONNECTED);
    }

    public void checkDisconnectedDevice(String checkName,String checkAdress){
        if(TextUtils.isEmpty(checkName)||TextUtils.isEmpty(checkAdress)){
            return;
        }

        if(null==nowUseBleDevice||!isConnect){
            return;
        }
        try {
            if(nowUseBleDevice.getDeviceName().equals(checkName)&&lastAddress.equals(checkAdress)){
                sendDisconnected();
            }
        }catch (Exception e){

        }

    }


}
