package com.newbee.ble_lib.manager.image;

import android.graphics.Bitmap;
import android.util.Log;


import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.newbee.ble_lib.util.BleSendImageUtil;
import com.newbee.ble_lib.util.ImageCompressUtils;
import com.nrmyw.ble_event_lib.bean.BleSendImageEndInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageStartInfoBean;

import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.type.BleSendBitmapQualityType;
import com.nrmyw.ble_event_lib.util.BleByteUtil;

import java.util.LinkedList;
import java.util.Queue;

public class BlueToothGattGetBitmapDataThread extends Thread{
    private Listen listen;
    private BleSendImageInfoBean bleSendImageInfoBean;
//    private Queue<byte[]> dataInfoQueue=new LinkedList<>();
    private boolean isStart;
    private long startTime;
    private boolean canStart;
    public BlueToothGattGetBitmapDataThread(BleSendImageInfoBean bleSendImageInfoBean, Listen listen){
        if(null==bleSendImageInfoBean||null==bleSendImageInfoBean.getBitmap()){
            canStart=false;
            return;
        }
        this.bleSendImageInfoBean=bleSendImageInfoBean;
        this.listen=listen;
        canStart=true;
    }


    @Override
    public void interrupt() {
        super.interrupt();
        clear();
    }

    public boolean isStart(){
        return isStart;
    }


    public int msgIndex;
    private Bitmap newBitMap;
    @Override
    public void run() {
        super.run();
        try {
            if(!canStart){
                return;
            }
            if(null==bleSendImageInfoBean||null==bleSendImageInfoBean.getBitmap()||bleSendImageInfoBean.getBitmap().isRecycled()){
                Log.i("kankanshibushizheli","kankanshibushizheli:1111----"+(null==bleSendImageInfoBean)+(null==bleSendImageInfoBean.getBitmap())+(bleSendImageInfoBean.getBitmap().isRecycled()));
                return;
            }
            startTime=System.currentTimeMillis();
//            if(null==dataInfoQueue){
//                dataInfoQueue=new LinkedList<>();
//            }else {
//                dataInfoQueue.clear();
//            }

            isStart=true;
//            Log.i("kankantupian","kankantubianzenmhuishi:1111--"+bleSendImageInfoBean.getBitmap().getAllocationByteCount()+"--"+bleSendImageInfoBean.getBitmap().getWidth()+"*"+bleSendImageInfoBean.getBitmap().getHeight());
            newBitMap= BleSendImageUtil.autoScaleBitmap(bleSendImageInfoBean.getBitmap(),bleSendImageInfoBean.getMaxW(),bleSendImageInfoBean.getMaxH(),bleSendImageInfoBean.getBitmapQualityType().getZoomScaling());
            if(null==newBitMap){
                listen.sendOver(0);
                return;
            }
//            Log.i("kankantupian","kankantubianzenmhuishi:1111222--"+newBitMap.getByteCount()+"--"+newBitMap.getWidth()+"*"+newBitMap.getHeight());
            w=newBitMap.getWidth();
            h=newBitMap.getHeight();
            byte[] imageBytes=BleSendImageUtil.bitmap2Bytes(newBitMap,bleSendImageInfoBean.getBitmapQualityType());
//            byte[] imageBytes= ImageCompressUtils.compressBitmap(newBitMap,5,Bitmap.CompressFormat.JPEG);
            size=imageBytes.length;
//            Log.i("kankantupian","kankantubianzenmhuishi:1111333--"+size+"---"+bleSendImageInfoBean.getBitmapQualityType().getQualityV());
//            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.NONE,newBitMap);
            if(null==imageBytes||imageBytes.length==0){
                listen.sendOver(0);
                return;
            }
            sendImageStart();
            splitPacketForMtuByte(imageBytes);
//            queToSendCmd();
            over();
        }catch (Exception e){
            listen.sendOver(0);
            Log.i("kankanshibushizheli","kankantubianzenmhuishi:1111222:err"+e.toString());
        }
    }


    private void over(){
        long endTime=System.currentTimeMillis();
        long useTime=endTime-startTime;
        sendImageEnd(useTime);
        isStart=false;
        clear();
        if(null!=listen){
            listen.sendOver(useTime);
        }

    }

    private void clear(){
        try {
            if(null!=bleSendImageInfoBean){
                bleSendImageInfoBean=null;
            }
            if(null!=newBitMap&&!newBitMap.isRecycled()){
                newBitMap.recycle();
                newBitMap=null;
            }
        }catch (Exception e){}
    }

    public int getType(){
        try {
            return bleSendImageInfoBean.getType();
        }catch (Exception e){
            return -1;
        }

    }

    public void sendImageStart(){
        BlueToothGattSendMsgManager.getInstance().readySendImage();
        BleSendImageStartInfoBean startInfoBean=new BleSendImageStartInfoBean();
        startInfoBean.setW(w);
        startInfoBean.setH(h);
        startInfoBean.setSize(size);
        startInfoBean.setType(bleSendImageInfoBean.getType());
        startInfoBean.setName(bleSendImageInfoBean.getName());
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEND_IMAGE_START,startInfoBean);


    }

    public void sendImageEnd(long useTime){
        msgIndex++;
        BleSendImageEndInfoBean endInfoBean=new BleSendImageEndInfoBean();
        endInfoBean.setW(w);
        endInfoBean.setH(h);
        endInfoBean.setSize(size);
        endInfoBean.setUseTime(useTime);
        endInfoBean.setIndex(msgIndex);
        endInfoBean.setType(bleSendImageInfoBean.getType());
        endInfoBean.setName(bleSendImageInfoBean.getName());
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEND_IMAGE_END,endInfoBean);
        BlueToothGattSendMsgManager.getInstance().setImageMsgNum(msgIndex+1);
//        BleHintEventSubscriptionSubject.getInstance().sendImageEnd(w,h,size,useTime,index);
    }




//    public void queToSendCmd(){

//        while (!dataInfoQueue.isEmpty()){
//            if(null!=dataInfoQueue.peek()){
//                byte[] cmd=dataInfoQueue.poll();
//                msgIndex++;
//                BlueToothGattSendMsgManager.getInstance().sendMsgByImg(msgIndex,cmd);
//                Log.i("kankanfasongtupian", "-------------kankanshenmegui:111---555666--88--????????" + msgIndex+"---"+ BleByteUtil.getCmdStrK(cmd));
//            }
//        }

//        if(null!=dataInfoQueue&&!dataInfoQueue.isEmpty()){
//            if(null!=dataInfoQueue.peek()){
//                byte[] cmd=dataInfoQueue.poll();
//                index++;
//                BlueToothGattSendMsgManager.getInstance().sendMsgByImg(index,cmd);
//            }
//        }else {
//            dataInfoQueue=null;
//            over();
//        }
//    }


    private int w,h,size;
    private   void splitPacketForMtuByte(byte[] imageDatas){
//        dataInfoQueue=new LinkedList();
        if(!(null == imageDatas)){

            int dataIndex=0;
            int mtu= NewBeeBleConfig.getInstance().getRealMtu();
            Log.i("kankanfasongtupian", "-------------kankanshenmegui:111---555666--999--????????" +mtu+"--"+imageDatas.length);
            Log.i("kankanfasongtupian", "-------------kankanshenmegui:111---555666--99911111--????????" +BleByteUtil.parseByte2HexStr(imageDatas));
            do{
                msgIndex++;
                byte[] currentData;
                if(imageDatas.length-dataIndex <= mtu){
                    currentData = new byte[imageDatas.length-dataIndex];
                    System.arraycopy(imageDatas, dataIndex, currentData, 0, imageDatas.length - dataIndex);

                    dataIndex = imageDatas.length;

                }else {
                    currentData = new byte[mtu];
                    System.arraycopy(imageDatas, dataIndex, currentData, 0,mtu);
                    dataIndex += mtu;

                }
                BlueToothGattSendMsgManager.getInstance().sendMsgByImg(msgIndex,currentData);
                Log.i("kankanfasongtupian", "-------------kankanshenmegui:111---555666----????????" + dataIndex+"---"+ BleByteUtil.parseByte2HexStr(currentData));
//                dataInfoQueue.offer(currentData);
            }while (dataIndex < imageDatas.length);
        }
    }

    public interface Listen{

        public void sendOver(long useTime);
    }

}
