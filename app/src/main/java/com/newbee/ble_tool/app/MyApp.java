package com.newbee.ble_tool.app;

import com.newbee.ble_lib.config.BlueToothGattConfig;
import com.newbee.ble_lib.BleManager;
import com.newbee.ble_tool.config.T800Config;
import com.newbee.ble_tool.type.BleDeviceType;
import com.newbee.bulid_lib.mybase.appliction.BaseApplication;


public class MyApp extends BaseApplication {

    @Override
    protected void init() {
        BlueToothGattConfig.getInstance().init(T800Config.isAutomatic,T800Config.mtu,T800Config.serviceID,T800Config.writeID,T800Config.noticeID, BleDeviceType.getBleDeviceTypeList());
        BleManager.getInstance().init(getBaseContext());
    }

    @Override
    protected void needClear(String str) {

    }

    @Override
    protected void close() {
        BleManager.getInstance().close();
    }




}
