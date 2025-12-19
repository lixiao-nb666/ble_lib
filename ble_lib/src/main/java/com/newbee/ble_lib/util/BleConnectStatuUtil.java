package com.newbee.ble_lib.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.newbee.ble_lib.config.BleManagerConfig;
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

    private long connectTime;
    private long disConnectTime;
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

    private void clear(){
        nowUseBleDevice=null;
        isConnect=false;
        connectTime=0;
    }


    public boolean checkCanUseOldDeviceAdress(boolean canNotScanData){
        if(disConnectTime==0){
            //因为是第一次，直接返回
            return false;
        }

        if(isConnect){
            //如果连接状态，直接返回不行
            return false;
        }
        if(null==BleConnectStatuUtil.getInstance().getNowUseBleDevice()){
            //数据为空，也不行
            return false;
        }
        if(TextUtils.isEmpty(BleConnectStatuUtil.getInstance().getNowUseBleDevice().getAdress())){
            return false;
        }
        if(canNotScanData){
            return true;
        }

        if(System.currentTimeMillis()-disConnectTime>= BleManagerConfig.CONNECT_OLD_HUD_TIME){
            //超出有效连接时间内
            return false;
        }



        return true;
    }


    public BleDeviceBean getNowUseBleDevice() {

        return nowUseBleDevice;
    }

    public boolean isConnect() {
        return isConnect;
    }


    public void sendConnecting(BleDeviceBean bleDeviceBean, String address){
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

        nowUseBleDevice=bleDeviceBean;
        BleConnectManager.getInstance().connect(address);
    }


    public  void sendConnected(){
        connectTime=System.currentTimeMillis();
        disConnectTime=0;
        isConnect=true;
        BlueToothGattSendMsgManager.getInstance().clear();
        BleConnectManager.getInstance().stopScan();
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CONNECTED,nowUseBleDevice.getAdress());
    }

    public void setConnectErr(String errStr){
        BleErrManager.sendConnectErrMsg(errStr);
    }



    public synchronized void sendDisconnected(){
        if(!isConnect){
            return;
        }
        isConnect=false;
        disConnectTime=System.currentTimeMillis();
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
            if(nowUseBleDevice.getDeviceName().equals(checkName)&&nowUseBleDevice.getAdress().equals(checkAdress)){
                sendDisconnected();
            }
        }catch (Exception e){

        }

    }


    public long getConnectTime() {
        return connectTime;
    }

    public long getDisConnectTime() {
        return disConnectTime;
    }
}
