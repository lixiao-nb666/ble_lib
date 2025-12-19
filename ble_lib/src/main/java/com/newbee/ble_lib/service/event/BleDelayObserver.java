package com.newbee.ble_lib.service.event;//package com.newbee.ble_lib.event.send;


/**
 * Created by lixiao on 2017/5/12.
 * about:
 */

public interface BleDelayObserver {

    public void delayDo(BleDelayType delayType,long needDelayTime);
}
