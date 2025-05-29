package com.newbee.ble_lib.base;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public abstract class BaseService extends Service {
    protected String tag=getClass().getName()+">>>>";
    private IBinder baseBinder=new BaseServiceBinder();

    //初始化，只会调用一次
    public abstract void init();
    //开始，每次启动都回调用这里
    public abstract void start(Intent intent, int flags, int startId);
    //结束关闭
    public abstract void close();


    @Override
    public IBinder onBind(Intent intent) {
        return baseBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        isStart=true;
        if(null!=statuListen){
            statuListen.isStart();
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start(intent,flags,startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        close();
        super.onDestroy();
        isStart=false;
    }

    private boolean isStart=false;
    private StatuListen statuListen;

    public class BaseServiceBinder extends Binder {

        public BaseService getService(){
            return BaseService.this;
        }

        public void setStatuListen(StatuListen setStatuListen){
            if(null==setStatuListen){
                return;
            }
            statuListen=setStatuListen;
            if(isStart){
                statuListen.isStart();
            }
        }
    }

    public interface  StatuListen{

        public void isStart();

    }
}
