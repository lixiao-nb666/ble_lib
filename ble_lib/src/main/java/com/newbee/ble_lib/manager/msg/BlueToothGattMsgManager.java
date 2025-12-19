package com.newbee.ble_lib.manager.msg;

import android.text.TextUtils;
import android.util.Log;

import com.newbee.ble_lib.manager.child.BlueToothGattManager;
import com.newbee.ble_lib.manager.child.BlueToothSendStatuManager;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.util.BleByteUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


class BlueToothGattMsgManager {
    private static BlueToothGattMsgManager sendMsgManager;
    private Map<String, byte[]> msgMq = new ConcurrentHashMap<>();

    private boolean nowCanSendImage;
    private int fileMsgCountNumb;
    private int fileMsgIndex;
    private Map<String, byte[]> fileMsgMq = new ConcurrentHashMap<>();


    private BlueToothGattMsgManager() {

    }

    public static BlueToothGattMsgManager getInstance() {
        if (null == sendMsgManager) {
            synchronized (BlueToothGattMsgManager.class) {
                if (null == sendMsgManager) {
                    sendMsgManager = new BlueToothGattMsgManager();
                }
            }
        }
        return sendMsgManager;
    }


    public void close() {
        clear();
        sendMsgManager = null;
    }

//   public void init(Listen listen){
//       this.listen=listen;
//   }

    public void clear() {
        msgMq.clear();
        clearFileMsg();
    }

    public synchronized void queMsg() {
        if (!queFileMsg()) {
            queCmdMsg();
        }
    }
    public synchronized void addMsg(byte[] msg) {
        String kStr = BleByteUtil.getCmdStrK(msg);
        msgMq.put(kStr, msg);
        Log.i("tag","发送 ===  :发送成功---checkReturnToSend 3311-1:count"+BleByteUtil.parseByte2HexStr(msg));
    }

    private void queCmdMsg() {
        if (msgMq.isEmpty()) {
            return;
        }
        String kStr = getFristKStr();
        if (TextUtils.isEmpty(kStr)) {
            msgMq.clear();
            return;
        }
        byte[] cmd = msgMq.get(kStr);
        if (null == cmd || cmd.length == 0) {
            msgMq.clear();
            return;
        }
        Log.i("tag","发送 ===  :发送成功---checkReturnToSend 3333331111---555666:count"+fileMsgCountNumb+"--"+fileMsgIndex+"----"+BleByteUtil.parseByte2HexStr(cmd));
        listenSendMsg(kStr, cmd);
    }


    public synchronized void removeMsg(String kStr) {
        msgMq.remove(kStr);
//       LG.i("kankanshujuqingkongmei:"+msgMq.size());
    }
    private String getFristKStr() {
        for (String str : msgMq.keySet()) {
            if (!TextUtils.isEmpty(str)) {
                return str;
            }
        }
        return null;
    }



    private void listenSendMsg(String kStr, byte[] msg) {

        try {
            if (BlueToothSendStatuManager.getInstance().isNowCanSend()) {
                Log.i("tag","发送 ===  :发送成功---checkReturnToSend 3333331112:count"+fileMsgCountNumb+"--"+fileMsgIndex+"----"+BleByteUtil.parseByte2HexStr(msg));
                BlueToothSendStatuManager.getInstance().sendBytyes(msg);
                if(msgMq.size()==1){
                    msgMq.clear();
                }else {
                    removeMsg(kStr);
                }

            }
        } catch (Exception e) {
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR, "canSendMsg:" + e.toString());
        }
    }

    public void clearFileMsg(){
        nowCanSendImage=false;
        fileMsgCountNumb=0;
        fileMsgIndex=0;
        fileMsgMq.clear();
    }

    private long startSendImageTime;
    public void readySendFile(){
        nowCanSendImage=true;
        fileMsgCountNumb=0;
        fileMsgIndex=0;
        fileMsgMq.clear();
        startSendImageTime=System.currentTimeMillis();
    }

    public void setNowCanSendFileNumb(int countNumb){
        fileMsgCountNumb=countNumb;
    }

    private void listenSendFileMsg(int index, byte[] msg) {
        try {
            if (BlueToothSendStatuManager.getInstance().isNowCanSend()) {
                BlueToothSendStatuManager.getInstance().sendBytyes(msg);
                if(index==fileMsgCountNumb-1){
                    clearFileMsg();
                    long sendImageUseTime=System.currentTimeMillis()-startSendImageTime;
                    BlueToothSendStatuManager.getInstance().setFileCheckOver(startSendImageTime,sendImageUseTime);
                }else {
                    fileMsgIndex++;
                }
            }
        } catch (Exception e) {
            clearFileMsg();
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR, "canSendImageMsg:" + e.toString());
        }
    }



    public synchronized void addMsgByFile(int index, byte[] msg) {
        if(!nowCanSendImage){
            return;
        }
        fileMsgMq.put(index + "", msg);
    }


    public void reSendFileMsg(){
        Log.i("tag", "发送 ===  :发送成功---checkReturnToSend 111133?????????:  ---A5A8----kaishichongfa111222--"+fileMsgIndex);
        fileMsgIndex--;
        Log.i("tag"," 发送 ===  :发送成功---checkReturnToSend 111133?????????:   ---A5A8----kaishichongfa111333:--"+fileMsgIndex);
        queMsg();
    }




    private synchronized boolean queFileMsg() {
        if (!nowCanSendImage) {
            return false;
        }
        if(fileMsgCountNumb==0){
            return false;
        }
        if(fileMsgMq.isEmpty()){
            return false;
        }
        if( fileMsgCountNumb!=fileMsgMq.size()){
            return false;
        }


//        int index = getImageFristK();
        byte[] cmd = fileMsgMq.get(fileMsgIndex + "");

        Log.i("tag","发送 ===  :发送成功---checkReturnToSend 3333331111---555665:count"+fileMsgCountNumb+"--"+fileMsgIndex+"----"+BleByteUtil.parseByte2HexStr(cmd));
        if (null == cmd || cmd.length == 0) {
            clearFileMsg();
            return false;
        }
        listenSendFileMsg(fileMsgIndex, cmd);
        return true;
    }


    public synchronized void removeFileMsg(String kStr) {
        fileMsgMq.remove(kStr);
//       LG.i("kankanshujuqingkongmei:"+msgMq.size());
    }






}
