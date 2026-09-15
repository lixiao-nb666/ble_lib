package com.newbee.ble_lib.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Arrays;

public class KeyStrBytesBean implements Serializable {
    private String keyStr;
    private byte[] bytes;

    public KeyStrBytesBean(String keyStr, byte[] bytes) {
        this.keyStr = keyStr;
        this.bytes = bytes;
    }

    public String getKeyStr() {
        if(TextUtils.isEmpty(keyStr)){
            keyStr="def_";
        }
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "KeyStrBytesBean{" +
                "keyStr='" + keyStr + '\'' +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
