package com.newbee.ble_tool.type;

import com.newbee.ble_lib.bean.BleDeviceBean;

import java.util.ArrayList;
import java.util.List;

public enum BleDeviceType {

    T800("T800","data"),
    T700("T700","data"),
    T700_1("T800","DATA"),
    ;
    private String title;
    private String body;
    BleDeviceType(String title,String body){
        this.title=title;
        this.body=body;
    }

    public  BleDeviceBean getBleDevice(){
        BleDeviceBean bleDeviceBean=new BleDeviceBean();
        bleDeviceBean.setDeviceName(name());
        bleDeviceBean.setDeviceType(ordinal());
        bleDeviceBean.setDeviceTitle(title);
        bleDeviceBean.setDeviceBody(body);
        return bleDeviceBean;
    }



    public static List<BleDeviceBean> getBleDeviceTypeList(){
        List<BleDeviceBean> typeList=new ArrayList<>();
        for(BleDeviceType deviceType:BleDeviceType.values()){
            typeList.add(deviceType.getBleDevice());
        }
        return typeList;
    }

    public static BleDeviceType getBleDeviceType(BleDeviceBean bleDeviceBean){
        try {
            return BleDeviceType.values()[bleDeviceBean.getDeviceType()];
        }catch (Exception e){
            return null;
        }
    }
}
