package com.newbee.ble_lib.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.util.BleByteUtil;

@SuppressLint("MissingPermission")
public class BleCheckUtil {

    public static boolean checkPhoneCanUseBle(PackageManager packageManager){
        return packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public static boolean checkTheBleCanUse(ScanResult result){
        ScanRecord record = result.getScanRecord();
        BleDeviceBean bleDeviceBean= NewBeeBleConfig.getInstance().checkBleScanRecord(record);
        BluetoothDevice device=result.getDevice();
        String deviceName = device.getName();
        String address=device.getAddress();
        if(null!=bleDeviceBean&&!TextUtils.isEmpty(address)){

            setToConnecting(bleDeviceBean,deviceName,address);
            return true;
        }
        return false;

//        byte[] manufacturerBytes= record.getManufacturerSpecificData(0x0642);
//        if(null!=manufacturerBytes){
//            String manufacturerString= BleByteUtil.parseByte2HexStr(manufacturerBytes);
//
//            Log.i("tag","34kasjffdlks:9999--11110000---"+manufacturerString+"--"+bleDeviceBean+"----"+record.getDeviceName());
//            if(null!=bleDeviceBean){
//                BluetoothDevice device=result.getDevice();
//                String deviceName = device.getName();
//                String address=device.getAddress();
//                setToConnecting(bleDeviceBean,deviceName,address);
//                return true;
//            }
//        }
//       return checkTheBleCanUse(result.getDevice());
    }
    public static boolean checkTheBleCanUse( BluetoothDevice device){
        Log.i("tag","34kasjffdlks:9999--11110011---"+device.getName());
        String deviceName = device.getName();
//            String deviceHardwareAddress = device.getAddress(); // MAC address
        BleDeviceBean bleDeviceBean= NewBeeBleConfig.getInstance().checkBleName(deviceName);
        String address=device.getAddress();
        if(null!=bleDeviceBean&&!TextUtils.isEmpty(address)){
//                Log.w(tag,"BluetoothAdapter  initialized  111556677--8811--"+ deviceName+"--"+address);
                setToConnecting(bleDeviceBean,deviceName,address);
            return true;
        }else {
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.FOUND_BLE_DEVICE,deviceName,address);
            return false;
        }
    }

    private static void setToConnecting(BleDeviceBean bleDeviceBean,String deviceName,String address){
        bleDeviceBean.setBleName(deviceName);

        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CONNECTING,deviceName,address);
        BleConnectStatuUtil.getInstance().sendConnecting(bleDeviceBean,address);
    }

}
