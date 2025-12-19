package com.newbee.ble_lib.manager.file;

public interface BlueToothGattSendFile {

    public void setListen(BlueToothGattGetFileDataThread.Listen listen);

    public boolean isStart();

    public void clear();

}
