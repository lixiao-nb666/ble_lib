package com.newbee.ble_tool.app;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.newbee.ble_lib.NewBeeBleManager;
import com.newbee.ble_tool.R;
import com.newbee.ble_tool.config.T800Config;

import com.newbee.ble_tool.type.HudDevice;
import com.newbee.bulid_lib.mybase.appliction.BaseApplication;
import com.newbee.bulid_lib.mybase.share.MyShare;
import com.newbee.gson_lib.gson.MyGson;
import com.nrmyw.ble_event_lib.bean.BleDeviceBean;
import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;


public class MyApp extends BaseApplication {
    public static final String bleDeviceShareStr="bleDeviceShareStr";
    @Override
    protected void init() {
        NewBeeBleConfig.getInstance().init(T800Config.isAutomatic,T800Config.mtu,T800Config.serviceID,T800Config.writeID,T800Config.noticeID, HudDevice.getBleDeviceTypeList());
        NewBeeBleManager.getInstance().init(getBaseContext());

        try {
            Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:1:---1152");
            String bleDeviceShareStr= MyShare.getInstance().getString(MyApp.bleDeviceShareStr);
            if(!TextUtils.isEmpty(bleDeviceShareStr)){
                Log.i("tryToConnectOldDevice","tryToConnectOldDevice2222:1:---1153");
                BleDeviceBean bleDeviceBean= MyGson.getInstance().fromJson(bleDeviceShareStr,BleDeviceBean.class);
                NewBeeBleManager.getInstance().setShareBleDevice(bleDeviceBean);
            }
        }catch (Exception e){}


        startTo();
    }

    @Override
    protected void needClear(String str) {

    }

    @Override
    protected void close() {
        NewBeeBleManager.getInstance().close();
    }

    private void startTo() {
        String CHANNEL_ID = "99";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel Channel = null;
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Channel = new NotificationChannel(CHANNEL_ID, "Main Service", NotificationManager.IMPORTANCE_LOW);
            Channel.enableLights(true);
            Channel.setLightColor(Color.RED);
            Channel.setShowBadge(true);
            Channel.setDescription("PowerStateService");
            Channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); //设置锁屏可见 VISIBILITY_PUBLIC=可见
            manager.createNotificationChannel(Channel);
            notification = new Notification.Builder(this)
                    .setChannelId(CHANNEL_ID)
                    .setContentTitle("Main Service")//标题
                    .setContentText("Running...")//内容
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)//小图标一定需要设置,否则会报错(如果不设置它启动服务前台化不会报错,但是你会发现这个通知不会启动),如果是普通通知,不设置必然报错
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .build();
//            NewBeeBleManager.getInstance().setNotification(notification);
//            NewBeeBleManager.getInstance().setNotificationId(11);
        }
    }


}
