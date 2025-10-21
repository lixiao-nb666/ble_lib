package com.newbee.ble_lib.manager.msg;

import android.text.TextUtils;
import com.newbee.ble_lib.manager.child.BlueToothGattManager;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.util.BleByteUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


class BlueToothGattMsgManager {
    private static BlueToothGattMsgManager sendMsgManager;
    private Map<String, byte[]> msgMq = new ConcurrentHashMap<>();

    private boolean nowCanSendImage;
    private int imageMsgCountNumb;
    private int imageMsgIndex;
    private Map<String, byte[]> imageMsgMq = new ConcurrentHashMap<>();


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
        clearImageMsg();
    }

    public synchronized void queMsg() {
        if (!queImageMsg()) {
            queCmdMsg();
        }
    }
    public synchronized void addMsg(byte[] msg) {
        String kStr = BleByteUtil.getCmdStrK(msg);
        msgMq.put(kStr, msg);

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
            if (BlueToothGattManager.getInstance().isNowCanSend()) {
                BlueToothGattManager.getInstance().queSendCmd(msg);
                removeMsg(kStr);
            }
        } catch (Exception e) {
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR, "canSendMsg:" + e.toString());
        }
    }

    public void clearImageMsg(){
        nowCanSendImage=false;
        imageMsgCountNumb=0;
        imageMsgIndex=0;
        imageMsgMq.clear();
    }

    public void readySendImage(){
        nowCanSendImage=true;
        imageMsgCountNumb=0;
        imageMsgIndex=0;
        imageMsgMq.clear();
    }

    public void setNowCanSendImageNumb(int countNumb){
        imageMsgCountNumb=countNumb;
    }

    private void listenSendImageMsg(int index, byte[] msg) {
        try {
            if (BlueToothGattManager.getInstance().isNowCanSend()) {
                BlueToothGattManager.getInstance().queSendCmd(msg);
                if(index==imageMsgCountNumb-1){
                    clearImageMsg();
                }else {
                    imageMsgIndex++;
                }
            }
        } catch (Exception e) {
            clearImageMsg();
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR, "canSendImageMsg:" + e.toString());
        }
    }



    public synchronized void addMsgByImage(int index, byte[] msg) {
        if(!nowCanSendImage){
            return;
        }
        imageMsgMq.put(index + "", msg);
    }




    private synchronized boolean queImageMsg() {
        if (!nowCanSendImage) {
            return false;
        }
        if(imageMsgCountNumb==0){
            return false;
        }
        if(imageMsgMq.isEmpty()){
            return false;
        }
        if( imageMsgCountNumb!=imageMsgMq.size()){
            return false;
        }


//        int index = getImageFristK();
        byte[] cmd = imageMsgMq.get(imageMsgIndex + "");
        if (null == cmd || cmd.length == 0) {
            clearImageMsg();
            return false;
        }
        listenSendImageMsg(imageMsgIndex, cmd);

        return true;
    }


    public synchronized void removeImageMsg(String kStr) {
        imageMsgMq.remove(kStr);
//       LG.i("kankanshujuqingkongmei:"+msgMq.size());
    }






}
