package com.newbee.ble_tool.util;

public enum HudSendImageType {
    //直接发送指令
    END((byte)0x00),//结束
    START((byte)0x01),//开始

    ;
    byte type;
    HudSendImageType(byte type){
        this.type=type;
    }

    public byte getType() {
        return type;
    }


}
