package com.newbee.ble_tool.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.newbee.ble_lib.HudBleManager;
import com.newbee.ble_tool.R;
import com.newbee.ble_tool.type.BleDeviceType;
import com.newbee.bulid_lib.mybase.LG;
import com.newbee.t800_lib.type.T800CmdType;
import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageEndInfoBean;
import com.nrmyw.ble_event_lib.bean.BleSendImageStartInfoBean;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventObserver;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;


public class MainActivity extends AppCompatActivity {

    private BleStatuEventObserver bleStatuEventObserver=new BleStatuEventObserver() {

        @Override
        public void sendBleStatu(BleStatu bleStatu, Object... objects) {
            Message msg=new Message();
            msg.what=bleStatu.ordinal();
            if(objects.length==1){
                msg.obj=objects[0];
            }
            if(objects.length==2){
                msg.arg1= (int) objects[0];
                msg.obj=objects[1];
            }
            handler.sendMessage(msg);
        }
    };

    private TextView bleTV,bleStatuTV;
    private Button initBT,searchBT,disconnectedBT,sendTestBT;
    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(null==v){
                return;
            }
            try {
                switch (v.getId()){
                    case R.id.bt_init:
                        HudBleManager.getInstance().getEventImp().havePermissionInitBle();
                        break;
                    case R.id.bt_search:
                        HudBleManager.getInstance().getEventImp().startSearchBle();
                        break;
                    case R.id.bt_disconnected:
                        HudBleManager.getInstance().getEventImp().disconnectedBle();
                        break;
                    case R.id.bt_send_test:
                        T800CmdType t800CmdType=T800CmdType.TIME;
                        t800CmdType.useObjectSSetBody();
                        HudBleManager.getInstance().getEventImp().sendCmd(t800CmdType.getAllByte());
                        break;
                }
            }catch (Exception e){
                //可能报空指针异常
            }

        }
    };
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            BleStatu bleStatu=BleStatu.values()[msg.what];
            bleStatuTV.setText(getResources().getText(bleStatu.getStrId()));
            switch (bleStatu){
                case CONNECTING:
                    BleDeviceBean bleDeviceBean= HudBleManager.getInstance().getNowUseBleDevice();
                    if(null!=bleDeviceBean){
                        bleTV.setText(BleDeviceType.values()[bleDeviceBean.getDeviceType()].name());
                    }
                    break;
                case CONNECTED:
                    bleTV.append("-连接成功");
                    setViewByBleConnectStatu(HudBleManager.getInstance().isConnect());
                    break;
                case DISCONNECTED:
                    bleTV.setText("已经断开连接");
                    setViewByBleConnectStatu(HudBleManager.getInstance().isConnect());
                    break;
                case RUN_ERR:
                    String errStr=getResources().getText(msg.arg1)+":"+msg.obj.toString();
                    bleStatuTV.append(errStr);
                    LG.i("lixiaokankanerrStr","lixiaokankanerrStr:"+errStr);
                    break;
                case SEND_IMAGE_START:
                    BleSendImageStartInfoBean startInfoBean= (BleSendImageStartInfoBean) msg.obj;
                    break;
                case SEND_IMAGE_END:
                    BleSendImageEndInfoBean endInfoBean= (BleSendImageEndInfoBean) msg.obj;
                    break;
            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bleTV=findViewById(R.id.tv_ble);
        bleStatuTV=findViewById(R.id.tv_ble_statu);
        initBT=findViewById(R.id.bt_init);
        searchBT=findViewById(R.id.bt_search);
        disconnectedBT=findViewById(R.id.bt_disconnected);
        sendTestBT=findViewById(R.id.bt_send_test);
        initBT.setOnClickListener(onClickListener);
        searchBT.setOnClickListener(onClickListener);
        disconnectedBT.setOnClickListener(onClickListener);
        sendTestBT.setOnClickListener(onClickListener);
        setViewByBleConnectStatu(HudBleManager.getInstance().isConnect());
        BleStatuEventSubscriptionSubject.getInstance().attach(bleStatuEventObserver);
//        BleHintEventSubscriptionSubject.getInstance().attach(bleHintEventObserver);

    }


    public void setViewByBleConnectStatu(boolean isConnected){
        if(isConnected){
            initBT.setVisibility(View.GONE);
            searchBT.setVisibility(View.GONE);
            disconnectedBT.setVisibility(View.VISIBLE);
            sendTestBT.setVisibility(View.VISIBLE);
        }else {
            initBT.setVisibility(View.VISIBLE);
            searchBT.setVisibility(View.VISIBLE);
            disconnectedBT.setVisibility(View.GONE);
            sendTestBT.setVisibility(View.GONE);
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
