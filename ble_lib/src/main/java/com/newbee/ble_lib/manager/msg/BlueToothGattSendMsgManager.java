package com.newbee.ble_lib.manager.msg;




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





   public void sendMsgByCmd(byte[] cmd){

       BlueToothGattMsgManager.getInstance().addMsg(cmd);
       BlueToothGattMsgManager.getInstance().queMsg();
    }

   public void sendMsgByImg(int index,byte[] cmd){

       BlueToothGattMsgManager.getInstance().addMsgByImage(index,cmd);
       BlueToothGattMsgManager.getInstance().queMsg();
   }


   public void readySendImage(){
       BlueToothGattMsgManager.getInstance().readySendImage();
   }

    public void setImageMsgNum(int imageMsgNum){
       BlueToothGattMsgManager.getInstance().setNowCanSendImageNumb(imageMsgNum);
    }







}
