package com.newbee.ble_lib.service;

import com.newbee.ble_lib.R;

public enum BluetoothGattServiceMsgType {
    NONE(R.string.ble_statu_none),
    INIT_BLE(R.string.ble_statu_user_init),
    SCAN_BLE(R.string.ble_statu_user_scan),
    DISCONNECT_BLE(R.string.ble_statu_user_disconnect),
    SEND_CMD(R.string.ble_statu_user_send_cmd),
    SEND_IMAGE(R.string.ble_statu_user_send_image),
    SEND_CMD_BY_IMAGE_INDEX(R.string.ble_statu_user_send_image_cmd_index),
    ;
    private int strId;
    BluetoothGattServiceMsgType(int strId){
        this.strId=strId;
    }

    public int getStrId() {
        return strId;
    }
}
