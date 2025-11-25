package com.newbee.ble_tool.activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.newbee.ble_lib.NewBeeBleManager;
import com.newbee.ble_tool.R;


import com.newbee.ble_tool.type.HudDevice;
import com.newbee.ble_tool.util.HudBleByteUtil;
import com.newbee.ble_tool.util.HudCmdType;
import com.newbee.ble_tool.util.HudSendImageType;
import com.newbee.bulid_lib.mybase.LG;
import com.newbee.bulid_lib.mybase.activity.BaseCompatActivity;

import com.newbee.t800_lib.type.T800CmdType;
import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageEndInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageStartInfoBean;
import com.nrmyw.ble_event_lib.send.BleEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventObserver;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.type.BleSendBitmapQualityType;


import java.nio.charset.StandardCharsets;


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
    private TextView bleTV,bleStatuTV;
    private Button initBT,searchBT,disconnectedBT,sendTestBT,sendImageTestBT,sendProgressBT;
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
                }


        }
    };

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

            if(null!=bleDeviceBean&&null!=bleTV){
                bleTV.setText(HudDevice.values()[bleDeviceBean.getDeviceType()].name());
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
                        doSendImageStartThing(startInfoBean);
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

                    break;
            }
        }
    };
    private long lastTime;

    private void doSendImageStartThing(BleSendImageStartInfoBean startInfoBean){
        BleDeviceBean bleDeviceBean= NewBeeBleManager.getInstance().getNowUseBleDevice();
        byte[] startBytes= null;
        HudDevice hudDevice=HudDevice.values()[bleDeviceBean.getDeviceType()];
        switch (hudDevice){
            case T800:
                startBytes= HudBleByteUtil.getAllByte(HudCmdType.READY_SEND_IMAGE,startInfoBean.getW(),startInfoBean.getH(),startInfoBean.getSize(), HudSendImageType.START);
                break;
            default:
                startBytes=HudBleByteUtil.getAllByte(HudCmdType.READY_SEND_IMAGE,startInfoBean.getW(),startInfoBean.getH(),startInfoBean.getSize(), HudSendImageType.START,startInfoBean.getType());
                break;
        }
        if(null!=startBytes){
            BleEventSubscriptionSubject.getInstance().sendImageIndexCmd(0,startBytes);
        }

    }

    private void doSendImageEndThing(BleSendImageEndInfoBean endInfoBean){
        BleDeviceBean bleDeviceBean= NewBeeBleManager.getInstance().getNowUseBleDevice();
        byte[] endBytes=null;
        HudDevice hudDevice=HudDevice.values()[bleDeviceBean.getDeviceType()];
        switch (hudDevice){
            case T800:
                endBytes=HudBleByteUtil.getAllByte(HudCmdType.READY_SEND_IMAGE,endInfoBean.getW(),endInfoBean.getH(),endInfoBean.getSize(), HudSendImageType.END);
                break;
            default:
                endBytes=HudBleByteUtil.getAllByte(HudCmdType.READY_SEND_IMAGE,endInfoBean.getW(),endInfoBean.getH(),endInfoBean.getSize(), HudSendImageType.END,endInfoBean.getType());
                break;
        }

        if(null!=endBytes){
            BleEventSubscriptionSubject.getInstance().sendImageIndexCmd(endInfoBean.getIndex(),endBytes);
        }




    }


    @Override
    public int getViewLayoutRsId() {
        return R.layout.activity_main_test;
    }

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
        ivv=findViewById(R.id.ivv);
        initBT.setOnClickListener(onClickListener);
        searchBT.setOnClickListener(onClickListener);
        disconnectedBT.setOnClickListener(onClickListener);
        sendTestBT.setOnClickListener(onClickListener);
        sendImageTestBT.setOnClickListener(onClickListener);
        sendProgressBT.setOnClickListener(onClickListener);
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
}
