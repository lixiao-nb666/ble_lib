package com.newbee.ble_lib.manager.msg;


import android.util.Log;

import com.nrmyw.ble_event_lib.util.BleByteUtil;

public class BlueToothGattSendMsgManager {
   private static BlueToothGattSendMsgManager blueToothGattSendManager;





   private BlueToothGattSendMsgManager(){

   }

   public static BlueToothGattSendMsgManager getInstance(){
       if(null==blueToothGattSendManager){
           synchronized (BlueToothGattSendMsgManager.class){
               if(null==blueToothGattSendManager){
                   blueToothGattSendManager=new BlueToothGattSendMsgManager();
               }
           }
       }
       return blueToothGattSendManager;
   }

   public void close(){
       BlueToothGattSendMsgManager.getInstance().close();
       blueToothGattSendManager=null;
   }

   public void clear(){
       BlueToothGattMsgManager.getInstance().clear();
   }

   public void setNowCanSend(){

       BlueToothGattMsgManager.getInstance().queMsg();
   }


    public void setFileNowReSend(){
        BlueToothGattMsgManager.getInstance().reSendFileMsg();

    }


   public void sendMsgByCmd(byte[] cmd){
       Log.i("tag","发送 ===  :发送成功---checkReturnToSend 3311-222112:count"+BleByteUtil.parseByte2HexStr(cmd));
       BlueToothGattMsgManager.getInstance().addMsg(cmd);
       BlueToothGattMsgManager.getInstance().queMsg();
    }

   public void sendMsgByFile(int index,byte[] cmd){
       Log.i("tag","发送 ===  :发送成功---checkReturnToSend 3311-222111:count"+index+"---"+"--"+cmd.length+"--"+BleByteUtil.parseByte2HexStr(cmd));
       BlueToothGattMsgManager.getInstance().addMsgByFile(index,cmd);
       BlueToothGattMsgManager.getInstance().queMsg();
   }


   public void readySendFile(){
       BlueToothGattMsgManager.getInstance().readySendFile();
   }

    public void setFileMsgNum(int imageMsgNum){
       BlueToothGattMsgManager.getInstance().setNowCanSendFileNumb(imageMsgNum);
    }







}
