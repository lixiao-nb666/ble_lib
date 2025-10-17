package com.newbee.ble_lib.manager.child;

import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

public class OtaBle {

    private static final UUID OTA_SERVICE_UUID  = UUID.fromString("0000ff12-0000-1000-8000-00805f9b34fb");
    private static final UUID OTA_DATA_IN_UUID  = UUID.fromString("0000ff14-0000-1000-8000-00805f9b34fb");
    private static final UUID OTA_DATA_OUT_UUID = UUID.fromString("0000ff15-0000-1000-8000-00805f9b34fb"); // without response

    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public OtaBle(){


    }

    private void findCharacteristics() {
//        otaService = bluetoothGatt.getService(OTA_SERVICE_UUID);
//        if (otaService == null) {
////            notifyOnError(OtaError.NOT_FOUND_OTA_SERVICE);
//            Timber.e("FOTA service not found");
//            return;
//        }
//        dataInCharacteristic = otaService.getCharacteristic(OTA_DATA_IN_UUID);
//        dataOutCharacteristic = otaService.getCharacteristic(OTA_DATA_OUT_UUID);
//
//        if (dataInCharacteristic == null || dataOutCharacteristic == null) {
////            notifyOnError(OtaError.NOT_FOUND_OTA_CHARACTERISTIC);
//            Timber.e("FOTA characteristic(s) not found");
//            return;
//        }
//
//        // 订阅Data In
//        boolean enabled = bluetoothGatt.setCharacteristicNotification(dataInCharacteristic, true);
//        if (!enabled) {
////            notifyOnError(OtaError.CAN_NOT_SUBSCRIBE_DATA_IN);
//            Timber.e("Error subscribe FOTA Data In");
//            return;
//        }
//        BluetoothGattDescriptor descriptor = dataInCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
//        if (descriptor == null) {
////            notifyOnError(OtaError.NOT_FOUND_CLIENT_CHARACTERISTIC_CONFIG);
//            Timber.e("FOTA CCC not found");
//            return;
//        }
//        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//        bluetoothGatt.writeDescriptor(descriptor);

        // 等待onDescriptorWrite回调做下一步操作
    }

}
