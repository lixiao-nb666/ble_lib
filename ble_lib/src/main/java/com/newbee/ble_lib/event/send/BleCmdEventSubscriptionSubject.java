package com.newbee.ble_lib.event.send;//package com.newbee.ble_lib.event.send;



import android.graphics.Bitmap;


import com.newbee.ble_lib.manager.image.BitmapQualityType;

import java.util.ArrayList;
import java.util.List;


public class BleCmdEventSubscriptionSubject implements BleCmdEventSubject {

    private List<BleCmdEventObserver> observers;
    private static BleCmdEventSubscriptionSubject subscriptionSubject;

    private BleCmdEventSubscriptionSubject() {
        observers = new ArrayList<>();
    }

    public static BleCmdEventSubscriptionSubject getInstance() {
        if (subscriptionSubject == null) {
            synchronized (BleCmdEventSubscriptionSubject.class) {
                if (subscriptionSubject == null)
                    subscriptionSubject = new BleCmdEventSubscriptionSubject();
            }
        }
        return subscriptionSubject;

    }

    @Override
    public void attach(BleCmdEventObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(BleCmdEventObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void havePermissionInitBle() {
        for (BleCmdEventObserver observer:observers){
            observer.havePermissionInitBle();
        }
    }

    @Override
    public void startSearchBle() {
        for (BleCmdEventObserver observer:observers){
            observer.startSearchBle();
        }
    }

    @Override
    public void disconnectedBle() {
        for (BleCmdEventObserver observer:observers){
            observer.disconnectedBle();
        }
    }

    @Override
    public void sendCmd(byte[] bytes) {
        for (BleCmdEventObserver observer:observers){
            observer.sendCmd(bytes);
        }
    }

    @Override
    public void sendImageIndexCmd(int index, byte[] bytes) {
        for (BleCmdEventObserver observer:observers){
            observer.sendImageIndexCmd(index,bytes);
        }
    }



    @Override
    public void sendImage(Bitmap bitmap, BitmapQualityType bitmapQualityType) {
        for (BleCmdEventObserver observer:observers){
            observer.sendImage(bitmap,bitmapQualityType);
        }
    }




}
