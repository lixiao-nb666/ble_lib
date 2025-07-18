package com.newbee.ble_lib.manager.msg;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;


import com.nrmyw.ble_event_lib.util.BleByteUtil;

import java.util.Map;


  class BlueToothGattMsgManager {
   private static BlueToothGattMsgManager sendMsgManager;
   private Map<String,byte[]>msgMq=new ArrayMap<>();
   private Map<String,byte[]>imageMsgMq=new ArrayMap<>();
   private Listen listen;

   private BlueToothGattMsgManager(){

   }

   public static BlueToothGattMsgManager getInstance(){
       if(null==sendMsgManager){
           synchronized (BlueToothGattMsgManager.class){
               if(null==sendMsgManager){
                  sendMsgManager=new BlueToothGattMsgManager();
               }
           }
       }
       return sendMsgManager;
   }

   public void init(Listen listen){
       this.listen=listen;
   }

   public void clear(){
       msgMq.clear();
       imageMsgMq.clear();
   }

   private void listenSendMsg(String kStr,byte[] msg){
       if(null!=listen){
           listen.canSendMsg(kStr,msg);
       }
   }

   private void listenSendImageMsg(int index,byte[] msg){
       if(null!=listen){
           listen.canSendImageMsg(index,msg);
       }
   }

   public void close(){
       msgMq.clear();
       sendMsgManager=null;
   }

   public void addMsg(byte[] msg){
       String kStr= BleByteUtil.getCmdStrK(msg);
       msgMq.put(kStr,msg);
       Log.i("kankanfasongtupian","-------------kankanshenmegui:111---"+kStr);
   }

      public void addMsgByImage(int index,byte[] msg){
          imageMsgMq.put(index+"",msg);
      }



   public void queMsg(){
        if(!queImageMsg()){
            queCmdMsg();
        }
   }

   private boolean queImageMsg(){
       if(imageMsgMq.size()>0){
           int index=getImageFristK();

           byte[] cmd=imageMsgMq.get(index+"");
           if(null==cmd||cmd.length==0){
               imageMsgMq.clear();
                return false;
           }

           listenSendImageMsg(index,cmd);
           return true;
       }
       return false;
   }

   private void queCmdMsg(){
       if(msgMq.size()>0){
           String kStr=getFristKStr();
           if(TextUtils.isEmpty(kStr)){
               msgMq.clear();
               return;
           }
           byte[] cmd=msgMq.get(kStr);
           if(null==cmd||cmd.length==0){
               msgMq.clear();
                return;
           }
           listenSendMsg(kStr,cmd);
       }
   }


   public void removeMsg(String kStr){
       msgMq.remove(kStr);
//       LG.i("kankanshujuqingkongmei:"+msgMq.size());
   }

      public void removeImageMsg(String kStr){
          imageMsgMq.remove(kStr);
//       LG.i("kankanshujuqingkongmei:"+msgMq.size());
      }



   private String getFristKStr(){
       for(String str:msgMq.keySet()){
           if(!TextUtils.isEmpty(str)){
               return str;
           }
       }
       return null;
   }

      private int getImageFristK(){
            int index=1000;
          for(String str:imageMsgMq.keySet()){
              if(TextUtils.isEmpty(str)){
                continue;
              }
              int nowIndex=Integer.valueOf(str);
              if(nowIndex<index){
                  index=nowIndex;
              }
          }

          return index;
      }

   public interface Listen{

       public void canSendMsg(String kStr,byte[] msg);

       public void canSendImageMsg(int index,byte[] msg);

   }

}
