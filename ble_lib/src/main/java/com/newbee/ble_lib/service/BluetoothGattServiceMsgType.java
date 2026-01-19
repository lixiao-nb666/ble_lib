package com.newbee.ble_lib.service;

import com.newbee.ble_lib.R;

public enum BluetoothGattServiceMsgType {
    NONE(com.nrmyw.ble_event_lib.R.string.ble_statu_none),
    INIT_BLE(com.nrmyw.ble_event_lib.R.string.ble_statu_user_init),
    SCAN_BLE(com.nrmyw.ble_event_lib.R.string.ble_statu_user_scan),
    DISCONNECT_BLE(com.nrmyw.ble_event_lib.R.string.ble_statu_user_disconnect),
    SEND_CMD(com.nrmyw.ble_event_lib.R.string.ble_statu_user_send_cmd),
    SEND_IMAGE(com.nrmyw.ble_event_lib.R.string.ble_statu_user_send_image),
    SEND_FILE(com.nrmyw.ble_event_lib.R.string.ble_statu_user_send_image),

    SEND_OTA(com.nrmyw.ble_event_lib.R.string.ble_statu_user_send_image),
    SEND_CMD_BY_BYTES_INDEX(com.nrmyw.ble_event_lib.R.string.ble_statu_user_send_image_cmd_index),

    CLEAR_INDEX_MSG(com.nrmyw.ble_event_lib.R.string.ble_clear_index_msg),
    CLEAR_MSG(com.nrmyw.ble_event_lib.R.string.ble_clear_msg),
    DELAY(com.nrmyw.ble_event_lib.R.string.ble_statu_none),
    ;
    private int strId;
    BluetoothGattServiceMsgType(int strId){
        this.strId=strId;
    }

    public int getStrId() {
        return strId;
    }
}
