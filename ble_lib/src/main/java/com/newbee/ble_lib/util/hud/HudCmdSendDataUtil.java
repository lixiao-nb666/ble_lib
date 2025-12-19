package com.newbee.ble_lib.util.hud;


import com.newbee.ble_lib.config.BleManagerConfig;
import com.newbee.ble_lib.util.CryptoUtils;
import com.nrmyw.ble_event_lib.util.BleByteUtil;



public class HudCmdSendDataUtil {




    public static byte[] getReadySendImage(int w,int h,int size,boolean isStart,int imageShowType) {
//        if (null == objects || objects.length < 5) {
//            return null;
//        }

        byte[] wBs=BleByteUtil.intToByteArray32only2(w);

        byte[] hBs=BleByteUtil.intToByteArray32only2(h);

        byte[] sizeBs= BleByteUtil.intToByteArray32(size);
//        HudSendImageType sendImageType= (HudSendImageType) objects[3];

        byte[]bytes=new byte[10];
        bytes[0]=wBs[0];
        bytes[1]=wBs[1];
        bytes[2]=hBs[0];
        bytes[3]=hBs[1];
        bytes[4]=sizeBs[0];
        bytes[5]=sizeBs[1];
        bytes[6]=sizeBs[2];
        bytes[7]=sizeBs[3];
        if(isStart){
            bytes[8]= BleManagerConfig.CMD_SEND_IMAGE_START;
        }else {
            bytes[8]=BleManagerConfig.CMD_SEND_IMAGE_END;
        }
        bytes[9]=(byte)(imageShowType & 0xFF);
        return bytes;
    }


    public static byte[] getReadySendFile( int length,byte[] md5Bytes,boolean isStart){
        int l=5;
        if(isStart){
            l=5;
        }else {
            l=2;
        }
        byte[]bytes=new byte[l];
        bytes[0]=BleManagerConfig.CMD_SEND_FILE_TITLE;
        if(isStart){
            bytes[1]=BleManagerConfig.CMD_SEND_FILE_START;
        }else {
            bytes[1]=BleManagerConfig.CMD_SEND_FILE_END;
        }
        if(l==2){
            return bytes;
        }

        byte[] lBytes= BleByteUtil.intToByteArray32only3(length);
        bytes[2]=lBytes[0];
        bytes[3]=lBytes[1];
        bytes[4]= lBytes[2];
//        byte[] cmd = new byte[bytes.length + md5Bytes.length];
        byte[] cmd = new byte[bytes.length ];
        System.arraycopy(bytes, 0, cmd, 0, bytes.length);
//        System.arraycopy(md5Bytes, 0, cmd, bytes.length, md5Bytes.length);
        return cmd;
    }


    public static byte[] getSendFile(int index,byte[] defdatas){
        byte[] datas=defdatas;
//        byte[] datas=new byte[defdatas.length];
//        for(int i=0;i<defdatas.length;i++){
//            datas[i]=defdatas[defdatas.length-1-i];
//        }

        byte[] indexBytes=BleByteUtil.intToByteArray32only2(index);
        byte[] lBytes=BleByteUtil.intToByteArray32only2(datas.length);
        byte[]bytes=new byte[6];
        bytes[0]=BleManagerConfig.CMD_SEND_FILE_TITLE;
        bytes[1]=BleManagerConfig.CMD_SEND_FILE_SENDING;
        bytes[2]= indexBytes[0];
        bytes[3]= indexBytes[1];
        bytes[4]= lBytes[0];
        bytes[5]= lBytes[1];
        byte[] md5Bytes= CryptoUtils.getMD5(datas);
        byte[] cmd = new byte[bytes.length +datas.length+ md5Bytes.length];
        System.arraycopy(bytes, 0, cmd, 0, bytes.length);
        System.arraycopy(datas, 0, cmd, bytes.length, datas.length);
        System.arraycopy(md5Bytes, 0, cmd, bytes.length+datas.length, md5Bytes.length);
        return cmd;
    }

    public static byte[] startOTA(){
        byte[]bytes=new byte[5];
        bytes[0]=(byte)0xA5;
        bytes[1]=(byte)0x5A;
        bytes[2]= (byte)0x00;
        bytes[3]= (byte)0x05;
        bytes[4] = (byte)0xA0;
        return bytes;

    }


}
