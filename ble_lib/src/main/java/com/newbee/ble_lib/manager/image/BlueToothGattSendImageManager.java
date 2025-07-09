package com.newbee.ble_lib.manager.image;

import android.graphics.Bitmap;

import com.nrmyw.ble_event_lib.type.BleSendBitmapQualityType;




 public class BlueToothGattSendImageManager {
   private static BlueToothGattSendImageManager sendImageManager;
    private BlueToothGattGetBitmapDataThread getBitmapDataThread;
    private BlueToothGattGetBitmapDataThread.Listen threadListen=new BlueToothGattGetBitmapDataThread.Listen() {
        @Override
        public void sendOver(long useTime) {
            if(null!=getBitmapDataThread){
                getBitmapDataThread.interrupt();
                getBitmapDataThread=null;
            }
            queLast();
        }
    };

    private void queLast(){
        if(null!=lastBitmap){
            getBitmapDataThread=new BlueToothGattGetBitmapDataThread(lastBitmap,lastQualityType,threadListen);
            getBitmapDataThread.start();
            lastBitmap=null;
            lastQualityType=null;
        }
    }

   private BlueToothGattSendImageManager(){
   }

   public static BlueToothGattSendImageManager getInstance(){
       if(null==sendImageManager){
           synchronized (BlueToothGattSendImageManager.class){
               if(null==sendImageManager){
                   sendImageManager=new BlueToothGattSendImageManager();
               }
           }
       }
       return sendImageManager;
   }



   public void close(){


       if(null!=getBitmapDataThread){
           getBitmapDataThread.stop();
           getBitmapDataThread=null;
       }
   }

   public boolean checkNowSendImage(){
        if(null==getBitmapDataThread){
            return false;
        }
        return getBitmapDataThread.isStart();
   }

   public void queToSend(){
       if(null!=getBitmapDataThread){
           getBitmapDataThread.queToSend();
       }
   }





   private Bitmap lastBitmap;
   private BleSendBitmapQualityType lastQualityType;


   public void sendBitMap(Bitmap bitmap, BleSendBitmapQualityType qualityType){
       if(null!=getBitmapDataThread){
            this.lastBitmap=bitmap;
            this.lastQualityType=qualityType;
        }else {
            getBitmapDataThread=new BlueToothGattGetBitmapDataThread(bitmap,qualityType,threadListen);
            getBitmapDataThread.start();
        }

    }




}







