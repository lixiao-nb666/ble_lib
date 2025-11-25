package com.newbee.ble_lib.util;


import android.graphics.Bitmap;
import android.util.Log;

import com.nrmyw.ble_event_lib.config.NewBeeBleConfig;
import com.nrmyw.ble_event_lib.type.BleSendBitmapQualityType;


public class BleSendImageUtil {





    public static byte[] bitmap2Bytes(Bitmap bm, BleSendBitmapQualityType qualityType) {
        if(null==qualityType){
            qualityType=BleSendBitmapQualityType.LOW;
        }
        int qualityV= qualityType.getQualityV();
        if(qualityV<=0){
            qualityV=15;
        }else if(qualityV>100){
            qualityV=60;
        }
        return AbImageUtil.bitmap2Bytes(bm, Bitmap.CompressFormat.JPEG,true,qualityV);
    }



    public static Bitmap autoScaleBitmap(Bitmap bm,int maxW,int maxH,float zoomScaling){
        maxW= (int) (maxW*zoomScaling);
        maxH= (int) (maxH*zoomScaling);
        float wmW=bm.getWidth();
        float wmH=bm.getHeight();
        if(maxW>=wmW&&maxH>=wmH){
            Log.i("kankantupian","kankantubianzenmhuishi:1111--12345---"+bm.getByteCount());
            return bm;
        }
        float needScaleX =maxW/wmW;
        float needScaleY =maxH/wmH;
        float needScale=needScaleX<needScaleY?needScaleX:needScaleY;
//        needScale=needScale/5*3;
//        Bitmap newBitMap=AbImageUtil.scaleImg(bm,needScale);
        Bitmap newBitMap=ImageCompressUtils.scaleBitmap(bm, (int) (wmW*needScale), (int) (wmH*needScale));

//        newBitMap.setConfig(Bitmap.Config.RGB_565);
        Log.i("kankantupian","kankantubianzenmhuishi:1111--123456:"+newBitMap.getConfig());
        return newBitMap;
    }



}
