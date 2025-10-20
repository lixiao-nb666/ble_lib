package com.newbee.ble_tool.activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.newbee.ble_lib.NewBeeBleManager;
import com.newbee.ble_tool.R;
import com.newbee.ble_tool.type.BleDeviceType;
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
import com.nrmyw.ble_event_lib.util.BleByteUtil;

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
    private Button initBT,searchBT,disconnectedBT,sendTestBT,sendImageTestBT;
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
                        T800CmdType t800CmdType=T800CmdType.TIME;
                        t800CmdType.useObjectSSetBody();
                        NewBeeBleManager.getInstance().getEventImp().sendCmd(t800CmdType.getAllByte());
                        break;
                    case R.id.bt_send_test_image:
                        Bitmap bt1 = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.img_test_1);
                        BleSendImageInfoBean bleSendImageInfoBean=new BleSendImageInfoBean();
                        bleSendImageInfoBean.setBitmap(bt1);
                        bleSendImageInfoBean.setBitmapQualityType(BleSendBitmapQualityType.ULTRA_HIGH);
                        BleEventSubscriptionSubject.getInstance().sendImage(bleSendImageInfoBean);

                        break;
                }


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
            switch (bleStatu){
                case CONNECTING:
                    BleDeviceBean bleDeviceBean= NewBeeBleManager.getInstance().getNowUseBleDevice();
                    if(null!=bleDeviceBean){
                        bleTV.setText(BleDeviceType.values()[bleDeviceBean.getDeviceType()].name());
                    }
                    break;
                case CONNECTED:
                    bleTV.append("-连接成功");
                    setViewByBleConnectStatu(NewBeeBleManager.getInstance().isConnect());
                    break;
                case DISCONNECTED:
                    bleTV.setText("已经断开连接");
                    setViewByBleConnectStatu(NewBeeBleManager.getInstance().isConnect());
                    break;
                case RUN_ERR:
                case NONE:
                case CONNECTING_ERR:
                    if(null!=msg.obj){
                        bleTV.append(msg.obj.toString());
                    }
                    break;
                case SEND_IMAGE_START:
                    if(msg.obj instanceof BleSendImageStartInfoBean){
                        BleSendImageStartInfoBean startInfoBean= (BleSendImageStartInfoBean) msg.obj;
                        String startSS="A55A000F1000AF0104000008FA0100";
                        BleEventSubscriptionSubject.getInstance().sendImageIndexCmd(0, startSS.getBytes(StandardCharsets.UTF_8));
                    }

                    break;
                case SEND_IMAGE_END:
                    if(msg.obj instanceof BleSendImageEndInfoBean){
                        BleSendImageEndInfoBean endInfoBean= (BleSendImageEndInfoBean) msg.obj;
                        String endSS="A55A000F1000AF0104000008FA0000";

                        BleEventSubscriptionSubject.getInstance().sendImageIndexCmd(endInfoBean.getIndex(), endSS.getBytes(StandardCharsets.UTF_8));
                    }
                    break;
            }
        }
    };


    @Override
    public int getViewLayoutRsId() {
        return R.layout.activity_main11;
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
        initBT.setOnClickListener(onClickListener);
        searchBT.setOnClickListener(onClickListener);
        disconnectedBT.setOnClickListener(onClickListener);
        sendTestBT.setOnClickListener(onClickListener);
        sendImageTestBT.setOnClickListener(onClickListener);
        setViewByBleConnectStatu(NewBeeBleManager.getInstance().isConnect());
        BleStatuEventSubscriptionSubject.getInstance().attach(bleStatuEventObserver);
//        BleHintEventSubscriptionSubject.getInstance().attach(bleHintEventObserver);
    }

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
