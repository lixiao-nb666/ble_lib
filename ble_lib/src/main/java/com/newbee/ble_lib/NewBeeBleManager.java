package com.newbee.ble_lib;

import android.app.Notification;
import android.content.Context;

import com.newbee.ble_lib.receiver.BleStatuBroadcastReceiverDao;
import com.newbee.ble_lib.service.BluetoothGattService;
import com.newbee.ble_lib.service.BluetoothGattServiceDao;
import com.newbee.ble_lib.util.BleConnectStatuUtil;
import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.send.BleEventObserver;

public class NewBeeBleManager {
    private static NewBeeBleManager bleManager;
    private BluetoothGattServiceDao bluetoothGattServiceDao;

    private NewBeeBleManager(){

    }

    public static NewBeeBleManager getInstance(){
        if(null==bleManager){
            synchronized (NewBeeBleManager.class){
                if(null==bleManager){
                    bleManager=new NewBeeBleManager();
                }
            }
        }
        return bleManager;
    }


    public void init(Context context){
        bluetoothGattServiceDao=new BluetoothGattServiceDao(context);
        bluetoothGattServiceDao.startServiceIsBind();
        BleStatuBroadcastReceiverDao.getInstance().init(context);

    }

    public void close(){
        if (null!=bleEventObserver) {
            bleEventObserver=null;
        }

        if(null!=bluetoothGattServiceDao){
            bluetoothGattServiceDao.stopServiceIsBind();
            bluetoothGattServiceDao=null;
        }
        BleStatuBroadcastReceiverDao.getInstance().close();
    }

    private BleEventObserver bleEventObserver;
    public void setBleEventObserver( BleEventObserver bleEventObserver){
        this.bleEventObserver=bleEventObserver;
        nowCheckToInitBle();
    }

    public BleEventObserver getEventImp(){

        return bleEventObserver;
    }

    private boolean needInitBle;
    public void nowGetAllPermissions(){
        needInitBle=true;
        nowCheckToInitBle();
    }

    private void nowCheckToInitBle(){
        if(null==bleEventObserver||!needInitBle){
            return;
        }
        bleEventObserver.havePermissionInitBle();
        needInitBle=false;
    }


    public BleDeviceBean getNowUseBleDevice(){
        return BleConnectStatuUtil.getInstance().getNowUseBleDevice();
    }

    public boolean isConnect(){
        return BleConnectStatuUtil.getInstance().isConnect();
    }


    private boolean nowPageIsShow;
    public void setPageAllIsShow(boolean nowPageIsShow){
        this.nowPageIsShow=nowPageIsShow;
    }

    public boolean isNowPageIsShow() {
        return nowPageIsShow;
    }

    //    private Notification notification;
//
//    public void setNotification(Notification notification){
//        this.notification=notification;
//    }
//
//    public Notification getNotification(){
//        return notification;
//    }
//
//    private int notificationId;
//
//    public int getNotificationId() {
//        return notificationId;
//    }
//
//    public void setNotificationId(int notificationId) {
//        this.notificationId = notificationId;
//    }

}
