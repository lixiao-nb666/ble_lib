package com.newbee.ble_lib.manager;

import android.content.Context;


import com.newbee.ble_lib.event.send.BleCmdEventObserver;
import com.newbee.ble_lib.service.BluetoothGattService;
import com.newbee.ble_lib.service.BluetoothGattServiceDao;

public class BleManager {
    private static BleManager bleManager;
    private BluetoothGattServiceDao bluetoothGattServiceDao;



    private BleManager(){

    }

    public static BleManager getInstance(){
        if(null==bleManager){
            synchronized (BleManager.class){
                if(null==bleManager){
                    bleManager=new BleManager();
                }
            }
        }
        return bleManager;
    }


    public void init(Context context){
        bluetoothGattServiceDao=new BluetoothGattServiceDao(context);
        bluetoothGattServiceDao.startServiceIsBind();
    }

    public BleCmdEventObserver getEventImp(){
        try {
            BluetoothGattService bluetoothGattService=(BluetoothGattService) bluetoothGattServiceDao.getService();
            if(null!=bluetoothGattService){
                return bluetoothGattService.getEventImp();
            }
        }catch (Exception e){

        }
        return null;
    }

    public void close(){
        if(null!=bluetoothGattServiceDao){
            bluetoothGattServiceDao.stopServiceIsBind();
            bluetoothGattServiceDao=null;
        }
    }



}
