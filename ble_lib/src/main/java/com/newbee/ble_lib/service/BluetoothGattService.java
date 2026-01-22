package com.newbee.ble_lib.service;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import com.newbee.ble_lib.NewBeeBleManager;
import com.newbee.ble_lib.base.BaseService;
import com.newbee.ble_lib.config.BleManagerConfig;
import com.newbee.ble_lib.manager.child.BleConnectManager;
import com.newbee.ble_lib.manager.child.BlueToothSendStatuManager;
import com.newbee.ble_lib.manager.file.BlueToothGattSendFileManager;
import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.newbee.ble_lib.service.event.BleDelayEventSubscriptionSubject;
import com.newbee.ble_lib.service.event.BleDelayObserver;
import com.newbee.ble_lib.service.event.BleDelayType;
import com.newbee.ble_lib.util.BleConnectStatuUtil;
import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.bean.BleSendFileInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendOtaInfoBean;
import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;
import com.nrmyw.ble_event_lib.send.BleEventObserver;
import com.nrmyw.ble_event_lib.send.BleEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventObserver;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;



public class BluetoothGattService extends BaseService {



    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                BluetoothGattServiceMsgType msgType=BluetoothGattServiceMsgType.values()[msg.what];

                switch (msgType){
                    case INIT_BLE:

                        BleConnectManager.getInstance().havePermissionInitBle(getBaseContext(),getPackageManager());
                        autoConnectDevice(AutoConnectType.INIT);
                        break;
                    case SCAN_BLE:
                        BleConnectManager.getInstance().startSearchBLE();
                        toConnectOldDevice();
                        break;
                    case DISCONNECT_BLE:
                        BleConnectManager.getInstance().disconnect();
                        break;
                    case SEND_CMD:
                        BlueToothGattSendMsgManager.getInstance().sendMsgByCmd((byte[]) msg.obj);
                        break;
                    case SEND_IMAGE:
                        BlueToothGattSendFileManager.getInstance().sendBitMap((BleSendImageInfoBean) msg.obj);
                        break;
                    case SEND_FILE:

                        BlueToothGattSendFileManager.getInstance().sendFile((BleSendFileInfoBean) msg.obj);
                        break;
                    case SEND_OTA:

                        BlueToothGattSendFileManager.getInstance().sendOta((BleSendOtaInfoBean) msg.obj);
                        break;

                    case SEND_CMD_BY_BYTES_INDEX:
                        BlueToothGattSendMsgManager.getInstance().sendMsgByFile(msg.arg1, (byte[]) msg.obj);
                        break;
                    case CLEAR_MSG:
                        BlueToothGattSendMsgManager.getInstance().clear();
                        break;
                    case CLEAR_INDEX_MSG:
                        BlueToothGattSendMsgManager.getInstance().clearIndexMsg();
                        break;
                    case DELAY:
                        BleDelayType bleDelayType=BleDelayType.values()[msg.arg1];
                        selectDelayTypeToDo(bleDelayType);
                        break;
                }
                BleStatu bleStatu=BleStatu.USER_DO;
                bleStatu.setStrId(msgType.getStrId());
                BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(bleStatu);
            }catch (Exception e){
                BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR, "BluetoothGattService: handler:"+msg.what+"---"+e.toString());
            }
        }
    };

    private void selectDelayTypeToDo(BleDelayType delayType){
        switch (delayType){
            case SEND_BLE_CMD_BYTES:
                BlueToothSendStatuManager.getInstance().noticeOtherNowCanSned();
                break;
        }
    }



    private void autoConnectDevice(AutoConnectType autoConnectType){
        handler.removeCallbacks(autoConnectRunnable);
        handler.removeCallbacks(bleDisConnectRunnable);
        handler.removeCallbacks(tryToConnectOldDeviceRunnable);
        if(null==autoConnectType){
            autoConnectType=AutoConnectType.INIT;
        }
        long needTime=0;
        switch (autoConnectType){
            case INIT:
                needTime=BleManagerConfig.BLE_AUTO_CONNECT_TIME;
//                handler.postDelayed(autoConnectRunnable, BleManagerConfig.BLE_AUTO_CONNECT_TIME);
                break;
            case DISCONNECT:
                needTime=BleManagerConfig.BLE_DISCONNECT_RECONNECT_TIME;
//                handler.postDelayed(autoConnectRunnable,BleManagerConfig.BLE_DISCONNECT_RECONNECT_TIME);
                break;
            case CONNECT_ERR:
                needTime=BleManagerConfig.BLE_CONNECT_ERR_RECONNECT_TIME;
                break;
            case CONNECTING:
                needTime=BleManagerConfig.BLE_CONNECTING_WAIT_TIME;
                break;

        }
        handler.postDelayed(autoConnectRunnable,needTime);


    }

//    private void removeAutoConnectDevice(){
//        handler.removeCallbacks(autoConnectRunnable);
//    }

//    boolean isDisConnectReinit;






//    private void nowConnectIngWait(){
//        handler.removeCallbacks(autoConnectRunnable);
//        handler.postDelayed(autoConnectRunnable,BleManagerConfig.BLE_CONNECTING_WAIT_TIME);
//    }

    private void cancelAutoConnect(){
        handler.removeCallbacks(autoConnectRunnable);
    }

//    private void nowIsDisConnect(){
//
//        if(!isDisConnectReinit){
//            isDisConnectReinit=true;
//        }
//
//    }


    private void toConnectOldDevice(){
        handler.removeCallbacks(tryToConnectOldDeviceRunnable);
        handler.postDelayed(tryToConnectOldDeviceRunnable,BleManagerConfig.BLE_CONNECT_OLD_WAIT_TIME);
    }

    private void cancelConnectOldDevice(){
        handler.removeCallbacks(tryToConnectOldDeviceRunnable);
    }



    private BleStatuEventObserver bleStatuEventObserver=new BleStatuEventObserver() {
        @Override
        public void sendBleStatu(BleStatu bleStatu, Object... objects) {

                switch (bleStatu){
                    case DISCONNECTED:
//                        nowIsDisConnect();

                        autoConnectDevice(AutoConnectType.DISCONNECT);
                        break;
                    case CONNECTING_ERR:
                       autoConnectDevice(AutoConnectType.CONNECT_ERR);
                        break;
                    case CONNECTING:
                        autoConnectDevice(AutoConnectType.CONNECTING);
                        cancelConnectOldDevice();
//                        nowConnectIngWait();
                        break;
                    case CONNECTED:
                        cancelConnectOldDevice();
                        cancelAutoConnect();
                        break;
                    case SENDING_DATA:
                        handler.removeCallbacks(bleDisConnectRunnable);
                        handler.postDelayed(bleDisConnectRunnable,BleManagerConfig.BLE_CHECK_DATA_SEND_TIME);
                        break;
                    case CAN_SEND_DATA:
                        handler.removeCallbacks(bleDisConnectRunnable);
                        break;

                }
        }
    };


    @Override
    public void init() {
        BleDelayEventSubscriptionSubject.getInstance().attach(bleDelayObserver);
        BleStatuEventSubscriptionSubject.getInstance().attach(bleStatuEventObserver);
        BleEventSubscriptionSubject.getInstance().attach(bleEventObserver);
        NewBeeBleManager.getInstance().setBleEventObserver(bleEventObserver);
    }



    @Override
    public void start(Intent intent, int flags, int startId) {

    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            if(NewBeeBleManager.getInstance().getNotificationId()==0||null==NewBeeBleManager.getInstance().getNotification()){
//                return super.onStartCommand(intent, flags, startId);
//
//            }else {
//                startForeground(NewBeeBleManager.getInstance().getNotificationId(), NewBeeBleManager.getInstance().getNotification());//服务前台化只能使用startForeground()方法,不能使用 notificationManager.notify(1,notification); 这个只是启动通知使用的,使用这个方法你只需要等待几秒就会发现报错了
//                return START_STICKY;
//            }
//
//
//        }else {
//            return super.onStartCommand(intent, flags, startId);
//        }
////
//    }


    @Override
    public void close() {
        handler.removeCallbacksAndMessages(null);
        BleConnectManager.getInstance().close();
        BleDelayEventSubscriptionSubject.getInstance().detach(bleDelayObserver);
        BleEventSubscriptionSubject.getInstance().detach(bleEventObserver);
        BleStatuEventSubscriptionSubject.getInstance().detach(bleStatuEventObserver);
    }



   private  BleEventObserver bleEventObserver=new BleEventObserver() {
        @Override
        public void havePermissionInitBle() {
            handler.sendEmptyMessage(BluetoothGattServiceMsgType.INIT_BLE.ordinal());
        }

        @Override
        public void startSearchBle() {

            handler.sendEmptyMessage(BluetoothGattServiceMsgType.SCAN_BLE.ordinal());

        }

        @Override
        public void disconnectedBle() {
            handler.sendEmptyMessage(BluetoothGattServiceMsgType.DISCONNECT_BLE.ordinal());
        }

        @Override
        public void sendCmd(byte[] bytes) {
            Message msg=new Message();
            msg.what=BluetoothGattServiceMsgType.SEND_CMD.ordinal();
            msg.obj=bytes;
            handler.sendMessage(msg);

        }

       @Override
       public void sendBytesIndexCmd(int index, byte[] bytes) {
           Message msg=new Message();
           msg.what=BluetoothGattServiceMsgType.SEND_CMD_BY_BYTES_INDEX.ordinal();
           msg.obj=bytes;
           msg.arg1=index;
           handler.sendMessage(msg);
       }

       @Override
        public void sendImage(BleSendImageInfoBean sendImageInfoBean) {
            Message msg=new Message();
            msg.what=BluetoothGattServiceMsgType.SEND_IMAGE.ordinal();
            msg.obj=sendImageInfoBean;
            handler.sendMessage(msg);
        }

       @Override
       public void sendFile(BleSendFileInfoBean sendFileInfoBean) {

           Message msg=new Message();
           msg.what=BluetoothGattServiceMsgType.SEND_FILE.ordinal();
           msg.obj=sendFileInfoBean;
           handler.sendMessage(msg);
       }

       @Override
       public void sendOta(BleSendOtaInfoBean sendOtaInfoBean) {
           Message msg=new Message();
           msg.what=BluetoothGattServiceMsgType.SEND_OTA.ordinal();
           msg.obj=sendOtaInfoBean;
           handler.sendMessage(msg);
       }

       @Override
       public void clearMsg() {
           Message msg=new Message();
           msg.what=BluetoothGattServiceMsgType.CLEAR_MSG.ordinal();

           handler.sendMessage(msg);
       }

       @Override
       public void clearIndexMsg() {
           Message msg=new Message();
           msg.what=BluetoothGattServiceMsgType.CLEAR_INDEX_MSG.ordinal();
           handler.sendMessage(msg);
       }
   };


    private BleDelayObserver bleDelayObserver=new BleDelayObserver() {
        @Override
        public void delayDo(BleDelayType delayType, long needDelayTime) {
            handler.removeMessages(BluetoothGattServiceMsgType.DELAY.ordinal());
            Message msg=new Message();
            msg.what=BluetoothGattServiceMsgType.DELAY.ordinal();
            msg.arg1=delayType.ordinal();
            handler.sendMessageDelayed(msg,needDelayTime);
        }
    };




    private Runnable autoConnectRunnable=new Runnable() {
        @Override
        public void run() {
            if(!NewBeeBleConfig.getInstance().isAutoConnect()){
                return;
            }
            BleDeviceBean bleDeviceBean= BleConnectStatuUtil.getInstance().getNowUseBleDevice();
            boolean isConnect=BleConnectStatuUtil.getInstance().isConnect();
            if(null!=bleDeviceBean&&isConnect){
                return;
            }
//            long nowTime=System.currentTimeMillis();
//            if(isDisConnectReinit){
//                isDisConnectReinit=false;
//                bleEventObserver.havePermissionInitBle();
//            }else {
//
//            }
            if(BleConnectManager.getInstance().checkCanScan()){
                bleEventObserver. startSearchBle();
            }else {
                bleEventObserver.havePermissionInitBle();
            }
            autoConnectDevice(AutoConnectType.INIT);
        }
    };

    private Runnable bleDisConnectRunnable=new Runnable() {
        @Override
        public void run() {
//            nowIsDisConnect();
            BleConnectStatuUtil.getInstance().sendDisconnected();
        }
    };

    private Runnable tryToConnectOldDeviceRunnable =new Runnable() {
        @Override
        public void run() {
            BleConnectManager.getInstance().checkAndToConnectOldDevice();
        }
    };


    private enum AutoConnectType{
        INIT,
        DISCONNECT,
        CONNECT_ERR,
        CONNECTING,
    }

}
