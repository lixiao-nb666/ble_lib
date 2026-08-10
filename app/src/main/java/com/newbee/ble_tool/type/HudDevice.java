package com.newbee.ble_tool.type;


import com.nrmyw.ble_event_lib.bean.BleDeviceBean;


import java.util.ArrayList;
import java.util.List;

public enum HudDevice {
    T800("T800","data", HudDeviceSendDataType.T800),

    T800_1("T800","DATA",HudDeviceSendDataType.T800),

    T3000("T3000","DATA", HudDeviceSendDataType.T800),
    T3000_1("T3000","data", HudDeviceSendDataType.T800),

    H3000("H3000","DATA", HudDeviceSendDataType.T800),
    H3000_1("H3000","data", HudDeviceSendDataType.T800),
    T700("T700","DATA","700666",0x0642,HudDeviceSendDataType.T700_GUI3),

    T700_1("HUD-","T700-DATA",HudDeviceSendDataType.T700),
    T700_2("hud-","T700-data",HudDeviceSendDataType.T700),

    H5000("H5000","DATA","700666",0x0643,HudDeviceSendDataType.T850),
    H5000_1("HUD-","H5000-DATA",HudDeviceSendDataType.T850),
    H5000_2("hud-","H5000-data",HudDeviceSendDataType.T850),

    T900("T900","DATA","700666",0x0643,HudDeviceSendDataType.T850),
    T900_1("HUD-","T900-DATA",HudDeviceSendDataType.T850),
    T900_2("hud-","T900-data",HudDeviceSendDataType.T850),

    T850("T850","DATA","700666",0x0643,HudDeviceSendDataType.T850),
    T850_1("HUD-","T850-DATA",HudDeviceSendDataType.T850),
    T850_2("hud-","T850-data",HudDeviceSendDataType.T850),


    HUD_T("HUD-","DATA",HudDeviceSendDataType.T850),
    HUT_T("hud-","data",HudDeviceSendDataType.T850),
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

    HudDevice(String manufacturerSpecificData, int manufacturerSpecificId, HudDeviceSendDataType hudDeviceSendDataType){
        this.manufacturerSpecificData=manufacturerSpecificData;
        this.manufacturerSpecificId=manufacturerSpecificId;
        this.sendDataType=hudDeviceSendDataType;
    }

    HudDevice(String title, String body, String manufacturerSpecificData, int manufacturerSpecificId, HudDeviceSendDataType hudDeviceSendDataType){
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
