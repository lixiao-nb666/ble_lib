package com.newbee.ble_lib.manager.child;

public class BlueToothSendOtaManager {

    private static BlueToothSendOtaManager manager;
    private boolean isOtaing;
    private long lastTime;
    private BlueToothSendOtaManager(){}

    public static BlueToothSendOtaManager getInstance(){
        if(null==manager){
            synchronized (BlueToothSendOtaManager.class){
                if(null==manager){
                    manager=new BlueToothSendOtaManager();
                }
            }
        }
        return manager;
    }

    public boolean nowIsOta(){

        return isOtaing;
    }

    public void nowStartOta(){
        lastTime=System.currentTimeMillis();
        isOtaing=true;
    }


    public void startOtaCmd(byte[] startCmd){

    }

    public void startIndex(int index,byte[] bodyCmd){

    }


    public void end(){

    }



}
