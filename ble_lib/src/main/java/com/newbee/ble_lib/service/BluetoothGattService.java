package com.newbee.ble_lib.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.newbee.ble_lib.R;
import com.newbee.ble_lib.base.BaseService;

import com.newbee.ble_lib.manager.child.BleConnectManager;

import com.newbee.ble_lib.manager.image.BlueToothGattSendImageManager;
import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.newbee.ble_lib.util.BleConnectStatuUtil;
import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageInfoBean;
import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;
import com.nrmyw.ble_event_lib.send.BleEventObserver;
import com.nrmyw.ble_event_lib.send.BleEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventObserver;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.type.BleSendBitmapQualityType;


public class BluetoothGattService extends BaseService implements BleEventObserver {



    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                BluetoothGattServiceMsgType msgType=BluetoothGattServiceMsgType.values()[msg.what];

                switch (msgType){
                    case INIT_BLE:
                        BleConnectManager.getInstance().havePermissionInitBle(getBaseContext(),getPackageManager());
                        autoConnectDevice();
                        break;
                    case SCAN_BLE:
                        BleConnectManager.getInstance().startSearchBLE();
                        break;
                    case DISCONNECT_BLE:
                        BleConnectManager.getInstance().disconnect();
                        break;
                    case SEND_CMD:
                        BlueToothGattSendMsgManager.getInstance().sendMsgByCmd((byte[]) msg.obj);
                        break;
                    case SEND_IMAGE:
                        BlueToothGattSendImageManager.getInstance().sendBitMap((BleSendImageInfoBean) msg.obj);
                        break;
                    case SEND_CMD_BY_IMAGE_INDEX:
                        BlueToothGattSendMsgManager.getInstance().sendMsgByImg(msg.arg1, (byte[]) msg.obj);
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

    private Runnable autoConnectRunnable=new Runnable() {
        @Override
        public void run() {
            Log.i("1111","1234kasjffdlks:0");
            if(!NewBeeBleConfig.getInstance().isAutoConnect()){
                return;
            }


            BleDeviceBean bleDeviceBean= BleConnectStatuUtil.getInstance().getNowUseBleDevice();
            boolean isConnect=BleConnectStatuUtil.getInstance().isConnect();
            if(null!=bleDeviceBean&&isConnect){
                return;
            }
            Log.i("1111","1234kasjffdlks:1");
            if(isDisConnectReinit){
                isDisConnectReinit=false;
                havePermissionInitBle();
            }else {
                startSearchBle();
            }
            autoConnectDevice();
        }
    };

    private void autoConnectDevice(){

        Log.i("1111","1234kasjffdlks:2");
        handler.removeCallbacks(autoConnectRunnable);
        handler.postDelayed(autoConnectRunnable,5*1000);
    }

    boolean isDisConnectReinit;
    private void autoConnectDeviceNow(){
        Log.i("1111","1234kasjffdlks:3");
        handler.removeCallbacks(autoConnectRunnable);
        int waitTime=1*1000;
        if(isDisConnectReinit){
            waitTime=3*1000;
        }
        handler.postDelayed(autoConnectRunnable,waitTime);

    }
    private void nowConnectIngWait(){
        Log.i("1111","1234kasjffdlks:4");
        handler.removeCallbacks(autoConnectRunnable);
        handler.postDelayed(autoConnectRunnable,13*1000);
    }

    private void cancelAutoConnect(){
        Log.i("1111","1234kasjffdlks:5");
        handler.removeCallbacks(autoConnectRunnable);
    }


    private Runnable bleDisConnectRunnable=new Runnable() {
        @Override
        public void run() {
            isDisConnectReinit=true;
            BleConnectStatuUtil.getInstance().sendDisconnected();
        }
    };
    private BleStatuEventObserver bleStatuEventObserver=new BleStatuEventObserver() {
        @Override
        public void sendBleStatu(BleStatu bleStatu, Object... objects) {
            Log.w(tag,"BluetoothAdapter  initialized  11155----"+ bleStatu);
                switch (bleStatu){
                    case DISCONNECTED:
                    case CONNECTING_ERR:
                        autoConnectDeviceNow();
                        break;
                    case CONNECTING:
                        nowConnectIngWait();
                        break;
                    case CONNECTED:
                        cancelAutoConnect();
                        break;
                    case SENDING_DATA:
                        handler.removeCallbacks(bleDisConnectRunnable);
                        handler.postDelayed(bleDisConnectRunnable,10*1000);
                        break;
                    case CAN_SEND_DATA:
                        handler.removeCallbacks(bleDisConnectRunnable);
                        break;

                }
        }
    };


    @Override
    public void init() {
        BleStatuEventSubscriptionSubject.getInstance().attach(bleStatuEventObserver);
        BleEventSubscriptionSubject.getInstance().attach(this);

    }



    @Override
    public void start(Intent intent, int flags, int startId) {

    }

    @Override
    public void close() {
        handler.removeCallbacksAndMessages(null);
        BleConnectManager.getInstance().close(getBaseContext());
        BleEventSubscriptionSubject.getInstance().detach(this);

        BleStatuEventSubscriptionSubject.getInstance().detach(bleStatuEventObserver);
    }

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
    public void sendImage(BleSendImageInfoBean sendImageInfoBean) {
        Message msg=new Message();
        msg.what=BluetoothGattServiceMsgType.SEND_IMAGE.ordinal();
        msg.obj=sendImageInfoBean;
        handler.sendMessage(msg);
    }

    @Override
    public void sendImageIndexCmd(int index, byte[] bytes) {
        Message msg=new Message();
        msg.what=BluetoothGattServiceMsgType.SEND_CMD_BY_IMAGE_INDEX.ordinal();
        msg.obj=bytes;
        msg.arg1=index;
        handler.sendMessage(msg);
    }




}
