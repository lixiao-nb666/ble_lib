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

import com.newbee.ble_lib.bean.BleDeviceBean;
import com.newbee.ble_lib.bean.BleSendImageEndInfoBean;
import com.newbee.ble_lib.bean.BleSendImageStartInfoBean;
import com.newbee.ble_lib.config.BlueToothGattConfig;
import com.newbee.ble_lib.event.statu.BleStatu;
import com.newbee.ble_lib.event.statu.BleStatuEventObserver;
import com.newbee.ble_lib.event.statu.BleStatuEventSubscriptionSubject;
import com.newbee.ble_lib.manager.BleManager;
import com.newbee.ble_lib.manager.child.BleConnectManager;
import com.newbee.ble_lib.service.BluetoothGattServiceDao;
import com.newbee.ble_lib.util.BleByteUtil;
import com.newbee.ble_tool.R;
import com.newbee.ble_tool.type.BleDeviceType;
import com.newbee.t800_lib.type.T800CmdType;
import com.newbee.t800_lib.util.T800SendUtil;

import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private BleStatuEventObserver bleStatuEventObserver=new BleStatuEventObserver() {

        @Override
        public void sendBleStatu(BleStatu bleStatu, Object... objects) {
            Message msg=new Message();
            msg.what=bleStatu.ordinal();
            if(objects.length>0){
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
                        BleManager.getInstance().getEventImp().havePermissionInitBle();
                        break;
                    case R.id.bt_search:
                        BleManager.getInstance().getEventImp().startSearchBle();
                        break;
                    case R.id.bt_disconnected:
                        BleManager.getInstance().getEventImp().disconnectedBle();
                        break;
                    case R.id.bt_send_test:
                        T800CmdType t800CmdType=T800CmdType.TIME;
                        t800CmdType.useObjectSSetBody();
                        BleManager.getInstance().getEventImp().sendCmd(t800CmdType.getAllByte());
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
                    BleDeviceBean bleDeviceBean= BlueToothGattConfig.getInstance().getNowUseBleDevice();
                    if(null!=bleDeviceBean){
                        bleTV.setText(BleDeviceType.values()[bleDeviceBean.getDeviceType()].name());
                    }
                    break;
                case CONNECTED:
                    bleTV.append("-连接成功");
                    setViewByBleConnectStatu(BlueToothGattConfig.getInstance().isConnect());
                    break;
                case DISCONNECTED:
                    bleTV.setText("已经断开连接");
                    setViewByBleConnectStatu(BlueToothGattConfig.getInstance().isConnect());
                    break;
                case BLE_CAN_NOT_USE:
                case BLE_MANAGER_CAN_NOT_USE:
                case BLE_ADAPTER_CAN_NOT_USE:
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
        setViewByBleConnectStatu(BlueToothGattConfig.getInstance().isConnect());
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
