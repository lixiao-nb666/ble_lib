//package com.newbee.ble_lib.event.retrun;
//
//
//
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class BleCmdRetrunEventSubscriptionSubject implements BleCmdRetrunEventSubject {
//
//    private List<BleCmdRetrunEventObserver> observers;
//    private static BleCmdRetrunEventSubscriptionSubject subscriptionSubject;
//
//    private BleCmdRetrunEventSubscriptionSubject() {
//        observers = new ArrayList<>();
//    }
//
//    public static BleCmdRetrunEventSubscriptionSubject getInstance() {
//        if (subscriptionSubject == null) {
//            synchronized (BleCmdRetrunEventSubscriptionSubject.class) {
//                if (subscriptionSubject == null)
//                    subscriptionSubject = new BleCmdRetrunEventSubscriptionSubject();
//            }
//        }
//        return subscriptionSubject;
//
//    }
//
//    @Override
//    public void attach(BleCmdRetrunEventObserver observer) {
//        observers.add(observer);
//    }
//
//    @Override
//    public void detach(BleCmdRetrunEventObserver observer) {
//        observers.remove(observer);
//    }
//
//    @Override
//    public void getCmd(byte[] bytes) {
//        for (BleCmdRetrunEventObserver observer:observers){
//            observer.getCmd(bytes);
//        }
//    }
//
//
//
//}
