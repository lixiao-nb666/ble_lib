package com.newbee.ble_tool.config;

import com.newbee.ble_lib.bean.BleDeviceBean;
import com.newbee.ble_lib.config.BlueToothGattConfig;
import com.newbee.ble_lib.manager.image.BitmapQualityType;
import com.nrmyw.gaode_t.event.HudNaviEventImp;
import com.nrmyw.gaode_t.event.debug.GaoDeDeBugEvent;
import com.nrmyw.gaode_t.type.BleDeviceType;
import com.nrmyw.gaode_t.util.GaoDeHudSendUtil;

public class GaoDeHudSendConfig {

    private static GaoDeHudSendConfig config;

    private boolean isNeedAddLaneInfo=true;

    private HudSendBitmapQualityType qualityType;

    private BleDeviceType connectedBleDevice;

    private GaoDeHudSendConfig(){
        qualityType=BitmapQualityType.DEF;
    }

    public static GaoDeHudSendConfig getInstance(){
        if(null==config){
            synchronized (GaoDeHudSendUtil.class){
                if(null==config){
                    config=new GaoDeHudSendConfig();
                }
            }
        }
        return config;
    }

    public boolean isNeedAddLaneInfo() {
        return isNeedAddLaneInfo;
    }

    public void setNeedAddLaneInfo(boolean needAddLaneInfo) {
        isNeedAddLaneInfo = needAddLaneInfo;
    }

    public BitmapQualityType getQualityType() {
        return qualityType;
    }

    public void setQualityType(BitmapQualityType qualityType) {
        this.qualityType = qualityType;
    }

    public BleDeviceType getBleDeviceType() {
        return connectedBleDevice;
    }

    public HudNaviEventImp getEventImp(){
        if(null==connectedBleDevice||null==connectedBleDevice.getEventImp()){
            return GaoDeDeBugEvent.getInstance();
        }

        return connectedBleDevice.getEventImp();
    }

    public void setBleDeviceConnecting(){
        BleDeviceBean bleDeviceBean= BlueToothGattConfig.getInstance().getNowUseBleDevice();
        if(null!=bleDeviceBean){
            connectedBleDevice=BleDeviceType.values()[bleDeviceBean.getDeviceType()];
        }
    }

    public void setBleDeviceConnected(){
        BleDeviceBean bleDeviceBean= BlueToothGattConfig.getInstance().getNowUseBleDevice();
        if(null!=bleDeviceBean){
            connectedBleDevice=BleDeviceType.values()[bleDeviceBean.getDeviceType()];
//            GaoDeEventManager.getInstance().sendEventImp(connectedBleDevice.getEventImp());
        }
    }


    public void setBleDeviceDisconnected() {
//        connectedBleDevice=null;
//        GaoDeEventManager.getInstance().sendEventImp(null);
    }
}
