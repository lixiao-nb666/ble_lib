package com.newbee.ble_tool.util;

public enum HudImageShowType {
    //直接发送指令
    HIDE((byte)0x00),//关闭
    SHOW((byte)0x01),//显示

    ;
    byte type;
    HudImageShowType(byte type){
        this.type=type;
    }

    public byte getType() {
        return type;
    }


}
