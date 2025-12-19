package com.newbee.ble_lib.manager.child;

public class ConnectOldDeviceManager {

    private static ConnectOldDeviceManager manager;
    private String oldDeviceAdress;

    private ConnectOldDeviceManager(){}

    public static ConnectOldDeviceManager getInstance(){
        if(null==manager){
            synchronized (ConnectOldDeviceManager.class){
                if(null==manager){
                    manager=new ConnectOldDeviceManager();
                }
            }
        }
        return manager;
    }



}
