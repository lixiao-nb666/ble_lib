package com.newbee.ble_lib.manager.file.send;

import android.text.TextUtils;
import android.util.Log;

import com.newbee.ble_lib.manager.child.BlueToothSendOtaManager;
import com.newbee.ble_lib.manager.child.BlueToothSendStatuManager;
import com.newbee.ble_lib.manager.file.BlueToothGattGetFileDataThread;
import com.newbee.ble_lib.manager.file.BlueToothGattSendFile;
import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.newbee.ble_lib.util.CryptoUtils;
import com.newbee.ble_lib.util.FileAndByteUtil;
import com.newbee.ble_lib.util.hud.HudCmdSendDataUtil;
import com.nrmyw.ble_event_lib.bean.BleSendFileInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendOtaInfoBean;
import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.util.BleByteUtil;

public class SendOtaManager implements BlueToothGattSendFile {

    private BleSendOtaInfoBean otaInfoBean;

    private boolean isStart;
    private long startTime;
    public SendOtaManager(BleSendOtaInfoBean otaInfoBean){
        this.otaInfoBean=otaInfoBean;
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEND_OTA_START);
    }

    private BlueToothGattGetFileDataThread.Listen listen;
    @Override
    public void setListen(BlueToothGattGetFileDataThread.Listen listen) {
        this.listen=listen;
        run();
    }

    @Override
    public boolean isStart() {
        return false;
    }

    @Override
    public void clear() {

    }

    private void run(){
        Log.i("ota","otakaishi1111111111:3");
        if(null==otaInfoBean|| TextUtils.isEmpty(otaInfoBean.getFilePath()) ){
            listen.sendOver(0);
            return;
        }
        startTime=System.currentTimeMillis();
//            if(null==dataInfoQueue){
//                dataInfoQueue=new LinkedList<>();
//            }else {
//                dataInfoQueue.clear();
//            }
        isStart=true;
        progress=1;
        sendProgressEvent();
       fileBytes= FileAndByteUtil.getBytesByFile(otaInfoBean.getFilePath());
        if(null==fileBytes||fileBytes.length==0){
            listen.sendOver(0);
            return;
        }
        md5Bytes= CryptoUtils.getMD5(fileBytes);
        size=fileBytes.length;
        Log.i("ota","otakankantubianzenmhuis:ota----1");
        msgIndex=0;
        BlueToothSendStatuManager.getInstance().setSendOverListen(sendOverListen);
        progress=2;
        sendProgressEvent();
        sendFileStart(size,md5Bytes);



    }
    byte[] fileBytes;
    byte[] md5Bytes;
    int size;
    private int msgIndex;

    private String checkStr;
    private int needProgressNumb;
    private int progress;
    private void sendFileStart(int size,byte[] md5Bytes){
        BlueToothGattSendMsgManager.getInstance().readySendFile();
        byte[] cmdStart= HudCmdSendDataUtil.getReadySendFile(size,md5Bytes,true);
        checkStr=BleByteUtil.parseByte2HexStr(cmdStart);
        BlueToothGattSendMsgManager.getInstance().sendMsgByCmd(cmdStart);
        progress=3;
        sendProgressEvent();
    }

    private void splitPacketForMtuByte(byte[] fileBytes){
        if(!(null == fileBytes)){
            int dataIndex=0;
            int mtu= NewBeeBleConfig.getInstance().getRealMtu()-22;
            Log.i("ota","otakankantubianzenmhuis:11111111:ready ing ");
            do{

                byte[] currentData;
                if(fileBytes.length-dataIndex <= mtu){
                    currentData = new byte[fileBytes.length-dataIndex];
                    System.arraycopy(fileBytes, dataIndex, currentData, 0, fileBytes.length - dataIndex);

                    dataIndex = fileBytes.length;

                }else {
                    currentData = new byte[mtu];
                    System.arraycopy(fileBytes, dataIndex, currentData, 0,mtu);
                    dataIndex += mtu;

                }
                byte[] cmd=HudCmdSendDataUtil.getSendFile(msgIndex,currentData);
                BlueToothGattSendMsgManager.getInstance().sendMsgByFile(msgIndex,cmd);
                msgIndex++;
//                byte[] indexByte=BleByteUtil.intToByteArray32only2(msgIndex);

//                dataInfoQueue.offer(currentData);
            }while (dataIndex < fileBytes.length);
            progress=7;
            sendProgressEvent();

        }
    }

    private void over(int size,byte[] md5Bytes){
        sendFileEnd(size,md5Bytes);
        isStart=false;
        clear();
        progress=20;
        sendProgressEvent();
        Log.i("ota","otakankantubianzenmhuis:11111111:ready over ");
    }


    public void sendFileEnd(int size,byte[] md5Bytes){
        byte[] cmdEnd= HudCmdSendDataUtil.getReadySendFile(size,md5Bytes,false);
        BlueToothGattSendMsgManager.getInstance().setFileMsgNum(msgIndex+1);
        BlueToothGattSendMsgManager.getInstance().sendMsgByFile(msgIndex, cmdEnd);
        needProgressNumb=(msgIndex+1)/79;
//        Log.i("tag","kaishi1111111111:9333--"+(msgIndex+1)+"--"+(msgIndex+2)+"--"+cmdEnd.length+"---"+BleByteUtil.parseByte2HexStr(cmdEnd));
//        BleHintEventSubscriptionSubject.getInstance().sendImageEnd(w,h,size,useTime,index);
    }


    private BlueToothSendStatuManager.RetrunByteListen retrunByteListen=new BlueToothSendStatuManager.RetrunByteListen() {
        private final String startOk="A5A6";
        private final String endOk="A5A6";
        @Override
        public void getRetrunByte(byte[] retrunBytes) {
                if(null==retrunBytes){
                    return;
                }
                Log.i("ota","otakankantubianzenmhuis:DoType:1--"+BleByteUtil.parseByte2HexStr(retrunBytes)+"----"+(System.currentTimeMillis()-startTime));
                CheckRetrunByteAndToDoType toDoType=CheckRetrunByteAndToDoType.useRetrunBytesGet(retrunBytes);
                Log.i("ota","otakankantubianzenmhuis:DoType:2--"+toDoType);
                switch (toDoType){
                    case READY_OK:
                        Log.i("ota","otakankantubianzenmhuis:ota----4");
                        progress=19;
                        sendProgressEvent();
                        over(size,md5Bytes);
//                        BlueToothSendStatuManager.getInstance().setToDoType(toDoType);
                        Log.i("ota","otakankantubianzenmhuis:11111111:ready ok");
                        break;
                    case END_OK:
                        if(null!=listen){
                            long endTime=System.currentTimeMillis();
                            long useAllTime=endTime-startTime;
                            listen.sendOver(useAllTime);
                            progress=100;
                            sendProgressEvent();
                            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEND_OTA_END);
                        }
                        break;
                    case SEND_INDEX_OK:
                        BlueToothSendStatuManager.getInstance().setToDoType(toDoType);
                        break;
                    default:
                        if(null!=listen){
                            listen.sendOver(0);
                            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEND_OTA_ERR,toDoType);
                        }
                        break;
                }
        }
    };

    private BlueToothSendStatuManager.SendOverListen sendOverListen=new BlueToothSendStatuManager.SendOverListen() {

        private int sendNumb;
        @Override
        public void sendMsgOver(byte[] sendCmd, long startTime, long useTime) {

            if(null==sendCmd||sendCmd.length==0){
                return;
            }
            if(TextUtils.isEmpty(checkStr)){
                sendNumb++;
                if(sendNumb%needProgressNumb==0) {
                    int nowP = sendNumb / needProgressNumb;
                    progress = nowP + 20;
                    sendProgressEvent();
                }
            }else {
                Log.i("ota","otakankantubianzenmhuis:ota----2");
                String sendStr=BleByteUtil.parseByte2HexStr(sendCmd);
                if(sendStr.equals(checkStr)){
                    checkStr=null;
                    progress=4;
                    sendProgressEvent();
//                  BlueToothSendStatuManager.getInstance().setSendOverListen(null);
                    BlueToothSendStatuManager.getInstance().needRetrunByteListenDo(true,retrunByteListen);
                    splitPacketForMtuByte(fileBytes);
                    Log.i("ota","otakankantubianzenmhuis:ota----3");
                }
            }

        }

        @Override
        public void sendFileOver(long startTime, long useTime) {
            if(null!=listen){
                long endTime=System.currentTimeMillis();
                long useAllTime=endTime-startTime;
                listen.sendOver(useAllTime);
                progress=100;
                sendProgressEvent();
                BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEND_OTA_END);
            }
        }
    };

    private void sendProgressEvent(){
        BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SEND_OTA_PROGRESS,progress);
    }



}
