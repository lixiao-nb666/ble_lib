package com.newbee.ble_lib.config;

import com.nrmyw.ble_event_lib.type.BleSendBitmapQualityType;

public class BleManagerConfig {

    public static final int CAN_SEND_MTU=512;

    public static int MTU_CHA=5;

    public static final long CONNECT_OLD_HUD_TIME=30*1000;

    private static int SEND_IMAGE_MAX_KB_1=8;
    private static int SEND_IMAGE_MAX_KB_2=6;

    private static int SEND_IMAGE_MAX_KB_3=4;

    public static byte CMD_TITLE_1=(byte) 0xA5;//T800固定要加的第一个字节
    public static byte CMD_TITLE_2=(byte) 0x5A;//T800固定要加的第二个字节
    public static byte CMD_END_1=(byte) 0x0D;//T800文档要求要加，实际不加的
    public static byte CMD_END_2=(byte) 0x0A;//T800文档要求要加，实际不加的
    public static int  defMixLength=5;

    public static byte CMD_SEND_IMAGE_TITLE=(byte)0x10;

    public static byte CMD_SEND_IMAGE_START=(byte)0x01;

    public static byte CMD_SEND_IMAGE_END=(byte)0x00;

    public static byte CMD_SEND_FILE_TITLE=(byte)0xA5;

    public static byte CMD_SEND_FILE_START=(byte)0xA1;
    public static byte CMD_SEND_FILE_SENDING=(byte)0xA2;
    public static byte CMD_SEND_FILE_END=(byte)0xA3;

    public static byte CMD_ERR_INDEX=(byte)0xA4;

    public static byte CMD_ERR_NUMB=(byte)0xA5;

    public static byte CMD_CUCCESS=(byte)0xA6;


    public static int useBleSendBitmapQualityTypeGetMaxKb(BleSendBitmapQualityType qualityType){
        switch (qualityType){
            case ULTRA_HIGH:
                return BleManagerConfig.SEND_IMAGE_MAX_KB_1;
            case HIGH:
            case DEF:
                return BleManagerConfig.SEND_IMAGE_MAX_KB_2;
            default:
                return BleManagerConfig.SEND_IMAGE_MAX_KB_3;
        }
    }

}
