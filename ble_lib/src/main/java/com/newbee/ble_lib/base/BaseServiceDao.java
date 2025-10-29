package com.newbee.ble_lib.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;


/**
 * @author lixiaogege!
 * @description: one day day ,no zuo no die !
 * @date :2021/1/18 0018 9:50
 */
public abstract class BaseServiceDao {
    private final String tag=getClass().getSimpleName()+">>>>";
    private Context context;
    private ServiceConnection sc;
    private BaseService ss;
    protected abstract Class getSsCls();



    public BaseServiceDao(Context context) {
        this.context = context.getApplicationContext();
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                BaseService.BaseServiceBinder serviceBinder = ( BaseService.BaseServiceBinder) service;
                ss = serviceBinder.getService();
                serviceBinder.setStatuListen(getStatuListen());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                ss=null;
            }
        };
    }



    private String actionStr;
    public void setActionStr(String actionStr){
        this.actionStr=actionStr;
    }

    protected BaseService.StatuListen getStatuListen(){
        return null;
    }


    public void startServiceIsBind() {
        Intent intent = new Intent(context, getSsCls());
        if(!TextUtils.isEmpty(actionStr)){
            intent.setAction(actionStr);
        }
        context.bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }




    public void stopServiceIsBind() {
        context.unbindService(sc);
    }


    public void startService() {
        Intent intent = new Intent(context, getSsCls());
        if(!TextUtils.isEmpty(actionStr)){
            intent.setAction(actionStr);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent);
        }else {
            context.startService(intent);
        }
    }

    public void stopService() {
        context.stopService(new Intent(context,getSsCls()));
    }

    public BaseService getService(){
        return ss;
    }


}
