package com.newbee.ble_lib.manager.file.send;

import android.text.TextUtils;
import com.nrmyw.ble_event_lib.util.BleByteUtil;

public enum CheckRetrunByteAndToDoType{
        NONE,
        READY_OK(new byte[]{(byte)0xA5,(byte)0xA6}),
        SEND_ERR(new byte[]{(byte)0xA5,(byte)0xA4}),
        SEND_INDEX_OK(new byte[]{(byte)0xA5,(byte)0xA7}),
        RE_SEND(new byte[]{(byte)0xA5,(byte)0xA8}),
        END_OK(new byte[]{(byte)0xA5,(byte)0xA9}),
        ;
        private String checkStr;
        private byte[] checkBytes;
        private CheckRetrunByteAndToDoType(){

        }
        private CheckRetrunByteAndToDoType(byte[] checkBytes){
            this.checkBytes=checkBytes;
            this.checkStr= BleByteUtil.parseByte2HexStr(this.checkBytes);
        }

    public String getCheckStr() {
        return checkStr;
    }

    public byte[] getCheckBytes() {
        return checkBytes;
    }

    public static CheckRetrunByteAndToDoType useRetrunBytesGet(byte[] retrunBytes){
            if(null==retrunBytes||retrunBytes.length==0){
                return CheckRetrunByteAndToDoType.NONE;
            }

        String retrunStr=BleByteUtil.parseByte2HexStr(retrunBytes);

        for(CheckRetrunByteAndToDoType toDoType:CheckRetrunByteAndToDoType.values()){

            if(!TextUtils.isEmpty(toDoType.checkStr)){

                if(retrunStr.length()>=toDoType.checkStr.length()){

                    String useStr=retrunStr.substring(0,toDoType.checkStr.length());

                    if(useStr.equals(toDoType.checkStr)){
                        return toDoType;
                    }
                }




            }
        }
        return CheckRetrunByteAndToDoType.NONE;
    }
}