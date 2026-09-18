package com.newbee.ble_lib.util;



import com.nrmyw.ble_event_lib.util.BleByteUtil;

public class BytesCheckUtil {


    public static boolean checkCmdGetToDo(byte[] checkBytes,byte[] retrunBytes){

        if(null==checkBytes||checkBytes.length==0){
            return false;
        }
        if(null==retrunBytes||retrunBytes.length==0){
            return false;
        }
        if(retrunBytes.length<checkBytes.length){
            return false;
        }
        try {
            String checkStr=BleByteUtil.parseByte2HexStr(checkBytes);
            String retrunStr=BleByteUtil.parseByte2HexStr(retrunBytes);

            return retrunStr.contains(checkStr);
        }catch (Exception e){
            return false;
        }

    }
}
