package com.newbee.ble_lib.manager.file;

import android.text.TextUtils;
import android.util.Log;

import com.newbee.ble_lib.manager.file.send.SendBitmapManager;
import com.newbee.ble_lib.manager.file.send.SendFileManager;
import com.newbee.ble_lib.manager.file.send.SendOtaManager;
import com.nrmyw.ble_event_lib.bean.BleSendFileInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendOtaInfoBean;

public class BlueToothGattGetFileDataThread extends Thread{
    private Listen listen;

//    private Queue<byte[]> dataInfoQueue=new LinkedList<>();

    private boolean canStart;

    private BleSendImageInfoBean bleSendImageInfoBean;
    public BlueToothGattGetFileDataThread(BleSendImageInfoBean bleSendImageInfoBean, Listen listen){
        if(null==bleSendImageInfoBean||null==bleSendImageInfoBean.getBitmap()){
            canStart=false;
            return;
        }
        this.bleSendImageInfoBean=bleSendImageInfoBean;
        this.listen=listen;
        canStart=true;
    }

    private BleSendFileInfoBean bleSendFileInfoBean;
    public BlueToothGattGetFileDataThread(BleSendFileInfoBean bleSendFileInfoBean, Listen listen){
        if(null==bleSendFileInfoBean|| TextUtils.isEmpty(bleSendFileInfoBean.getFilePath())){
            canStart=false;
            return;
        }
        this.bleSendFileInfoBean=bleSendFileInfoBean;
        this.listen=listen;
        canStart=true;
    }

    private BleSendOtaInfoBean bleSendOtaInfoBean;
    public BlueToothGattGetFileDataThread(BleSendOtaInfoBean bleSendOtaInfoBean, Listen listen){
        if(null==bleSendOtaInfoBean|| TextUtils.isEmpty(bleSendOtaInfoBean.getFilePath())){
            canStart=false;
            return;
        }
        this.bleSendOtaInfoBean=bleSendOtaInfoBean;
        this.listen=listen;
        canStart=true;
    }


    @Override
    public void interrupt() {
        super.interrupt();
        if(null!=blueToothGattSendFile){
            blueToothGattSendFile.clear();
        }
    }

    public boolean isStart(){
        if(null!=blueToothGattSendFile){
           return blueToothGattSendFile.isStart();
        }
        return false;
    }


    private BlueToothGattSendFile blueToothGattSendFile;
    @Override
    public void run() {
        super.run();
        try {
            if(!canStart){
                return;
            }
            if(null==listen){
                return;
            }
            if(null!=bleSendImageInfoBean){
                blueToothGattSendFile=new SendBitmapManager(bleSendImageInfoBean);
                blueToothGattSendFile.setListen(listen);
            }else if(null!=bleSendFileInfoBean){
                blueToothGattSendFile=new SendFileManager(bleSendFileInfoBean);
                blueToothGattSendFile.setListen(listen);
            }else if(null!=bleSendOtaInfoBean){
                blueToothGattSendFile=new SendOtaManager(bleSendOtaInfoBean);
                blueToothGattSendFile.setListen(listen);
            }
            if(null!=blueToothGattSendFile){
                blueToothGattSendFile.clear();
            }
        }catch (Exception e){
            listen.sendOver(0);
            Log.i("kankanshibushizheli","kankantubianzenmhuishi:1111222:err"+e.toString());
        }


    }



    public interface Listen{

        public void sendOver(long useTime);
    }

}
