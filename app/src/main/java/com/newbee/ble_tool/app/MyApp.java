package com.newbee.ble_tool.app;


import com.newbee.ble_lib.NewBeeBleManager;
import com.newbee.ble_tool.config.T800Config;
import com.newbee.ble_tool.type.BleDeviceType;
import com.newbee.bulid_lib.mybase.appliction.BaseApplication;
import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;


public class MyApp extends BaseApplication {

    @Override
    protected void init() {
        NewBeeBleConfig.getInstance().init(T800Config.isAutomatic,T800Config.mtu,T800Config.serviceID,T800Config.writeID,T800Config.noticeID, BleDeviceType.getBleDeviceTypeList());
        NewBeeBleManager.getInstance().init(getBaseContext());
    }

    @Override
    protected void needClear(String str) {

    }

    @Override
    protected void close() {
        NewBeeBleManager.getInstance().close();
    }




}
