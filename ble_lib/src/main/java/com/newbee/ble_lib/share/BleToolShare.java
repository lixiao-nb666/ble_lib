package com.newbee.ble_lib.share;

import android.content.Context;
import android.util.Log;

import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.bean.BlePairFuntionType;

public class BleToolShare extends BaseShare{
    public BleToolShare(Context context) {
        super(context);
    }
    private final String bleDeviceShareKey="bleDeviceShareKey_";
    private final String bleNameShareKey=bleDeviceShareKey+"bleName";
    private final String bleAdressShareKey=bleDeviceShareKey+"bleAdress";
    private final String deviceNameShareKey=bleDeviceShareKey+"deviceName";
    private final String deviceTypeShareKey=bleDeviceShareKey+"deviceType";
    private final String deviceTitleShareKey=bleDeviceShareKey+"deviceTitle";
    private final String deviceBodyShareKey=bleDeviceShareKey+"deviceBody";
    private final String blePairFuntionTypeShareKey=bleDeviceShareKey+"blePairFuntionType";
    private final String bleManufacturerSpecificIdShareKey=bleDeviceShareKey+"bleManufacturerSpecificId";
    private final String bleManufacturerSpecificDataShareKey=bleDeviceShareKey+ "bleManufacturerSpecificData";
    public void putShareBleDevice(BleDeviceBean bleDeviceBean){
        try {
            putString(bleNameShareKey,bleDeviceBean.getBleName());
            putString(bleAdressShareKey,bleDeviceBean.getAdress());
            putString(deviceNameShareKey,bleDeviceBean.getDeviceName());
            putString(deviceTypeShareKey,bleDeviceBean.getDeviceType()+"");
            putString(deviceTitleShareKey,bleDeviceBean.getDeviceTitle());
            putString(deviceBodyShareKey,bleDeviceBean.getDeviceBody());
            putString(blePairFuntionTypeShareKey,bleDeviceBean.getPairFuntionType().ordinal()+"");
            putString(bleManufacturerSpecificIdShareKey,bleDeviceBean.getManufacturerSpecificId()+"");
            putString(bleManufacturerSpecificDataShareKey,bleDeviceBean.getManufacturerSpecificData());
        }catch (Exception e){}
        if(null==bleDeviceBean){
            return;
        }

    }

    public BleDeviceBean getShareBleDevice(){
        try {
            String bleName=getString(bleNameShareKey);
            String bleAdress=getString(bleAdressShareKey);
            String deviceName=getString(deviceNameShareKey);
            String deviceTypeStr=getString(deviceTypeShareKey,"0");
            int deviceType=Integer.valueOf(deviceTypeStr);
            String deviceTitle=getString(deviceTitleShareKey);
            String deviceBody=getString(deviceBodyShareKey);
            String blePairFuntionTypeStr=getString(blePairFuntionTypeShareKey,"0");
            BlePairFuntionType blePairFuntionType=BlePairFuntionType.values()[Integer.valueOf(blePairFuntionTypeStr)];
            String bleManufacturerSpecificIdStr=getString(bleManufacturerSpecificIdShareKey,"0");
            int bleManufacturerSpecificId=Integer.valueOf(bleManufacturerSpecificIdStr);
            String bleManufacturerSpecificData=getString(bleManufacturerSpecificDataShareKey,"");
            BleDeviceBean bleDeviceBean=new BleDeviceBean();
            bleDeviceBean.setBleName(bleName);
            bleDeviceBean.setAdress(bleAdress);
            bleDeviceBean.setDeviceName(deviceName);
            bleDeviceBean.setDeviceType(deviceType);
            bleDeviceBean.setDeviceTitle(deviceTitle);
            bleDeviceBean.setDeviceBody(deviceBody);
            bleDeviceBean.setPairFuntionType(blePairFuntionType);
            bleDeviceBean.setManufacturerSpecificId(bleManufacturerSpecificId);
            bleDeviceBean.setManufacturerSpecificData(bleManufacturerSpecificData);
            return bleDeviceBean;
        }catch (Exception e){
            Log.i("checkShareBleDevice","checkShareBleDevice111661:"+e.toString());
        }
        return null;
    }

}
