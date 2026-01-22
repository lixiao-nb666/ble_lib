package com.newbee.ble_tool.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.newbee.ble_lib.NewBeeBleManager;
import com.newbee.ble_lib.util.BleSendImageUtil;
import com.newbee.ble_lib.util.FileAndByteUtil;

import com.newbee.ble_lib.util.hud.HudCmdSendDataUtil;
import com.newbee.ble_tool.R;


import com.newbee.ble_tool.type.HudDevice;

import com.newbee.ble_tool.util.HudBleByteUtil;
import com.newbee.ble_tool.util.HudCmdType;
import com.newbee.ble_tool.util.HudImageShowType;
import com.newbee.ble_tool.util.HudSendImageType;
import com.newbee.bulid_lib.mybase.LG;
import com.newbee.bulid_lib.mybase.activity.BaseCompatActivity;

import com.newbee.t800_lib.type.T800CmdType;
import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.bean.BleSendFileInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageEndInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageStartInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendOtaInfoBean;
import com.nrmyw.ble_event_lib.send.BleEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventObserver;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.type.BleSendBitmapQualityType;
import com.nrmyw.ble_event_lib.util.BleByteUtil;

import java.io.File;


public class MainActivity extends BaseCompatActivity {
    private BleStatuEventObserver bleStatuEventObserver=new BleStatuEventObserver() {
        @Override
        public void sendBleStatu(BleStatu bleStatu, Object... objects) {
            Message msg=new Message();
            msg.what=bleStatu.ordinal();
            if(objects.length==1){
                msg.obj=objects[0];
            }else if(objects.length==2){
                if(objects[0] instanceof Integer){
                    msg.arg1= (int) objects[0];
                }
                msg.obj=objects[1];
            }
            handler.sendMessage(msg);
        }
    };
    private TextView bleTV,bleStatuTV,logTV,progressTV;
    private Button initBT,searchBT,disconnectedBT,sendTestBT,sendImageTestBT,sendProgressBT,sendFileBT;
    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(null==v){
                return;
            }

                switch (v.getId()){
                    case R.id.bt_init:
                        NewBeeBleManager.getInstance().getEventImp().havePermissionInitBle();
                        break;
                    case R.id.bt_search:
                        NewBeeBleManager.getInstance().getEventImp().startSearchBle();
                        break;
                    case R.id.bt_disconnected:
                        NewBeeBleManager.getInstance().getEventImp().disconnectedBle();
                        break;
                    case R.id.bt_send_test:
                        handler.removeCallbacks(sendTimeRunnable);
                        handler.postDelayed(sendTimeRunnable,1000);
                        break;
                    case R.id.bt_send_test_image:
                        int rsId=R.mipmap.img_test_7;
                        if(isSend){
                            rsId=R.mipmap.hg_test;
                        }
                        Bitmap bt1 = BitmapFactory.decodeResource(MainActivity.this.getResources(),rsId);
//                        BleSendImageInfoBean bleSendImageInfoBean=new BleSendImageInfoBean();
//                        bleSendImageInfoBean.setBitmap(bt1);
//                        bleSendImageInfoBean.setBitmapQualityType(BleSendBitmapQualityType.ULTRA_LOW);
//                        BleEventSubscriptionSubject.getInstance().sendImage(bleSendImageInfoBean);
                        sendBitmap(bt1);
                        isSend=!isSend;
                        break;
                    case R.id.bt_send_test_progress:
                        Bitmap bt2 = BitmapFactory.decodeResource(MainActivity.this.getResources(),R.mipmap.img_test_jd);
                        sendProgress(bt2);
                        break;
                    case R.id.bt_send_test_file:
                        if(!canOta){

                            canOta=true;
                        }

                        BleEventSubscriptionSubject.getInstance().sendCmd(HudCmdSendDataUtil.startOTA());
                        lastTime=System.currentTimeMillis();
                        String filePath= Environment.getExternalStorageDirectory().getPath()+"/"+"DCIM/cpu1.bin";
                        byte[] fileBytes= FileAndByteUtil.getBytesByFile(filePath);
                        if(null==fileBytes||fileBytes.length==0){
                            showToast("err : file size =0" );
                            return;
                        }
                        size=fileBytes.length;
                        byte[] start=HudCmdSendDataUtil.getReadySendFile(size,null,true);
                        Log.i("tag","发送 ===  :发送成功---checkReturnToSend 3322111111:count"+ BleByteUtil.parseByte2HexStr(start));
                        BleEventSubscriptionSubject.getInstance().sendCmd(start);
                        logTV.setText("开始发送OTA");

                        //                        String filePath= Environment.getExternalStorageDirectory().getPath()+"/"+"DCIM/cpu1.bin";
//                        File file=new File(filePath);
//                        Log.i("kankanshifoucunzai","kankanshifoucunzai:"+filePath+"--"+file.exists());
//                        BleSendFileInfoBean bleSendFileInfoBean=new BleSendFileInfoBean();
//                        bleSendFileInfoBean.setFilePath(filePath);
//                        bleSendFileInfoBean.setFileName("cpu1.bin");
//                        BleEventSubscriptionSubject.getInstance().sendFile(bleSendFileInfoBean);

                        BleSendOtaInfoBean bleSendFileInfoBean=new BleSendOtaInfoBean();
                        bleSendFileInfoBean.setFilePath(filePath);
                        bleSendFileInfoBean.setFileName("cpu1.bin");

                        BleEventSubscriptionSubject.getInstance().sendOta(bleSendFileInfoBean);

                        break;
                }
        }
    };

    private int index;
    private boolean isSend;

    public void sendBitmap(Bitmap bitmap){
        BleSendImageInfoBean bleSendImageInfoBean=new BleSendImageInfoBean();
        bleSendImageInfoBean.setType(0);
        bleSendImageInfoBean.setMaxW(200);
        bleSendImageInfoBean.setMaxH(320);
        bleSendImageInfoBean.setBitmap(bitmap);
        BleSendBitmapQualityType.DEF.setQualityV(65);
        BleSendBitmapQualityType.DEF.setZoomScaling(0.65f);
        bleSendImageInfoBean.setBitmapQualityType(BleSendBitmapQualityType.DEF);
        BleEventSubscriptionSubject.getInstance().sendImage(bleSendImageInfoBean);
    }

    public void sendProgress(Bitmap bitmap){
        BleSendImageInfoBean bleSendImageInfoBean=new BleSendImageInfoBean();
        bleSendImageInfoBean.setType(1);
        bleSendImageInfoBean.setMaxW(22);
        bleSendImageInfoBean.setMaxH(320);
        bleSendImageInfoBean.setBitmap(bitmap);
        bleSendImageInfoBean.setBitmapQualityType(BleSendBitmapQualityType.PROGRESS);
        BleEventSubscriptionSubject.getInstance().sendImage(bleSendImageInfoBean);
    }

    private Runnable sendTimeRunnable=new Runnable() {
        @Override
        public void run() {
            T800CmdType t800CmdType=T800CmdType.TIME;
            t800CmdType.useObjectSSetBody();
            NewBeeBleManager.getInstance().getEventImp().sendCmd(t800CmdType.getAllByte());
            handler.removeCallbacks(sendTimeRunnable);
            handler.postDelayed(sendTimeRunnable,1000);
        }
    };
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            BleStatu bleStatu=BleStatu.values()[msg.what];
            bleStatuTV.setText(getResources().getText(bleStatu.getStrId()));
            if(msg.arg1!=0){
                bleTV.append("-"+getResources().getText(msg.arg1));
            }
            BleDeviceBean bleDeviceBean= NewBeeBleManager.getInstance().getNowUseBleDevice();
            if(null!=bleTV){
                if(null!=bleDeviceBean){
                    Log.w(tag,"BluetoothAdapter  initialized  11155----111188:"+bleDeviceBean.getDeviceName());
                    bleTV.setText(HudDevice.values()[bleDeviceBean.getDeviceType()].name());
                }else {
                    Log.w(tag,"BluetoothAdapter  initialized  11155----11118811");
                    bleTV.setText("");
                }
            }
            switch (bleStatu){
                case NONE:
                    if(null!=msg.obj&&msg.obj instanceof Bitmap){
                        Bitmap bitmap=(Bitmap) msg.obj;
                        if(null!=bitmap&&!bitmap.isRecycled()){
                            ivv.setImageBitmap(bitmap);
                        }
                    }
                    break;
                case CONNECTING:
                    break;
                case CONNECTED:
                    bleTV.append("-连接成功");
                    Log.i("","kankanshujufdajfls:"+bleDeviceBean.getPairFuntionType());
                    setViewByBleConnectStatu(NewBeeBleManager.getInstance().isConnect());
                    break;
                case DISCONNECTED:
                    bleTV.append("已经断开连接");
                    setViewByBleConnectStatu(NewBeeBleManager.getInstance().isConnect());
                    break;
                case RUN_ERR:

                case CONNECTING_ERR:
                    if(null!=msg.obj){
                        bleTV.append(msg.obj.toString());
                    }
                    break;
                case SEND_IMAGE_START:
                    if(msg.obj instanceof BleSendImageStartInfoBean){
                        BleSendImageStartInfoBean startInfoBean= (BleSendImageStartInfoBean) msg.obj;
//                        String startSS="A55A000F1000AF0104000008FA0100";
//                        BleEventSubscriptionSubject.getInstance().sendImageIndexCmd(0, startSS.getBytes(StandardCharsets.UTF_8));
                        lastTime=System.currentTimeMillis();
                        Log.i("kankantupian","kankantubianzenmhuishi:1111333--0000---"+startInfoBean.toString());
//                        doSendImageStartThing(startInfoBean);
                    }

                    break;
                case SEND_IMAGE_END:
                    if(msg.obj instanceof BleSendImageEndInfoBean){
                        BleSendImageEndInfoBean endInfoBean= (BleSendImageEndInfoBean) msg.obj;
                        Log.i("kankantupian","kankantubianzenmhuishi:1111333--9999---"+endInfoBean.toString());
                        doSendImageEndThing(endInfoBean);
                    }
                    break;
                case SEND_IMAGE_DATA_END:
                    long nowTime=System.currentTimeMillis();
                    Log.i("kankantupian","kankantubianzenmhuishi:1111333--8888---"+msg.obj.toString()+"---"+(nowTime-lastTime));
                    showToast("send image time :"+(nowTime-lastTime));
                    break;
                case RETRUN_BYTES:
                    byte[] data= (byte[]) msg.obj;
                    Log.i("kankantupian","aishi1111111111:9333---888999000---"+ BleByteUtil.parseByte2HexStr(data));
                    logTV.append("\n"+index+"-"+BleByteUtil.parseByte2HexStr(data));
//                    if(canOta){
//                        String filePath= Environment.getExternalStorageDirectory().getPath()+"/"+"DCIM/cpu1.bin";
//                        File file=new File(filePath);
//                        Log.i("kankanshifoucunzai","kankanshifoucunzai:"+filePath+"--"+file.exists());
//                        BleSendOtaInfoBean bleSendFileInfoBean=new BleSendOtaInfoBean();
//                        bleSendFileInfoBean.setFilePath(filePath);
//                        bleSendFileInfoBean.setFileName("cpu1.bin");
//
//                        BleEventSubscriptionSubject.getInstance().sendOta(bleSendFileInfoBean);
//                        canOta=false;
//                        index=-1;
//                    }

                    index++;

                    break;
                case SEND_OTA_PROGRESS:
                    int progress= (int) msg.obj;
                    progressTV.setText(progress+"%");
                    break;
            }
        }
    };
    private long lastTime;
    private int size;

//    private void doSendImageStartThing(BleSendImageStartInfoBean startInfoBean){
//        BleDeviceBean bleDeviceBean= NewBeeBleManager.getInstance().getNowUseBleDevice();
//        byte[] startBytes= null;
//        HudDevice hudDevice=HudDevice.values()[bleDeviceBean.getDeviceType()];
//        switch (hudDevice){
//            case T800:
//                startBytes= HudBleByteUtil.getAllByte(HudCmdType.READY_SEND_IMAGE,startInfoBean.getW(),startInfoBean.getH(),startInfoBean.getSize(), HudSendImageType.START);
//                break;
//            default:
//                startBytes=HudBleByteUtil.getAllByte(HudCmdType.READY_SEND_IMAGE,startInfoBean.getW(),startInfoBean.getH(),startInfoBean.getSize(), HudSendImageType.START,startInfoBean.getType());
//                break;
//        }
//        if(null!=startBytes){
//            BleEventSubscriptionSubject.getInstance().sendImageIndexCmd(0,startBytes);
//        }
//
//    }
//
    private void doSendImageEndThing(BleSendImageEndInfoBean endInfoBean){
        if(endInfoBean.getType()!=0){
            return;
        }
        byte[]   endBytes= HudBleByteUtil.getAllByte(HudCmdType.SHOW_IMAGE, HudImageShowType.SHOW);
        if(null!=endBytes){
            BleEventSubscriptionSubject.getInstance().sendBytesIndexCmd(endInfoBean.getIndex(),endBytes);
        }
    }


    @Override
    public int getViewLayoutRsId() {
        return R.layout.activity_main_test;
    }

    private boolean canOta;
    @Override
    public void initView() {
        bleTV=findViewById(R.id.tv_ble);
        bleStatuTV=findViewById(R.id.tv_ble_statu);
        initBT=findViewById(R.id.bt_init);
        searchBT=findViewById(R.id.bt_search);
        disconnectedBT=findViewById(R.id.bt_disconnected);
        sendTestBT=findViewById(R.id.bt_send_test);
        sendImageTestBT=findViewById(R.id.bt_send_test_image);
        sendProgressBT=findViewById(R.id.bt_send_test_progress);
        sendFileBT=findViewById(R.id.bt_send_test_file);
        progressTV=findViewById(R.id.tv_progress);
        ivv=findViewById(R.id.ivv);
        logTV=findViewById(R.id.tv_log);
        initBT.setOnClickListener(onClickListener);
        searchBT.setOnClickListener(onClickListener);
        disconnectedBT.setOnClickListener(onClickListener);
        sendTestBT.setOnClickListener(onClickListener);
        sendImageTestBT.setOnClickListener(onClickListener);
        sendProgressBT.setOnClickListener(onClickListener);
        sendFileBT.setOnClickListener(onClickListener);

        setViewByBleConnectStatu(NewBeeBleManager.getInstance().isConnect());
        BleStatuEventSubscriptionSubject.getInstance().attach(bleStatuEventObserver);
//        BleHintEventSubscriptionSubject.getInstance().attach(bleHintEventObserver);
        NewBeeBleManager.getInstance().nowGetAllPermissions();
    }
    private ImageView ivv;
    @Override
    public void initData() {

    }

    @Override
    public void initControl() {

    }

    @Override
    public void closeActivity() {

    }

    @Override
    public void viewIsShow() {
        requestPermission();
    }

    @Override
    public void viewIsPause() {

    }

    @Override
    public void changeConfig() {

    }




    public void setViewByBleConnectStatu(boolean isConnected){
        if(isConnected){
            initBT.setVisibility(View.GONE);
            searchBT.setVisibility(View.GONE);
            disconnectedBT.setVisibility(View.VISIBLE);
            sendTestBT.setVisibility(View.VISIBLE);
            sendImageTestBT.setVisibility(View.VISIBLE);
        }else {
            initBT.setVisibility(View.VISIBLE);
            searchBT.setVisibility(View.VISIBLE);
            disconnectedBT.setVisibility(View.GONE);
            sendTestBT.setVisibility(View.GONE);
            sendImageTestBT.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        BleStatuEventSubscriptionSubject.getInstance().detach(bleStatuEventObserver);
//        BleHintEventSubscriptionSubject.getInstance().detach(bleHintEventObserver);
    }

    private static final int REQUEST_MANAGE_FILES_ACCESS = 2;
    //申请所有文件访问权限
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //判断是否有管理外部存储的权限
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_MANAGE_FILES_ACCESS);
            } else {
                // TODO: 2023/11/22
                // 已有所有文件访问权限，可直接执行文件相关操作
            }
        } else {
            // TODO: 2023/11/22
            //非android11及以上版本，走正常申请权限流程
        }
    }


}
