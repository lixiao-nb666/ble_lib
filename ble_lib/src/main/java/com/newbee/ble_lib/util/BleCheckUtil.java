package com.newbee.ble_lib.util;

import android.content.pm.PackageManager;

public class BleCheckUtil {

    public static boolean checkPhoneCanUseBle(PackageManager packageManager){
        return packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }


}
