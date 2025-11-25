package com.newbee.ble_tool.util;


import com.nrmyw.ble_event_lib.util.BleByteUtil;



public class HudCmdSendDataUtil {




    public static byte[] getReadySendImage(Object... objects) {
        if (null == objects || objects.length < 5) {
            return null;
        }
        int w= (int) objects[0];
        byte[] wBs=BleByteUtil.intToByteArray32only2(w);
        int h= (int) objects[1];
        byte[] hBs=BleByteUtil.intToByteArray32only2(h);
        int size= (int) objects[2];
        byte[] sizeBs= BleByteUtil.intToByteArray32(size);
        HudSendImageType sendImageType= (HudSendImageType) objects[3];
        int imageShowType= (int) objects[4];
        byte[]bytes=new byte[10];
        bytes[0]=wBs[0];
        bytes[1]=wBs[1];
        bytes[2]=hBs[0];
        bytes[3]=hBs[1];
        bytes[4]=sizeBs[0];
        bytes[5]=sizeBs[1];
        bytes[6]=sizeBs[2];
        bytes[7]=sizeBs[3];
        bytes[8]=sendImageType.getType();
        bytes[9]=(byte)(imageShowType & 0xFF);
        return bytes;
    }




}
