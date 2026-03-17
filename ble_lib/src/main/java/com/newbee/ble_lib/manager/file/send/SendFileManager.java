package com.newbee.ble_lib.manager.file.send;

import android.bluetooth.BluetoothManager;
import android.text.TextUtils;
import android.util.Log;

import com.newbee.ble_lib.manager.child.BlueToothGattManager;
import com.newbee.ble_lib.manager.child.BlueToothSendStatuManager;
import com.newbee.ble_lib.manager.file.BlueToothGattGetFileDataThread;
import com.newbee.ble_lib.manager.file.BlueToothGattSendFile;
import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.newbee.ble_lib.util.BleSendImageUtil;
import com.newbee.ble_lib.util.CryptoUtils;
import com.newbee.ble_lib.util.FileAndByteUtil;
import com.newbee.ble_lib.util.hud.HudBleByteUtil;
import com.newbee.ble_lib.util.hud.HudCmdSendDataUtil;
import com.nrmyw.ble_event_lib.bean.BleSendFileInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageEndInfoBean;
import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.util.BleByteUtil;

public class SendFileManager implements BlueToothGattSendFile {

    private BleSendFileInfoBean bleSendFileInfoBean;

    private boolean isStart;
    private long startTime;
    public SendFileManager(BleSendFileInfoBean bleSendFileInfoBean){
        this.bleSendFileInfoBean=bleSendFileInfoBean;
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
        Log.i("tag","kaishi1111111111:3");
        if(null==bleSendFileInfoBean|| TextUtils.isEmpty(bleSendFileInfoBean.getFilePath()) ){
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
        byte[] fileBytes= FileAndByteUtil.getBytesByFile(bleSendFileInfoBean.getFilePath());
        if(null==fileBytes||fileBytes.length==0){
            listen.sendOver(0);
            return;
        }

        byte[] md5Bytes = CryptoUtils.getMD5(fileBytes);
        int size=fileBytes.length;
        Log.i("tag","kaishi1111111111:999--"+size);
        msgIndex=0;
        sendFileStart(size,md5Bytes);
        splitPacketForMtuByte(fileBytes);
        over(size,md5Bytes);
        Log.i("tag","kaishi1111111111:9");
    }


    private int msgIndex;
    private void sendFileStart(int size,byte[] md5Bytes){
//        BlueToothSendStatuManager.getInstance().setWaitTime(50);
//        byte[] startBytes={(byte)0xA5,(byte)0xA6};
//        BlueToothSendStatuManager.getInstance().setCanStartSendCmdbytes(startBytes);
//        byte[] canSendBytes={(byte)0xA5,(byte)0xA7};
//        BlueToothSendStatuManager.getInstance().setCheckRetrunCanSendCmdbytes(canSendBytes);
//        byte[] reSendBytes={(byte)0xA5,(byte)0xA8};
//        BlueToothSendStatuManager.getInstance().setCheckRetrunReSendCmdbytes(reSendBytes);
//        byte[] endBytes={(byte)0xA5,(byte)0xA9};
//        BlueToothSendStatuManager.getInstance().setSendEndCmdbytes(endBytes);
        BlueToothSendStatuManager.getInstance().needRetrunByteListenDo(false,retrunByteListen);
        BlueToothGattSendMsgManager.getInstance().readySendFile();
//        byte[] cmdStart= HudCmdSendDataUtil.getReadySendFile(size,md5Bytes,true);
//        Log.i("tag","发送 ===  :发送成功---checkReturnToSend 3322111122:count"+ BleByteUtil.parseByte2HexStr(cmdStart));
//        BlueToothGattSendMsgManager.getInstance().sendMsgByCmd(cmdStart);
//        BlueToothGattSendMsgManager.getInstance().sendMsgByFile(msgIndex,cmdStart);
//        Log.i("tag","kaishi1111111111:9333--start:"+msgIndex+"--"+size+"---"+BleByteUtil.parseByte2HexStr(cmdStart));
    }

    private void splitPacketForMtuByte(byte[] fileBytes){
        if(!(null == fileBytes)){
            int dataIndex=0;
            int mtu= NewBeeBleConfig.getInstance().getRealMtu()-22;
            Log.i("kankanfasongtupian", "-------------kankanshenmegui:111---555666--999--????????" +mtu+"--"+fileBytes.length);
            Log.i("kankanfasongtupian", "-------------kankanshenmegui:111---555666--99911111--????????" + BleByteUtil.parseByte2HexStr(fileBytes));
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
        }

    }

    private void over(int size,byte[] md5Bytes){
        long endTime=System.currentTimeMillis();
        long useTime=endTime-startTime;
        sendFileEnd(useTime,size,md5Bytes);
        isStart=false;
        clear();
        if(null!=listen){
            listen.sendOver(useTime);
        }
        Log.i("tag","kaishi1111111111:9333--"+useTime);
    }


    public void sendFileEnd(long useTime,int size,byte[] md5Bytes){

        byte[] cmdEnd= HudCmdSendDataUtil.getReadySendFile(size,md5Bytes,false);
        BlueToothGattSendMsgManager.getInstance().setFileMsgNum(msgIndex+1);
        BlueToothGattSendMsgManager.getInstance().sendMsgByFile(msgIndex, cmdEnd);
        Log.i("tag","kaishi1111111111:9333--"+(msgIndex+1)+"--"+(msgIndex+2)+"--"+cmdEnd.length+"---"+BleByteUtil.parseByte2HexStr(cmdEnd));
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
                Log.i("kankantodoType","kankantoDoType:1--"+BleByteUtil.parseByte2HexStr(retrunBytes));
                CheckRetrunByteAndToDoType toDoType=CheckRetrunByteAndToDoType.useRetrunBytesGet(retrunBytes);
                Log.i("kankantodoType","kankantoDoType:2--"+toDoType);
                BlueToothSendStatuManager.getInstance().setToDoType(toDoType);

        }
    };




}
