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

import com.newbee.ble_lib.config.BleManagerConfig;
import com.newbee.ble_lib.manager.msg.BlueToothGattSendMsgManager;
import com.newbee.ble_lib.util.BleConnectStatuUtil;
import com.newbee.ble_lib.util.BleErrManager;
import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;
import com.nrmyw.ble_event_lib.util.BleByteUtil;
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

            BlueToothSendStatuManager.getInstance().sendBytesIsOk(status);
        }



        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            /*if (status == BluetoothGatt.GATT_SUCCESS) {}*/


            if (status == BluetoothGatt.GATT_SUCCESS) {
//                bluetoothGatt.requestMtu(NewBeeBleConfig.getInstance().getMtu());
//                BleConnectStatuUtil.getInstance().sendConnected();

            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);


            if(mtu>= BleManagerConfig.CAN_SEND_MTU){
                NewBeeBleConfig.getInstance().setRealMtu(mtu-BleManagerConfig.MTU_CHA);
            }else {
                NewBeeBleConfig.getInstance().setRealMtu(mtu);
            }


//            if (BluetoothGatt.GATT_SUCCESS == status){
////                bluetoothGatt.discoverServices();
//
//
//
//                Log.e(tag,"设置MTU值成功"+mtu );
//            }else {
//                Log.e(tag,"设置MTU值失败"+mtu);
//                NewBeeBleConfig.getInstance().setRealMtu(mtu-5);
////                bluetoothGatt.discoverServices();
//            }
            bluetoothGatt.discoverServices();
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                // 开始查找服务
//                bluetoothGatt.discoverServices();
//            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                isReady = false;
//            }


            connecting=false;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //广播里面已经发送了事件通知，这里就不用处理了

//                if(connectionPriority!=BluetoothGatt.CONNECTION_PRIORITY_HIGH){
//                    Log.w(tag,"BluetoothAdapter  initialized  11122----------4");
//                    connectionPriority=BluetoothGatt.CONNECTION_PRIORITY_HIGH;
//                    bluetoothGatt.requestConnectionPriority(connectionPriority);
//                }
                Log.i("ble_code_check","ble_code_check:connect set HIGH");
                bluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_BALANCED);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.i("ble_code_check","ble_code_check:connect reset mtu");
                    bluetoothGatt.requestMtu(NewBeeBleConfig.getInstance().getMtu());
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //广播里面已经发送了事件通知，这里就不用处理了
                BleConnectStatuUtil.getInstance().sendDisconnected();
            }
        }

        /**
         * 重写onServicesDiscovered,发现蓝牙服务
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {

                //EventBus.getDefault().post(new EventBluetoothStateMessage(ACTION_GATT_SERVICES_DISCOVERED));
                displayGattServices();
            } else {

                // 8 ： 设备超出范围
                // 22 ：表示本地设备终止了连接
                // 133 ：连接超时或未找到设备。
                // status为错误的133时：调用gatt的disconnect方法，然后在onConnectionStateChange方法里调用Gatt的close方法并置空gatt并重新开始使用connectGatt方法进行连接

                BleConnectStatuUtil.getInstance().setConnectErr("OnservicesDiscovered receiced can found :" + status);
            }
        }

        /** 查找蓝牙对应服务和读写特征 **/
//        private void displayGattServices(){
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
//                            readCharacteristic=characteristic;
//                            setCharacteristicNotification(characteristic,true);
//                        }
//                        if (characteristic.getUuid().toString().equals(NewBeeBleConfig.getInstance().getWriteID())) {
//                            nowCanSend=true;
//                            writeCharacteristic = characteristic;
//                            BleConnectStatuUtil.getInstance().sendConnected();
//                        }
//                    }
////                    EventBus.getDefault().post(new EventBluetoothStateMessage(ACTION_GATT_SERVICES_DISCOVERED));
//                }
//            }
//            Log.w(tag,"BluetoothAdapter  initialized  111:222333444555");
//
//        }

        private void displayGattServices(){
            //查找服务
            if(!findService()){
                return;
            }
            //查找对应的2个特征
            if(!findCharacteristic()){
                return;
            }
            // 订阅Data In
            if(!setCharacteristicNotification1(readCharacteristic,true)){
                return;
            }


            BlueToothSendStatuManager.getInstance().initOk();
            BleConnectStatuUtil.getInstance().sendConnected();
        }

        private boolean findService(){
            dataService=bluetoothGatt.getService(UUID.fromString(NewBeeBleConfig.getInstance().getServiceID()));
            if (dataService == null) {
//            notifyOnError(OtaError.NOT_FOUND_OTA_SERVICE);
//                Timber.e("FOTA service not found");
                BleErrManager.sendConnectErrMsg("DATA service not found");

                return false;
            }
            return true;
        }

        private boolean findCharacteristic(){
            writeCharacteristic = dataService.getCharacteristic(UUID.fromString(NewBeeBleConfig.getInstance().getWriteID()));
            readCharacteristic = dataService.getCharacteristic(UUID.fromString(NewBeeBleConfig.getInstance().getNoticeID()));
            if (null == writeCharacteristic|| null ==readCharacteristic) {
//            notifyOnError(OtaError.NOT_FOUND_OTA_CHARACTERISTIC);
//                Timber.e("FOTA characteristic(s) not found");
                BleErrManager.sendConnectErrMsg("DATA characteristic(s) not found");

                return false;
            }
            return true;
        }


        /**
         * 特征值读
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {

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
            try {
                BlueToothSendStatuManager.getInstance().getRetrunBytes(characteristic.getValue());
            }catch (Exception e){

            }





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




    private void clearData(){
        BlueToothSendStatuManager.getInstance().initData();
        connecting=false;
        dataService = null;
        writeCharacteristic = null;
        readCharacteristic = null;
        if(null!=bluetoothGatt){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt=null;
        }
    }

    public void close(){
        clearData();
        BlueToothGattSendMsgManager.getInstance().close();
        BlueToothGattSendMsgManager.getInstance().close();
        blueToothGattManager=null;
    }

    public void checkIsDisConnecting(){
        clearData();
    }

    private boolean connecting=false;
    private long connectingTime;
    public synchronized boolean isConnecting(){
        if(connecting&&System.currentTimeMillis()-connectingTime>=BleManagerConfig.BLE_CONNECTING_WAIT_TIME){
            connecting=false;
        }
        return connecting;
    }



    public synchronized void initGatt(BluetoothDevice device, Context context,boolean isScanData){
        if(null!=bluetoothGatt||null!=readCharacteristic||null!=writeCharacteristic||null!=dataService){
            clearData();
        }
        connecting=true;
        connectingTime=System.currentTimeMillis();
        BlueToothSendStatuManager.getInstance().initData();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //自动回连有个问题，如果数据量大的情况下，手机系统自动回连会按默认的mtu值传输
//            Log.w(tag,"BluetoothAdapter  initialized  111"+NewBeeBleConfig.getInstance().isAutoConnect());
//            bluetoothGatt = device.connectGatt(context, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
//        } else {
//
//            bluetoothGatt = device.connectGatt(context, false, mGattCallback);
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.w(tag,"BluetoothAdapter  initialized  11100"+NewBeeBleConfig.getInstance().isAutoConnect());
//            bluetoothGatt = device.connectGatt(context, false, mGattCallback, BluetoothDevice.TRANSPORT_LE, BluetoothDevice.PHY_LE_1M_MASK);
//        } else

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            bluetoothGatt = device.connectGatt(context, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);

        } else {

            bluetoothGatt = device.connectGatt(context, false, mGattCallback);

        }
        if(isScanData){

            Log.i("ble_code_check","ble_code_check:connect_mold:new_device");
            bluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
        }else {
            Log.i("ble_code_check","ble_code_check:connect_mold:old_device");
            bluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER);
        }

    }



    /**
     * 设置特征什变化通知
     */
    public boolean setCharacteristicNotification1(BluetoothGattCharacteristic characteristic, boolean enabled) {


        boolean setEnabled = bluetoothGatt.setCharacteristicNotification(readCharacteristic, enabled);
        if (!setEnabled) {
//            notifyOnError(OtaError.CAN_NOT_SUBSCRIBE_DATA_IN);
//                Timber.e("Error subscribe FOTA Data In");
            BleErrManager.sendConnectErrMsg("DATA Error subscribe  Data In");

            return false;
        }
        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if (clientConfig == null) {
//            notifyOnError(OtaError.NOT_FOUND_CLIENT_CHARACTERISTIC_CONFIG);
//                Timber.e("FOTA CCC not found");
            BleErrManager.sendConnectErrMsg("DATA characteristic(s) descriptor not found");
            return false;
        }
        if (enabled) {
            clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        bluetoothGatt.writeDescriptor(clientConfig);
        return true;
    }

    /**
     * 设置特征什变化通知
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if ( bluetoothGatt == null) {

            return;
        }
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if (enabled) {
            clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        bluetoothGatt.writeDescriptor(clientConfig);
    }








    long lastTime;
    synchronized SEND_CMD_STATU queSendCmd(byte[] cmd){
        if (bluetoothGatt!=null&&writeCharacteristic!=null){
            try {

                //if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU)
                //boolean b = mBluetoothGatt.writeCharacteristic(writeCharacteristic, cmd, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                writeCharacteristic.setValue(cmd);
                bluetoothGatt.writeCharacteristic(writeCharacteristic);
                long nowTime=System.currentTimeMillis();

                if(lastTime==0){
                    lastTime=nowTime;
                    Log.i("ble_code_check","ble_code_check:send data start");
                }else {

                    Log.i("ble_code_check","ble_code_check:send data end"+(nowTime-lastTime));
                    lastTime=nowTime;
                }



//              LogUtil.e("发送指令："+ CYUtils.Bytes2HexString(cmd));
                //这里设置flase，因为正在发送中

                return SEND_CMD_STATU.OK;
            }catch (Exception e){
                e.printStackTrace();
                //这里设置true，因为正在发送失败了

                return SEND_CMD_STATU.ERR;

            }

        }
        return SEND_CMD_STATU.FLASE;
    }


    public void disconnect() {
//        if(null!=writeCharacteristic){
//            writeCharacteristic=null;
//        }
        connecting=false;
        if (null!=bluetoothGatt ) {
            bluetoothGatt.disconnect();
//            bluetoothGatt=null;
        }

    }

    public enum SEND_CMD_STATU{
        OK,
        ERR,
        FLASE,
    }

}
