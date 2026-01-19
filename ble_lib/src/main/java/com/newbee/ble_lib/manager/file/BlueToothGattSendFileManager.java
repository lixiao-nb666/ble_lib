package com.newbee.ble_lib.manager.file;

import android.util.Log;


import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.nrmyw.ble_event_lib.bean.BleSendFileInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendOtaInfoBean;


public class BlueToothGattSendFileManager {
   private static BlueToothGattSendFileManager sendImageManager;
    private BlueToothGattGetFileDataThread getFileDataThread;
    private BlueToothGattGetFileDataThread.Listen threadListen=new BlueToothGattGetFileDataThread.Listen() {
        @Override
        public void sendOver(long useTime) {
            Log.i("kankantupian","kankantubianzenmhuis:over3");
            if(null!=getFileDataThread){
                getFileDataThread.interrupt();
                getFileDataThread=null;
                Log.i("kankantupian","kankantubianzenmhuis:over4");
            }
//            queLast();
        }
    };

    private void queLast(){
//        if(null!=lastBleSendImageInfoBean){
//            getBitmapDataThread=new BlueToothGattGetBitmapDataThread(lastBleSendImageInfoBean,threadListen);
//            getBitmapDataThread.start();
//        }
    }

   private BlueToothGattSendFileManager(){
   }

   public static BlueToothGattSendFileManager getInstance(){
       if(null==sendImageManager){
           synchronized (BlueToothGattSendFileManager.class){
               if(null==sendImageManager){
                   sendImageManager=new BlueToothGattSendFileManager();
               }
           }
       }
       return sendImageManager;
   }



   public void close(){
       if(null!=getFileDataThread){
           getFileDataThread.interrupt();
           getFileDataThread=null;
       }
   }

//   public boolean checkNowSendImage(){
//        if(null==getBitmapDataThread){
//            return false;
//        }
//        return getBitmapDataThread.isStart();
//   }
//
//   public void queToSendCmd(){
//       if(null!=getBitmapDataThread){
//           getBitmapDataThread.queToSendCmd();
//       }
//   }



//    private Map<String,BleSendImageInfoBean>bleSendImageInfoBeanMap=new HashMap<>();
//
//  private BleSendImageInfoBean lastBleSendImageInfoBean;

    public void sendFile(BleSendFileInfoBean bleSendFileInfoBean){
        Log.i("tag","kaishi1111111111:1");
        if(null==getFileDataThread){
            Log.i("tag","kaishi1111111111:2");
            getFileDataThread=new BlueToothGattGetFileDataThread(bleSendFileInfoBean,threadListen);
            getFileDataThread.start();
        }
    }

    public void sendOta(BleSendOtaInfoBean bleSendOtaInfoBean){
        Log.i("tag","kaishi1111111111:1");
        if(null==getFileDataThread){
            Log.i("tag","kaishi1111111111:2");
            getFileDataThread=new BlueToothGattGetFileDataThread(bleSendOtaInfoBean,threadListen);
            getFileDataThread.start();
        }
    }

   public void sendBitMap(BleSendImageInfoBean bleSendImageInfoBean){
        if(null==getFileDataThread){
            getFileDataThread=new BlueToothGattGetFileDataThread(bleSendImageInfoBean,threadListen);
            getFileDataThread.start();
        }else {
            if(bleSendImageInfoBean.getType()==0){
                getFileDataThread.interrupt();
                getFileDataThread=null;
                BlueToothGattSendMsgManager.getInstance().clearIndexMsg();
                getFileDataThread=new BlueToothGattGetFileDataThread(bleSendImageInfoBean,threadListen);
                getFileDataThread.start();
            }
        }


//       if(null!=getBitmapDataThread){
//           Log.i("kaishifasongtupianle","kaishifasongtupianle-zhiling:5555");
//            if(getBitmapDataThread.isStart()&&getBitmapDataThread.getType()==0&&bleSendImageInfoBean.getType()!=0){
//                return;
//            }
//           getBitmapDataThread.interrupt();
//           getBitmapDataThread=null;
//           Log.i("kaishifasongtupianle","kaishifasongtupianle-zhiling:3333");
//       }

//       getBitmapDataThread=new BlueToothGattGetBitmapDataThread(bleSendImageInfoBean,threadListen);
//       getBitmapDataThread.start();
       Log.i("kaishifasongtupianle","kaishifasongtupianle-zhiling:4444");
//       if(null==getBitmapDataThread){
//
//
//        }else {
////           if(null!=bleSendImageInfoBean&&bleSendImageInfoBean.getType()==0){
////               this.lastBleSendImageInfoBean=bleSendImageInfoBean;
////           }
//        }

    }




}







