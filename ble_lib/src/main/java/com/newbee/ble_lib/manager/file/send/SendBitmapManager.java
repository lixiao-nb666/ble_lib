package com.newbee.ble_lib.manager.file.send;

import android.graphics.Bitmap;
import android.util.Log;

import com.newbee.ble_lib.manager.child.BlueToothSendStatuManager;
import com.newbee.ble_lib.manager.file.BlueToothGattGetFileDataThread;
import com.newbee.ble_lib.manager.file.BlueToothGattSendFile;
import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.newbee.ble_lib.util.BleSendImageUtil;
import com.newbee.ble_lib.util.hud.HudBleByteUtil;
import com.nrmyw.ble_event_lib.bean.BleSendImageEndInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageStartInfoBean;
import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.util.BleByteUtil;

public class SendBitmapManager implements BlueToothGattSendFile {

    private static SendBitmapManager sendBitmapManager;
    private BleSendImageInfoBean bleSendImageInfoBean;

    private boolean isStart;
    private long startTime;
    public SendBitmapManager(BleSendImageInfoBean bleSendImageInfoBean){
        this.bleSendImageInfoBean=bleSendImageInfoBean;
    }

//    public static SendBitmapManager getInstance(){
//        if(null==sendBitmapManager){
//            synchronized (SendBitmapManager.class){
//                if(null==sendBitmapManager){
//                    sendBitmapManager=new SendBitmapManager();
//                }
//            }
//        }
//        return sendBitmapManager;
//    }

    public int msgIndex;
    private Bitmap newBitMap;

    public void run(){
        if(null==bleSendImageInfoBean||null==bleSendImageInfoBean.getBitmap()||bleSendImageInfoBean.getBitmap().isRecycled()){
            Log.i("kankanshibushizheli","kankanshibushizheli:1111----"+(null==bleSendImageInfoBean)+(null==bleSendImageInfoBean.getBitmap())+(bleSendImageInfoBean.getBitmap().isRecycled()));
            listen.sendOver(0);
            return;
        }
        startTime=System.currentTimeMillis();
//            if(null==dataInfoQueue){
//                dataInfoQueue=new LinkedList<>();
//            }else {
//                dataInfoQueue.clear();
//            }
        isStart=true;
        Log.i("kankantupian","kankantubianzenmhuishi:1111--"+bleSendImageInfoBean.getBitmap().getAllocationByteCount()+"--"+bleSendImageInfoBean.getBitmap().getWidth()+"*"+bleSendImageInfoBean.getBitmap().getHeight());
        if(bleSendImageInfoBean.getBitmapQualityType().getQualityV()==100&&bleSendImageInfoBean.getBitmapQualityType().getZoomScaling()==1f){
            newBitMap =  bleSendImageInfoBean.getBitmap();
        }else {
            newBitMap= BleSendImageUtil.autoScaleBitmap(bleSendImageInfoBean.getBitmap(),bleSendImageInfoBean.getMaxW(),bleSendImageInfoBean.getMaxH(),bleSendImageInfoBean.getBitmapQualityType().getZoomScaling());
        }
        if(null==newBitMap){
            listen.sendOver(0);
            return;
        }
        Log.i("kankantupian","kankantubianzenmhuishi:1111222--"+newBitMap.getByteCount()+"--"+newBitMap.getWidth()+"*"+newBitMap.getHeight());
        w=newBitMap.getWidth();
        h=newBitMap.getHeight();
        byte[] imageBytes=BleSendImageUtil.bitmap2Bytes(newBitMap,bleSendImageInfoBean.getBitmapQualityType());
//            byte[] imageBytes= ImageCompressUtils.compressBitmap(newBitMap,4,Bitmap.CompressFormat.JPEG,100);
        if(null==imageBytes||imageBytes.length==0){
            listen.sendOver(0);
            return;
        }
        msgIndex=0;
        size=imageBytes.length;
        Log.i("kankantupian","kankantubianzenmhuishi:1111333--"+size+"---"+bleSendImageInfoBean.getBitmapQualityType().getQualityV()+"---"+bleSendImageInfoBean.getBitmapQualityType().getZoomScaling());
//            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.NONE,newBitMap);
        if(null==imageBytes||imageBytes.length==0){
            listen.sendOver(0);
            return;
        }
        BlueToothSendStatuManager.getInstance().setSendOverListen(sendOverListen);
        sendImageStart();
        splitPacketForMtuByte(imageBytes);
//            queToSendCmd();
        over();

    }


    private void over(){
        long endTime=System.currentTimeMillis();
        long useTime=endTime-startTime;
        sendImageEnd(useTime);
        isStart=false;
        clear();
    }


    BlueToothGattGetFileDataThread.Listen listen;

    private BlueToothSendStatuManager.SendOverListen sendOverListen=new BlueToothSendStatuManager.SendOverListen() {

        @Override
        public void sendMsgOver(byte[] sendCmd, long startTime, long useTime) {

        }

        @Override
        public void sendFileOver(long startTime, long useTime) {
            Log.i("kankantupian","kankantubianzenmhuis:over1");
            long nowTime=System.currentTimeMillis();
            long allUseTime=startTime-nowTime;
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEND_IMAGE_DATA_END,allUseTime);
            if(null!=listen){
                Log.i("kankantupian","kankantubianzenmhuis:over2");
                listen.sendOver(useTime);
            }
        }
    };

    @Override
    public void setListen(BlueToothGattGetFileDataThread.Listen listen) {
        this.listen=listen;
        run();
    }

    @Override
    public boolean isStart() {
        return isStart;
    }
    @Override
    public void clear(){
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
        msgIndex=0;
        BlueToothGattSendMsgManager.getInstance().readySendFile();
        BleSendImageStartInfoBean startInfoBean=new BleSendImageStartInfoBean();
        startInfoBean.setW(w);
        startInfoBean.setH(h);
        startInfoBean.setSize(size);
        startInfoBean.setType(bleSendImageInfoBean.getType());
        startInfoBean.setName(bleSendImageInfoBean.getName());
        BlueToothGattSendMsgManager.getInstance().sendMsgByFile(msgIndex, HudBleByteUtil.getImageAllByte(w,h,size,true,bleSendImageInfoBean.getType()));
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEND_IMAGE_START,startInfoBean);
    }

    public void sendImageEnd(long useTime){
        msgIndex++;
        BleSendImageEndInfoBean endInfoBean=new BleSendImageEndInfoBean();
        endInfoBean.setW(w);
        endInfoBean.setH(h);
        endInfoBean.setSize(size);
        endInfoBean.setUseTime(useTime);
        endInfoBean.setIndex(msgIndex+1);//最后一帧的内容
        endInfoBean.setType(bleSendImageInfoBean.getType());
        endInfoBean.setName(bleSendImageInfoBean.getName());
        BlueToothGattSendMsgManager.getInstance().sendMsgByFile(msgIndex, HudBleByteUtil.getImageAllByte(w,h,size,false,bleSendImageInfoBean.getType()));
        BlueToothGattSendMsgManager.getInstance().setFileMsgNum(msgIndex+2);//总数
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEND_IMAGE_END,endInfoBean);
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
            Log.i("kankanfasongtupian", "-------------kankanshenmegui:111---555666--99911111--????????" + BleByteUtil.parseByte2HexStr(imageDatas));
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
                BlueToothGattSendMsgManager.getInstance().sendMsgByFile(msgIndex,currentData);
                Log.i("kankanfasongtupian", "-------------kankanshenmegui:111---555666----????????" + dataIndex+"---"+ BleByteUtil.parseByte2HexStr(currentData));
//                dataInfoQueue.offer(currentData);
            }while (dataIndex < imageDatas.length);
        }
    }


}
