package com.newbee.ble_lib;

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



    public BleEventObserver getEventImp(){
        try {
            BluetoothGattService bluetoothGattService=(BluetoothGattService) bluetoothGattServiceDao.getService();
            if(null!=bluetoothGattService){
                return bluetoothGattService.getEventImp();
            }
        }catch (Exception e){

        }
        return null;
    }


    public BleDeviceBean getNowUseBleDevice(){
        return BleConnectStatuUtil.getInstance().getNowUseBleDevice();
    }

    public boolean isConnect(){
        return BleConnectStatuUtil.getInstance().isConnect();
    }

    public void close(){
        if(null!=bluetoothGattServiceDao){
            bluetoothGattServiceDao.stopServiceIsBind();
            bluetoothGattServiceDao=null;
        }
        BleStatuBroadcastReceiverDao.getInstance().close();
    }

}
