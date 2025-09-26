package com.newbee.ble_lib.manager.image;

import android.graphics.Bitmap;
import android.util.Log;


import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.newbee.ble_lib.util.BleSendImageUtil;
import com.nrmyw.ble_event_lib.bean.BleSendImageEndInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageStartInfoBean;

import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.type.BleSendBitmapQualityType;

import java.util.LinkedList;
import java.util.Queue;

public class BlueToothGattGetBitmapDataThread extends Thread{
    private Listen listen;
    private BleSendImageInfoBean bleSendImageInfoBean;
    private Queue<byte[]> dataInfoQueue=new LinkedList<>();
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

    public void close(){
        isStart=false;
        over(0);
    }


    public boolean isStart(){
        return isStart;
    }


    public int index;
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
            if(null!=dataInfoQueue){
                dataInfoQueue.clear();
            }
            isStart=true;
            newBitMap= BleSendImageUtil.autoScaleBitmap(bleSendImageInfoBean.getBitmap(),bleSendImageInfoBean.getMaxW(),bleSendImageInfoBean.getMaxH());
            w=newBitMap.getWidth();
            h=newBitMap.getHeight();
            if(null==newBitMap){
                listen.sendOver(0);
                return;
            }
            byte[] imageBytes=BleSendImageUtil.bitmap2Bytes(newBitMap,bleSendImageInfoBean.getBitmapQualityType());
            size=imageBytes.length;
            if(null==imageBytes||imageBytes.length==0){
                listen.sendOver(0);
                return;
            }


            sendImageStart();
            splitPacketForMtuByte(imageBytes);

        }catch (Exception e){
            listen.sendOver(0);
            Log.i("kankanshibushizheli","kankanshibushizheli:333"+e.toString());
        }
    }


    private void over(long useTime){
        if(null!=newBitMap&&!newBitMap.isRecycled()){
            newBitMap.recycle();
            newBitMap=null;
        }
        if(null!=bleSendImageInfoBean&&null!=bleSendImageInfoBean.getBitmap()&&!bleSendImageInfoBean.getBitmap().isRecycled()){
            bleSendImageInfoBean=null;
        }
        if(null!=listen){
            listen.sendOver(useTime);
        }
        isStart=false;
    }

    public int getType(){
        try {
            return bleSendImageInfoBean.getType();
        }catch (Exception e){
            return -1;
        }

    }

    public void sendImageStart(){
        BleSendImageStartInfoBean startInfoBean=new BleSendImageStartInfoBean();
        startInfoBean.setW(w);
        startInfoBean.setH(h);
        startInfoBean.setSize(size);
        startInfoBean.setType(bleSendImageInfoBean.getType());
        startInfoBean.setName(bleSendImageInfoBean.getName());
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEND_IMAGE_START,startInfoBean);
//        BleHintEventSubscriptionSubject.getInstance().sendImageStart(w,h,size);

    }

    public void sendImageEnd(long useTime){
        index++;
        BleSendImageEndInfoBean endInfoBean=new BleSendImageEndInfoBean();
        endInfoBean.setW(w);
        endInfoBean.setH(h);
        endInfoBean.setSize(size);
        endInfoBean.setUseTime(useTime);
        endInfoBean.setIndex(index);
        endInfoBean.setType(bleSendImageInfoBean.getType());
        endInfoBean.setName(bleSendImageInfoBean.getName());
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEND_IMAGE_END,endInfoBean);
//        BleHintEventSubscriptionSubject.getInstance().sendImageEnd(w,h,size,useTime,index);
    }




    public void queToSendCmd(){
        if(null!=dataInfoQueue&&!dataInfoQueue.isEmpty()){
            if(null!=dataInfoQueue.peek()){
                byte[] cmd=dataInfoQueue.poll();
                index++;
                BlueToothGattSendMsgManager.getInstance().sendMsgByImg(index,cmd);
            }
        }else {
            dataInfoQueue=null;
            long endTime=System.currentTimeMillis();
            long useTime=endTime-startTime;
            sendImageEnd(useTime);
            over(useTime);
        }
    }


    private int w,h,size;
    private   void splitPacketForMtuByte(byte[] data){
        dataInfoQueue=new LinkedList();
        if(null!=data){
            int index=0;
            do{
                int mtu= NewBeeBleConfig.getInstance().getSendFileMtu();
                byte[] currentData;
                if(data.length- index <= mtu){
                    currentData = new byte[data.length-index];
                    System.arraycopy(data, index, currentData, 0, data.length - index);
                    index = data.length;
                }else {
                    currentData = new byte[mtu];
                    System.arraycopy(data, index, currentData, 0,mtu);
                    index += mtu;
                }
                dataInfoQueue.offer(currentData);
            }while (index < data.length);


        }
    }

    public interface Listen{

        public void sendOver(long useTime);
    }

}
