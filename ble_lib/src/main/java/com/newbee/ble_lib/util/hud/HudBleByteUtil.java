package com.newbee.ble_lib.util.hud;


import com.newbee.ble_lib.NewBeeBleManager;
import com.newbee.ble_lib.config.BleManagerConfig;
import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;

public class HudBleByteUtil {


    public  static byte[] getImageAllByte(int w,int h,int size,boolean isStart,int imageShowType){
        byte title= BleManagerConfig.CMD_SEND_IMAGE_TITLE;
        byte[] body=HudCmdSendDataUtil.getReadySendImage(w,h,size,isStart,imageShowType);
        return HudBleByteUtil.useTitleAndBodyGetAllCmdBytes(title,body);
    }

    public static byte[] useTitleAndBodyGetAllCmdBytes(byte title,byte[] body){
        int needLength=BleManagerConfig.defMixLength;
        if(null!=body&&body.length>0){
            needLength+=body.length;
        }
        byte[] result = new byte[needLength];
        result[0]= BleManagerConfig.CMD_TITLE_1;
        result[1]= BleManagerConfig.CMD_TITLE_2;
        result[2]= (byte) ((needLength >> 8) & 0xFF);
        result[3]= (byte) (needLength & 0xFF);
        result[4]=title;
        if(needLength>BleManagerConfig.defMixLength){
            for(int i=0;i<body.length;i++){
                result[i+5] = body[i];
            }
        }
        if(BleManagerConfig.defMixLength>=7){
            result[needLength-2]=BleManagerConfig.CMD_END_1;
            result[needLength-1]=BleManagerConfig.CMD_TITLE_2;
        }
        return result;
    }








}
