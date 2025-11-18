package com.newbee.ble_tool.type;


import com.nrmyw.ble_event_lib.bean.BleDeviceBean;

import java.util.ArrayList;
import java.util.List;

public enum HudDevice {
    T800("T800","data",HudDeviceSendDataType.T800),
    T700_DEBUG("T800","DATA",HudDeviceSendDataType.T700),
    T700("T700","DATA","700666",0x0642,HudDeviceSendDataType.T700),
    T700_UPDATE_1("HUD-","-7DATA",HudDeviceSendDataType.T700),
    T700_UPDATE_2("hud-","-7data",HudDeviceSendDataType.T700),


    ;
    private String title;
    private String body;

    private int manufacturerSpecificId;
    private String manufacturerSpecificData;
    private HudDeviceSendDataType sendDataType;
    HudDevice(String title, String body, HudDeviceSendDataType hudDeviceSendDataType){
        this.title=title;
        this.body=body;
        this.sendDataType=hudDeviceSendDataType;
    }

    HudDevice(String manufacturerSpecificData,int manufacturerSpecificId, HudDeviceSendDataType hudDeviceSendDataType){
        this.manufacturerSpecificData=manufacturerSpecificData;
        this.manufacturerSpecificId=manufacturerSpecificId;
        this.sendDataType=hudDeviceSendDataType;
    }

    HudDevice(String title, String body,String manufacturerSpecificData, int manufacturerSpecificId,HudDeviceSendDataType hudDeviceSendDataType){
        this.title=title;
        this.body=body;
        this.manufacturerSpecificData=manufacturerSpecificData;
        this.manufacturerSpecificId=manufacturerSpecificId;
        this.sendDataType=hudDeviceSendDataType;
    }

    public BleDeviceBean getBleDevice(){
        BleDeviceBean bleDeviceBean=new BleDeviceBean();
        bleDeviceBean.setDeviceName(name());
        bleDeviceBean.setDeviceType(ordinal());
        bleDeviceBean.setDeviceTitle(title);
        bleDeviceBean.setDeviceBody(body);
        bleDeviceBean.setManufacturerSpecificData(manufacturerSpecificData);
        bleDeviceBean.setManufacturerSpecificId(manufacturerSpecificId);
        return bleDeviceBean;
    }

    public HudDeviceSendDataType getSendDataType() {
        return sendDataType;
    }



    public static List<BleDeviceBean> getBleDeviceTypeList(){
        List<BleDeviceBean> typeList=new ArrayList<>();
        for(HudDevice deviceType:HudDevice.values()){
            typeList.add(deviceType.getBleDevice());
        }
        return typeList;
    }

    public static HudDevice getBleDeviceType(BleDeviceBean bleDeviceBean){
        try {
            return HudDevice.values()[bleDeviceBean.getDeviceType()];
        }catch (Exception e){
            return null;
        }
    }
}
