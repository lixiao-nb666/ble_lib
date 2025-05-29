package com.newbee.ble_lib.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.newbee.ble_lib.base.BaseService;
import com.newbee.ble_lib.event.send.BleCmdEventObserver;
import com.newbee.ble_lib.event.send.BleCmdEventSubscriptionSubject;
import com.newbee.ble_lib.event.statu.BleStatu;
import com.newbee.ble_lib.event.statu.BleStatuEventSubscriptionSubject;
import com.newbee.ble_lib.manager.child.BleConnectManager;
import com.newbee.ble_lib.manager.image.BitmapQualityType;
import com.newbee.ble_lib.manager.image.BlueToothGattSendImageManager;
import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;


public class BluetoothGattService extends BaseService implements BleCmdEventObserver {



    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                BluetoothGattServiceMsgType msgType=BluetoothGattServiceMsgType.values()[msg.what];
                switch (msgType){
                    case INIT_BLE:
                        BleConnectManager.getInstance().havePermissionInitBle(getBaseContext(),getPackageManager());
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
                        BlueToothGattSendImageManager.getInstance().sendBitMap((Bitmap) msg.obj,BitmapQualityType.values()[msg.arg1]);
                        break;
                    case SEND_CMD_BY_IMAGE_INDEX:
                        BlueToothGattSendMsgManager.getInstance().sendMsgByImg(msg.arg1, (byte[]) msg.obj);
                        break;
                }
            }catch (Exception e){
                BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,"BluetoothGattService: handler-"+e.toString());
            }
        }
    };

    public BleCmdEventObserver getEventImp(){
        return this;
    }




    @Override
    public void init() {
//        BleStatuEventSubscriptionSubject.getInstance().attach(bleStatuEventObserver);
        BleCmdEventSubscriptionSubject.getInstance().attach(this);
    }



    @Override
    public void start(Intent intent, int flags, int startId) {

    }

    @Override
    public void close() {
        handler.removeCallbacksAndMessages(null);
        BleConnectManager.getInstance().close(getBaseContext());
        BleCmdEventSubscriptionSubject.getInstance().detach(this);
//        BleStatuEventSubscriptionSubject.getInstance().detach(bleStatuEventObserver);
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
    public void sendImage(Bitmap bitmap, BitmapQualityType bitmapQualityType) {
        Message msg=new Message();
        msg.what=BluetoothGattServiceMsgType.SEND_IMAGE.ordinal();
        msg.obj=bitmap;
        msg.arg1=bitmapQualityType.ordinal();
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
