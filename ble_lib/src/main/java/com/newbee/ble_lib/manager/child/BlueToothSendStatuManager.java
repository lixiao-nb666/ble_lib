package com.newbee.ble_lib.manager.child;

import android.util.Log;

import com.newbee.ble_lib.manager.file.send.CheckRetrunByteAndToDoType;
import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.newbee.ble_lib.service.event.BleDelayEventSubscriptionSubject;
import com.newbee.ble_lib.service.event.BleDelayType;
import com.newbee.ble_lib.util.BytesCheckUtil;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.util.BleByteUtil;

import java.nio.charset.CharacterCodingException;

public class BlueToothSendStatuManager {

    private static BlueToothSendStatuManager manager;
    private final String tag="BlueToothMsgManager >>>";
    private BlueToothSendStatuManager(){}

    public static BlueToothSendStatuManager getInstance(){
        if(null==manager){
            synchronized (BlueToothSendStatuManager.class){
                if(null==manager){
                    manager=new BlueToothSendStatuManager();
                }
            }
        }
        return manager;
    }

    private boolean nowCanSend;
//    private long lastSendTime;
    private long sendTime;
    private byte[] lastSendBytes;
    private long waitTime=0;
//    private byte[] checkRetrunCanSendCmdbytes,checkRetrunReSendCmdbytes,canStartSendCmdbytes,sendEndCmdbytes;
//    //0.数据还没来，1，继续，2重新发
//    private ChechRetrunByteAndToDoType toDoType=ChechRetrunByteAndToDoType.NONE;


    private SendOverListen sendOverListen;

//    private boolean needWaitRetrunToSend;
    private RetrunByteListen retrunByteListen;
    private CheckRetrunByteAndToDoType toDoType;
    private int resendNumb;

     void initData(){
        nowCanSend=false;
         sendOverListen=null;
        sendTime=0;
        lastSendBytes=null;
        waitTime=0;
//         needWaitRetrunToSend=false;
        retrunByteListen=null;
         toDoType=CheckRetrunByteAndToDoType.NONE;
        resendNumb=0;

//        checkRetrunCanSendCmdbytes=null;
//        checkRetrunReSendCmdbytes=null;
//        toDoType=ChechRetrunByteAndToDoType.NONE;
    }


    public void initOk(){
        nowCanSend=true;
        sendOverListen=null;
        sendTime=0;
        lastSendBytes=null;
        waitTime=0;
//        needWaitRetrunToSend=false;
        retrunByteListen=null;
        toDoType=CheckRetrunByteAndToDoType.NONE;
        resendNumb=0;

    }


    public void setFileCheckOver(long startSendFileTime,long sendFileUseTime){


        waitTime=0;
        if(null!=sendOverListen){
            sendOverListen.sendFileOver(startSendFileTime,sendFileUseTime);
            sendOverListen=null;
        }
//        needWaitRetrunToSend=false;
        retrunByteListen=null;
        resendNumb=0;
        toDoType=CheckRetrunByteAndToDoType.NONE;
    }

    public boolean isNowCanSend(){
        if(nowCanSend){
            return nowCanSend;
        } else {
            long nowTime=System.currentTimeMillis();
            if(sendTime!=0&&nowTime-sendTime>3000){
                nowCanSend=true;
            }
            return nowCanSend;
        }
    }


    public void sendBytyes(byte[] bytes){
        if(BlueToothGattManager.getInstance().queSendCmd(bytes)){
            nowCanSend=false;
            sendTime=System.currentTimeMillis();
            lastSendBytes=bytes;
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SENDING_DATA,bytes);
        }else {
            nowCanSend=true;
            lastSendBytes=null;
            BlueToothGattSendMsgManager.getInstance().setNowCanSend();
        }
    }


    public void sendBytesIsOk(int status){
        long sendOkTime=System.currentTimeMillis();
        Log.i(tag,"发送 ===  :发送成功"+status+"---用时为:"+(sendOkTime-sendTime));
        if(null!=sendOverListen){
            sendOverListen.sendMsgOver(lastSendBytes,sendTime,sendOkTime-sendOkTime);
        }

        if(isNeedToCheckRetrun()){
            nowCanSend=true;
            checkReturnToSend();
        }else if(waitTime>0&&waitTime<=50){
            if(sendOkTime>waitTime){
                Log.w(tag,"BluetoothAdapter  initialized  111:1112");
                noticeOtherNowCanSned();
                Log.w(tag,"BluetoothAdapter  initialized  111:1111");
                BleDelayEventSubscriptionSubject.getInstance().delayDo(BleDelayType.SEND_BLE_CMD_BYTES,waitTime-sendOkTime);
            }
        }else {
            Log.w(tag,"BluetoothAdapter  initialized  111:1113");
            noticeOtherNowCanSned();
        }
        Log.i(tag,"收到指令没111");
    }

    public void noticeOtherNowReSned(){
        nowCanSend=true;
        BlueToothGattSendMsgManager.getInstance().setFileNowReSend();
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CAN_SEND_DATA);
    }


    public void noticeOtherNowCanSned(){
        nowCanSend=true;
        BlueToothGattSendMsgManager.getInstance().setNowCanSend();
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CAN_SEND_DATA);
    }






    public void getRetrunBytes(byte[] bytes){
        if(null!=retrunByteListen){
            Log.i("tag","发送 ===  :发送成功---checkReturnToSend 3322:count"+ BleByteUtil.parseByte2HexStr(bytes));
            retrunByteListen.getRetrunByte(bytes);
        }
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RETRUN_BYTES,bytes);
    }

    private boolean isNeedToCheckRetrun(){
         if(null!=retrunByteListen){
                return true;
         }
         return false;
    }



     public void setToDoType(CheckRetrunByteAndToDoType toDoType){
         this.toDoType=toDoType;
         checkReturnToSend();
     }


    private void checkReturnToSend(){
        Log.i(tag,"发送 ===  :发送成功---checkReturnToSend 11:"+toDoType);
         if(!nowCanSend||null==toDoType||toDoType==CheckRetrunByteAndToDoType.NONE){
             return;
         }
        Log.i(tag,"发送 ===  :发送成功---checkReturnToSend 22:");
         switch (toDoType){
             case READY_OK:
             case SEND_ERR:
                 noticeOtherNowCanSned();
                 break;
             case RE_SEND:
                 noticeOtherNowReSned();
                 break;
             default:
                 noticeOtherNowCanSned();
                 break;

         }
        toDoType= CheckRetrunByteAndToDoType.NONE;
    }











    public void setWaitTime(long waitTime){
        if(waitTime>50){
            waitTime=50;
        }
        this.waitTime=waitTime;
        Log.w(tag,"BluetoothAdapter  initialized  111:1110");
    }




    public void needRetrunByteListenDo(boolean firstNeedWaitRetrunToSend,RetrunByteListen retrunByteListen) {
//         this.needWaitRetrunToSend=firstNeedWaitRetrunToSend;
         this.retrunByteListen = retrunByteListen;
    }


    public void setSendOverListen(SendOverListen sendOverListen){
         this.sendOverListen=sendOverListen;
    }


    public interface RetrunByteListen{

         public void getRetrunByte(byte[] retrunBytes);

    }



    public interface SendOverListen{

         public void sendMsgOver(byte[] sendCmd,long startTime,long useTime);
         public void sendFileOver(long startTime,long useTime);
    }




}

