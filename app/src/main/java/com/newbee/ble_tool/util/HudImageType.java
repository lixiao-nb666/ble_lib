package com.newbee.ble_tool.util;

public enum HudImageType {
    //直接发送指令
    IMAGE((byte)0x00),//关闭
    PROGRESS_BAR((byte)0x01),//显示

    ;
    byte type;
    HudImageType(byte type){
        this.type=type;
    }

    public byte getType() {
        return type;
    }
}
