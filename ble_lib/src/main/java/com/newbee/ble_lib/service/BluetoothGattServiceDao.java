package com.newbee.ble_lib.service;

import android.content.Context;

import com.newbee.ble_lib.base.BaseService;
import com.newbee.ble_lib.base.BaseServiceDao;



/**
 * @author lixiaogege!
 * @description: one day day ,no zuo no die !
 * @date :2021/1/18 0018 9:50
 */
public class BluetoothGattServiceDao extends BaseServiceDao {
    BaseService.StatuListen statuListen=new BaseService.StatuListen() {
        @Override
        public void isStart() {

        }
    };

    @Override
    protected Class getSsCls() {
        return BluetoothGattService.class;
    }

    public BluetoothGattServiceDao(Context context) {
        super(context);
    }

    @Override
    protected BaseService.StatuListen getStatuListen() {
        return statuListen;
    }
}
