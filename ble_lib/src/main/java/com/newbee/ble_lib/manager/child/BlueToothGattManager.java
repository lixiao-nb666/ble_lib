package com.newbee.ble_lib.manager.child;



import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.newbee.ble_lib.NewBeeBleManager;
import com.newbee.ble_lib.manager.image.BlueToothGattSendImageManager;
import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;



import com.newbee.ble_lib.util.BleConnectStatuUtil;

import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;
import com.nrmyw.ble_event_lib.statu.BleStatu;
import com.nrmyw.ble_event_lib.statu.BleStatuEventSubscriptionSubject;
import com.nrmyw.ble_event_lib.util.BleByteUtil;


import java.util.List;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BlueToothGattManager {
    private final String tag=getClass().getName()+">>>>";
    private static BlueToothGattManager blueToothGattManager;
    private BluetoothGatt bluetoothGatt;

    private BluetoothGattService dataService;
    private BluetoothGattCharacteristic writeCharacteristic;//写数据特征值
    private BluetoothGattCharacteristic readCharacteristic;//写数据特征值
    /* 连接远程设备的回调函数 */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            /* 蓝牙反馈回调 */
//            EventBus.getDefault().post(new EventBluetoothStateMessage(ACTION_GATT_WRITE_STATE,status));
            Log.i(tag,"发送 ===  :发送成功"+status);
            Log.w(tag,"BluetoothAdapter  initialized  111:1");
            nowCanSend=true;
            if(BlueToothGattSendImageManager.getInstance().checkNowSendImage()){
                BlueToothGattSendImageManager.getInstance().queToSendCmd();
            }else {
                BlueToothGattSendMsgManager.getInstance().setNowCanSend();
            }
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.CAN_SEND_DATA);
            Log.i(tag,"收到指令没111");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            /*if (status == BluetoothGatt.GATT_SUCCESS) {}*/
            Log.e(tag,"写入值成功22:" + status);
            Log.w(tag,"BluetoothAdapter  initialized  111:2");

            if (status == BluetoothGatt.GATT_SUCCESS) {
//                bluetoothGatt.requestMtu(NewBeeBleConfig.getInstance().getMtu());
//                BleConnectStatuUtil.getInstance().sendConnected();
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.w(tag,"BluetoothAdapter  initialized  111:3");
            NewBeeBleConfig.getInstance().setMtu(mtu);
            if (BluetoothGatt.GATT_SUCCESS == status){
                Log.e(tag,"设置MTU值成功:" + bluetoothGatt.discoverServices());
            }else {
                Log.e(tag,"设置MTU值失败");
                bluetoothGatt.discoverServices();
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                // 开始查找服务
//                bluetoothGatt.discoverServices();
//            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                isReady = false;
//            }

            Log.w(tag,"BluetoothAdapter  initialized  111:4");

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //广播里面已经发送了事件通知，这里就不用处理了
                Log.e(tag,"连接成功Connected to GATT server ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    bluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                    bluetoothGatt.requestMtu(NewBeeBleConfig.getInstance().getMtu());
                }
                bluetoothGatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //广播里面已经发送了事件通知，这里就不用处理了
                Log.e(tag,"连接失败 Disconnected from GATT server "+newState+" === "+status);
                pause();
                BleConnectStatuUtil.getInstance().sendDisconnected();
            }

        }

        /**
         * 重写onServicesDiscovered,发现蓝牙服务
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.w(tag,"BluetoothAdapter  initialized  111:5");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(tag,"--发现服务onServicesDiscovered called--");
                //EventBus.getDefault().post(new EventBluetoothStateMessage(ACTION_GATT_SERVICES_DISCOVERED));
                displayGattServices();
            } else {
                Log.w(tag,"OnservicesDiscovered receiced:" + status);
                // 8 ： 设备超出范围
                // 22 ：表示本地设备终止了连接
                // 133 ：连接超时或未找到设备。
                // status为错误的133时：调用gatt的disconnect方法，然后在onConnectionStateChange方法里调用Gatt的close方法并置空gatt并重新开始使用connectGatt方法进行连接

                BleConnectStatuUtil.getInstance().setConnectErr("OnservicesDiscovered receiced can found :" + status);
            }
        }

        /** 查找蓝牙对应服务和读写特征 **/
        private void displayGattServices(){
//            List<BluetoothGattService> servicesList = bluetoothGatt.getServices();
//            for (int i = 0; i < servicesList.size(); i++) {
//                BluetoothGattService service = servicesList.get(i);
//                Log.e(tag,"onServicesDiscovered: 服务" + i + "+" + service.getUuid().toString());
//                if (service.getUuid().equals(UUID.fromString(NewBeeBleConfig.getInstance().getServiceID()))) {
//                    List<BluetoothGattCharacteristic> characteristicsList = service.getCharacteristics();
//                    for (int j = 0; j < characteristicsList.size(); j++) {
//                        BluetoothGattCharacteristic characteristic = characteristicsList.get(j);
////                      ？  LogUtil.e("onServicesDiscovered: 特征" + j + "+" + characteristic.getUuid().toString());
//                        if (characteristic.getUuid().toString().equals(NewBeeBleConfig.getInstance().getNoticeID())) {
//                            setCharacteristicNotification(characteristic,true);
//                        }
//                        if (characteristic.getUuid().toString().equals(NewBeeBleConfig.getInstance().getWriteID())) {
//                            nowCanSend=true;
//                            writeCharacteristic = characteristic;
//                        }
//                    }
////                    EventBus.getDefault().post(new EventBluetoothStateMessage(ACTION_GATT_SERVICES_DISCOVERED));
//                }
//            }

                aaa();
        }

        private void aaa(){
            dataService=bluetoothGatt.getService(UUID.fromString(NewBeeBleConfig.getInstance().getServiceID()));
            if (dataService == null) {
//            notifyOnError(OtaError.NOT_FOUND_OTA_SERVICE);
//                Timber.e("FOTA service not found");
                BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,com.nrmyw.ble_event_lib.R.string.ble_statu_connecting_err,"DATA service not found");
                return;
            }
            writeCharacteristic = dataService.getCharacteristic(UUID.fromString(NewBeeBleConfig.getInstance().getWriteID()));
            readCharacteristic = dataService.getCharacteristic(UUID.fromString(NewBeeBleConfig.getInstance().getNoticeID()));

            if (writeCharacteristic == null || readCharacteristic == null) {
//            notifyOnError(OtaError.NOT_FOUND_OTA_CHARACTERISTIC);
//                Timber.e("FOTA characteristic(s) not found");
                BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,com.nrmyw.ble_event_lib.R.string.ble_statu_connecting_err,"DATA characteristic(s) not found");
                return;
            }
            // 订阅Data In
            setCharacteristicNotification(readCharacteristic,true);
            BleConnectStatuUtil.getInstance().sendConnected();
        }

        /**
         * 特征值读
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.w(tag,"BluetoothAdapter  initialized  111:6");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(tag,"--onCharacteristicRead called--");
                /////将数据通过通知到页面
//                eventUpdate(characteristic);
            }
        }

        /**
         * 特征值改变
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            eventUpdate(characteristic);
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RETRUN_BYTES,characteristic.getValue());
//            BleCmdRetrunEventSubscriptionSubject.getInstance().getCmd(characteristic.getValue());
//            byte[] value =characteristic.getValue() ;
//            Log.e( "接收--->端口数 据： --------------start"+ T800CmdRetrunType.GET_BRIGHTNESS.getType());
//            for (byte b:value){
//                Log.e( "接收--->端口数 据： " + b);
//            }
//            Log.e( "接收--->端口数 据： --------------end" );


        }

    };




    private BlueToothGattManager(){
        BlueToothGattSendMsgManager.getInstance();
    }

    public static BlueToothGattManager getInstance(){
        if(null==blueToothGattManager){
            synchronized (BlueToothGattManager.class){
                if(null==blueToothGattManager){
                    blueToothGattManager=new BlueToothGattManager();
                }
            }
        }
        return blueToothGattManager;
    }


    private void pause(){
        dataService = null;
        writeCharacteristic = null;
        readCharacteristic = null;
        if(null!=bluetoothGatt){
            bluetoothGatt.close();

        }
    }

    public void close(){
        BlueToothGattSendMsgManager.getInstance().close();
        writeCharacteristic=null;
        if(null!=bluetoothGatt){
            bluetoothGatt.close();
            bluetoothGatt=null;
        }

    }


    public void initGatt(BluetoothDevice device,Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //自动回连有个问题，如果数据量大的情况下，手机系统自动回连会按默认的mtu值传输
            Log.w(tag,"BluetoothAdapter  initialized  111");
            bluetoothGatt = device.connectGatt(context, NewBeeBleConfig.getInstance().isAutoConnect(), mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            Log.w(tag,"BluetoothAdapter  initialized  122");
            bluetoothGatt = device.connectGatt(context, NewBeeBleConfig.getInstance().isAutoConnect(), mGattCallback);
        }
    }

    /**
     * 设置特征什变化通知
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {


        boolean setEnabled = bluetoothGatt.setCharacteristicNotification(readCharacteristic, enabled);
        if (!setEnabled) {
//            notifyOnError(OtaError.CAN_NOT_SUBSCRIBE_DATA_IN);
//                Timber.e("Error subscribe FOTA Data In");
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,com.nrmyw.ble_event_lib.R.string.ble_statu_connecting_err,"DATA Error subscribe  Data In");
            return;
        }
        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if (clientConfig == null) {
//            notifyOnError(OtaError.NOT_FOUND_CLIENT_CHARACTERISTIC_CONFIG);
//                Timber.e("FOTA CCC not found");
            BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.RUN_ERR,com.nrmyw.ble_event_lib.R.string.ble_statu_connecting_err,"DATA characteristic(s) not found");
            return;
        }
        if (enabled) {
            clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        bluetoothGatt.writeDescriptor(clientConfig);
    }





    boolean nowCanSend;
    long lastSendTime;

    public boolean isNowCanSend(){
        long nowTime=System.currentTimeMillis();
        if(nowTime-lastSendTime>3000){
            nowCanSend=true;
        }
        if(nowCanSend){
            lastSendTime=nowTime;
        }
        return nowCanSend;
    }


   public void queSendCmd(byte[] cmd){
        if (bluetoothGatt!=null&&writeCharacteristic!=null){
            try {
                //if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU)
                //boolean b = mBluetoothGatt.writeCharacteristic(writeCharacteristic, cmd, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                writeCharacteristic.setValue(cmd);
                bluetoothGatt.writeCharacteristic(writeCharacteristic);
                BleStatuEventSubscriptionSubject.getInstance().sendBleStatu(BleStatu.SENDING_DATA,cmd);
                Log.i("kankanfasongtupian","-------------kankanshenmegui111:"+ BleByteUtil.parseByte2HexStr(cmd));
//              LogUtil.e("发送指令："+ CYUtils.Bytes2HexString(cmd));
                nowCanSend=false;
            }catch (Exception e){
                e.printStackTrace();
                nowCanSend=true;
                BlueToothGattSendMsgManager.getInstance().setNowCanSend();
            }
        }
    }


    public void disconnect() {
//        if(null!=writeCharacteristic){
//            writeCharacteristic=null;
//        }
        if (null!=bluetoothGatt ) {
            bluetoothGatt.disconnect();
//            bluetoothGatt=null;
        }

    }

}
