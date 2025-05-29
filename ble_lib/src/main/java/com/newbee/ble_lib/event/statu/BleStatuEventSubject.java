package com.newbee.ble_lib.event.statu;






public interface BleStatuEventSubject {
    /**
     * 增加订阅者
     *
     * @param observer
     */
    public void attach(BleStatuEventObserver observer);

    /**
     * 删除订阅者
     *
     * @param observer
     */
    public void detach(BleStatuEventObserver observer);


    public  void sendBleStatu(BleStatu bleStatu,Object... objects);

}
