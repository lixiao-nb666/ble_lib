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

import com.newbee.ble_lib.NewBeeBleManager;
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
    private Button initBT,searchBT,disconnectedBT,sendTestBT;
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
                    }

                    break;
                case SEND_IMAGE_END:
                    if(msg.obj instanceof BleSendImageEndInfoBean){
                        BleSendImageEndInfoBean endInfoBean= (BleSendImageEndInfoBean) msg.obj;
                    }
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
        setViewByBleConnectStatu(NewBeeBleManager.getInstance().isConnect());
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
