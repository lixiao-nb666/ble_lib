package com.newbee.ble_tool.config;

public class T800Config {

    public static  final boolean isAutomatic = true;
    public static  final int mtu = 517;
    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_GATT_WRITE_STATE = "ACTION_GATT_WRITE_STATE";
    public static String serviceID = "0000ffff-0000-1000-8000-00805f9b34fb";
    public static String writeID = "00009abc-0000-1000-8000-00805f9b34fb";
    public static String noticeID = "00001234-0000-1000-8000-00805f9b34fb";
    public static byte cmdt1=(byte) 0xA5;//T800固定要加的第一个字节
    public static byte cmdt2=(byte) 0x5A;//T800固定要加的第二个字节
    public static byte end1=(byte) 0x0D;//T800文档要求要加，实际不加的
    public static byte end2=(byte) 0x0A;//T800文档要求要加，实际不加的
}
