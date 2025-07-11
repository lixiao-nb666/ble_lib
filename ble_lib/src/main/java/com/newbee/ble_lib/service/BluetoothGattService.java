package com.newbee.ble_lib.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.newbee.ble_lib.base.BaseService;

import com.newbee.ble_lib.manager.child.BleConnectManager;

import com.newbee.ble_lib.manager.image.BlueToothGattSendImageManager;
import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.nrmyw.ble_event_lib.bean.BleSendImageInfoBean;
import com.nrmyw.ble_event_lib.send.BleEventObserver;
import com.nrmyw.ble_event_lib.send.BleEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.statu.BleStatu;
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
                BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,"BluetoothGattService: handler-"+e.toString());
            }
        }
    };





    @Override
    public void init() {
//        BleStatuEventSubscriptionSubject.getInstance().attach(bleStatuEventObserver);
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
