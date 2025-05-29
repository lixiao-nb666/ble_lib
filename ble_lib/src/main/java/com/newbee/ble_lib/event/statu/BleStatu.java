package com.newbee.ble_lib.event.statu;

import com.newbee.ble_lib.R;

public enum BleStatu {
    NONE(R.string.ble_statu_none),
    RUN_ERR(R.string.ble_statu_run_err),//运行出错
    BLE_CAN_NOT_USE(R.string.ble_statu_can_not_use),//系统蓝牙不能使用
    BLE_MANAGER_CAN_NOT_USE(R.string.ble_statu_manager_can_not_use),//系统蓝牙管理器不能使用
    BLE_ADAPTER_CAN_NOT_USE(R.string.ble_statu_adapter_can_not_use),//系统蓝牙适配器不能使用
    OPENING(R.string.ble_statu_opening),//打开中
    STATE_ON(R.string.ble_statu_state_on),//已经打开
    STATE_OFF(R.string.ble_statu_state_off),//关闭
    CONNECTING(R.string.ble_statu_connecting),//关闭
    CONNECTED(R.string.ble_statu_connected),//连接成功
    DISCONNECTED(R.string.ble_statu_disconnected),//断开连接
    CAN_SEND_DATA(R.string.ble_statu_can_send_data),//现在能发送数据-不带数据
    SENDING_DATA(R.string.ble_statu_sending_data),//发送数据成功-带的数据为byte[] bytes
    RETRUN_BYTES(R.string.ble_statu_retrun_bytes),//蓝牙返回值
    SEND_IMAGE_START(R.string.ble_statu_send_image_start),//蓝牙准备开始发送图片
    SEND_IMAGE_END(R.string.ble_statu_send_image_end),//蓝牙发送图片结束
    ;
    int strId;
    BleStatu(int strId){
        this.strId=strId;
    }

    public int getStrId() {
        return strId;
    }
}
