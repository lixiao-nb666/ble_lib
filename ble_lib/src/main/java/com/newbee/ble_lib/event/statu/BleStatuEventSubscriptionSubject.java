package com.newbee.ble_lib.event.statu;



import java.util.ArrayList;
import java.util.List;


public class BleStatuEventSubscriptionSubject implements BleStatuEventSubject {

    private List<BleStatuEventObserver> observers;
    private static BleStatuEventSubscriptionSubject subscriptionSubject;

    private BleStatuEventSubscriptionSubject() {
        observers = new ArrayList<>();
    }

    public static BleStatuEventSubscriptionSubject getInstance() {
        if (subscriptionSubject == null) {
            synchronized (BleStatuEventSubscriptionSubject.class) {
                if (subscriptionSubject == null)
                    subscriptionSubject = new BleStatuEventSubscriptionSubject();
            }
        }
        return subscriptionSubject;

    }

    @Override
    public void attach(BleStatuEventObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(BleStatuEventObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void sendBleStatu(BleStatu bleStatu, Object... objects) {
        for (BleStatuEventObserver observer:observers){
            observer.sendBleStatu(bleStatu,objects);
        }
    }


}
