package com.newbee.ble_lib.event.send;//package com.newbee.ble_lib.event.send;


import android.graphics.Bitmap;


import com.nrmyw.ble_event_lib.type.BleSendBitmapQualityType;

import java.util.ArrayList;
import java.util.List;


public class BleEventSubscriptionSubject implements BleEventSubject {

    private List<BleEventObserver> observers;
    private static BleEventSubscriptionSubject subscriptionSubject;

    private BleEventSubscriptionSubject() {
        observers = new ArrayList<>();
    }

    public static BleEventSubscriptionSubject getInstance() {
        if (subscriptionSubject == null) {
            synchronized (BleEventSubscriptionSubject.class) {
                if (subscriptionSubject == null)
                    subscriptionSubject = new BleEventSubscriptionSubject();
            }
        }
        return subscriptionSubject;

    }

    @Override
    public void attach(BleEventObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(BleEventObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void havePermissionInitBle() {
        for (BleEventObserver observer:observers){
            observer.havePermissionInitBle();
        }
    }

    @Override
    public void startSearchBle() {
        for (BleEventObserver observer:observers){
            observer.startSearchBle();
        }
    }

    @Override
    public void disconnectedBle() {
        for (BleEventObserver observer:observers){
            observer.disconnectedBle();
        }
    }

    @Override
    public void sendCmd(byte[] bytes) {
        for (BleEventObserver observer:observers){
            observer.sendCmd(bytes);
        }
    }

    @Override
    public void sendImageIndexCmd(int index, byte[] bytes) {
        for (BleEventObserver observer:observers){
            observer.sendImageIndexCmd(index,bytes);
        }
    }



    @Override
    public void sendImage(Bitmap bitmap, BleSendBitmapQualityType bitmapQualityType) {
        for (BleEventObserver observer:observers){
            observer.sendImage(bitmap,bitmapQualityType);
        }
    }




}
