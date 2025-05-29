//package com.newbee.ble_lib.event.hint;
//
//
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class BleHintEventSubscriptionSubject implements BleHintEventSubject{
//
//    private List<BleHintEventObserver> observers;
//    private static BleHintEventSubscriptionSubject subscriptionSubject;
//
//    private BleHintEventSubscriptionSubject() {
//        observers = new ArrayList<>();
//    }
//
//    public static BleHintEventSubscriptionSubject getInstance() {
//        if (subscriptionSubject == null) {
//            synchronized (BleHintEventSubscriptionSubject.class) {
//                if (subscriptionSubject == null)
//                    subscriptionSubject = new BleHintEventSubscriptionSubject();
//            }
//        }
//        return subscriptionSubject;
//
//    }
//
//    @Override
//    public void attach(BleHintEventObserver observer) {
//        observers.add(observer);
//    }
//
//    @Override
//    public void detach(BleHintEventObserver observer) {
//        observers.remove(observer);
//    }
//
//    @Override
//    public void sendImageStart(int w, int h, int size) {
//        for (BleHintEventObserver observer:observers){
//            observer.sendImageStart(w, h, size);
//        }
//    }
//
//    @Override
//    public void sendImageEnd(int w, int h, int size,long useTime,int index) {
//        for (BleHintEventObserver observer:observers){
//            observer.sendImageEnd(w, h, size,useTime,index);
//        }
//    }
//
//
//
//
//
//
//}
