package com.newbee.ble_lib.service.event;//package com.newbee.ble_lib.event.send;






public interface BleDelayEventSubject {
    /**
     * 增加订阅者
     *
     * @param observer
     */
    public void attach(BleDelayObserver observer);

    /**
     * 删除订阅者
     *
     * @param observer
     */
    public void detach(BleDelayObserver observer);

    public void delayDo(BleDelayType delayType,long needDelayTime);
}
