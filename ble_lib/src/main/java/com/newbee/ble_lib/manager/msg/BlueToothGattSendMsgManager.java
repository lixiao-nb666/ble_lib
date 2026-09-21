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
       BlueToothGattMsgManager.getInstance().close();
       blueToothGattSendManager=null;
   }

   public int getMsgNumb(){
       return BlueToothGattMsgManager.getInstance().getMsgNumb();
   }

    public int getFileMsgNumb(){
        return BlueToothGattMsgManager.getInstance().getFileMsgNumb();
    }


   public void clear(){
       BlueToothGattMsgManager.getInstance().clear();
   }

   public void clearIndexMsg(){
       BlueToothGattMsgManager.getInstance().clearFileMsg();
   }

   public void setNowCanSend(){

       BlueToothGattMsgManager.getInstance().queMsg();
   }


    public void setFileNowReSend(){
        BlueToothGattMsgManager.getInstance().reSendFileMsg();

    }


   public void sendMsgByCmd(byte[] cmd){
       BlueToothGattMsgManager.getInstance().addMsg(cmd);
       BlueToothGattMsgManager.getInstance().queMsg();
    }

    public void sendMsgByCmd(String key,byte[] cmd){
        BlueToothGattMsgManager.getInstance().addMsg(key,cmd);
        BlueToothGattMsgManager.getInstance().queMsg();
    }

   public void sendMsgByFile(int index,byte[] cmd){
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
