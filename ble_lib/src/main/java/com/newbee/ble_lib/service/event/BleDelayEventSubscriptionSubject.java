package com.newbee.ble_lib.service.event;//package com.newbee.ble_lib.event.send;




import java.util.ArrayList;
import java.util.List;


public class BleDelayEventSubscriptionSubject implements BleDelayEventSubject {

    private List<BleDelayObserver> observers;
    private static BleDelayEventSubscriptionSubject subscriptionSubject;

    private BleDelayEventSubscriptionSubject() {
        observers = new ArrayList<>();
    }

    public static BleDelayEventSubscriptionSubject getInstance() {
        if (subscriptionSubject == null) {
            synchronized (BleDelayEventSubscriptionSubject.class) {
                if (subscriptionSubject == null)
                    subscriptionSubject = new BleDelayEventSubscriptionSubject();
            }
        }
        return subscriptionSubject;

    }


    @Override
    public void attach(BleDelayObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(BleDelayObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void delayDo(BleDelayType delayType, long needDelayTime) {
        for(BleDelayObserver observer:observers){
            observer.delayDo(delayType,needDelayTime);
        }
    }
}
