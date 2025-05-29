package com.newbee.ble_lib.bean;

import java.io.Serializable;

public class BleSendImageEndInfoBean implements Serializable {
    private int w;
    private int h;
    private int size;
    private int index;
    private long useTime;




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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "SendImageEndInfoBean{" +
                "w=" + w +
                ", h=" + h +
                ", size=" + size +
                ", index=" + index +
                ", useTime=" + useTime +
                '}';
    }
}
