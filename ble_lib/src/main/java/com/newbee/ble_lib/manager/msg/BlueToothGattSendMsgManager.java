package com.newbee.ble_lib.manager.msg;


import android.util.Log;

import com.newbee.ble_lib.manager.child.BlueToothGattManager;
import com.nrmyw.ble_event_lib.util.BleByteUtil;


public class BlueToothGattSendMsgManager {
   private static BlueToothGattSendMsgManager blueToothGattSendManager;




   private BlueToothGattMsgManager.Listen msgListen=new BlueToothGattMsgManager.Listen() {
       @Override
       public void canSendMsg(String kStr, byte[] msg) {
            Log.i("nengfasongma","nengfasongshima???1");
            if(BlueToothGattManager.getInstance().isNowCanSend()){
                Log.i("nengfasongma","nengfasongshima???2");
                BlueToothGattMsgManager.getInstance().removeMsg(kStr);
                BlueToothGattManager.getInstance().queSendCmd(msg);
            }
       }

       @Override
       public void canSendImageMsg(int index, byte[] msg) {
           Log.i("nengfasongma","nengfasongshima???3");

           if(BlueToothGattManager.getInstance().isNowCanSend()){
               Log.i("nengfasongma","nengfasongshima???4"+"---"+index+"-------"+ BleByteUtil.parseByte2HexStr(msg));
               BlueToothGattMsgManager.getInstance().removeImageMsg(index+"");
               BlueToothGattManager.getInstance().queSendCmd(msg);
           }
       }
   };

   private BlueToothGattSendMsgManager(){
       BlueToothGattMsgManager.getInstance().init(msgListen);
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




//   private void sendMsg(String keyTitle,byte[] cmd){
//
//       if(BlueToothGattManager.getInstance().isNowCanSend()){
////           LG.i("发送 ==    queToSend-----sendMsg:"+keyTitle);
//           String strK= BleByteUtil.getCmdStrK(cmd);
//           msgListen.canSendMsg(strK,cmd);
//       }else {
//
//
//
//       }
//   }

   public void sendMsgByCmd(byte[] cmd){

       BlueToothGattMsgManager.getInstance().addMsg(cmd);
       BlueToothGattMsgManager.getInstance().queMsg();
    }

   public void sendMsgByImg(int index,byte[] cmd){
       Log.i("kankanfasongtupian","-------------kankanshenmegui:"+BleByteUtil.parseByte2HexStr(cmd));
       BlueToothGattMsgManager.getInstance().addMsgByImage(index,cmd);
       BlueToothGattMsgManager.getInstance().queMsg();
   }











}
