package com.newbee.ble_lib.manager.image;

import android.graphics.Bitmap;
import android.util.Log;

import com.newbee.ble_lib.event.statu.BleStatu;
import com.newbee.ble_lib.event.statu.BleStatuEventSubscriptionSubject;

import java.util.Timer;
import java.util.TimerTask;


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
   private BitmapQualityType lastQualityType;


   public void sendBitMap(Bitmap bitmap, BitmapQualityType qualityType){
       Log.i("kankanindex","kankanindex:11111111111--");

       if(null!=getBitmapDataThread){
            Log.i("kankanindex","kankanindex:11111111111--------1111");
            this.lastBitmap=bitmap;
            this.lastQualityType=qualityType;
        }else {
            Log.i("kankanindex","kankanindex:11111111111--------2222");
            getBitmapDataThread=new BlueToothGattGetBitmapDataThread(bitmap,qualityType,threadListen);
            getBitmapDataThread.start();
        }

    }




}







