package com.newbee.ble_lib.bean;

import java.io.Serializable;

public class BleDeviceBean implements Serializable {
    private String deviceName;
    private int deviceType;
    private String deviceTitle;
    private String deviceBody;


    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceTitle() {
        return deviceTitle;
    }

    public void setDeviceTitle(String deviceTitle) {
        this.deviceTitle = deviceTitle;
    }

    public String getDeviceBody() {
        return deviceBody;
    }

    public void setDeviceBody(String deviceBody) {
        this.deviceBody = deviceBody;
    }

    @Override
    public String toString() {
        return "BleDeviceBean{" +
                "deviceName='" + deviceName + '\'' +
                ", deviceType=" + deviceType +
                ", deviceTitle='" + deviceTitle + '\'' +
                ", deviceBody='" + deviceBody + '\'' +
                '}';
    }
}
