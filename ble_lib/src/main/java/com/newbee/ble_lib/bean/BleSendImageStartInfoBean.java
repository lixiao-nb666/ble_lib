package com.newbee.ble_lib.bean;

import java.io.Serializable;

public class BleSendImageStartInfoBean implements Serializable {
    private int w;
    private int h;
    private int size;




    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "SendImageStartBean{" +
                "w=" + w +
                ", h=" + h +
                ", size=" + size +
                '}';
    }
}
