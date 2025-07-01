package com.newbee.ble_lib.util;


import android.graphics.Bitmap;
import android.util.Log;

import com.newbee.ble_lib.config.BlueToothGattConfig;
import com.newbee.ble_lib.manager.image.BitmapQualityType;


public class BleSendImageUtil {





    public static byte[] bitmap2Bytes(Bitmap bm, BitmapQualityType qualityType) {
                if(null==qualityType){
                    qualityType=BitmapQualityType.LOW;
                }
                int qualityV=200;
                switch (qualityType){
                    case ULTRA_LOW:
                        qualityV=6;
                        break;
                    case LOW:
                        qualityV=20;
                        break;
                    case DEF:
                        qualityV=40;
                        break;
                    case HIGH:
                        qualityV=65;
                        break;
                    case ULTRA_HIGH:
                        qualityV=100;
                        break;
                }
                return AbImageUtil.bitmap2Bytes(bm, Bitmap.CompressFormat.JPEG,true,qualityV);
    }



    public static Bitmap autoScaleBitmap(Bitmap bm){
        float w= BlueToothGattConfig.getInstance().getImageW();
        float h= BlueToothGattConfig.getInstance().getImageH();
        float wmW=bm.getWidth();
        float wmH=bm.getHeight();
        if(w>=wmW&&h>=wmH){
            return bm;
        }
        float needScaleX =w/wmW;
        float needScaleY =h/wmH;
        float needScale=needScaleX<needScaleY?needScaleX:needScaleY;
        Bitmap newBitMap=AbImageUtil.scaleImg(bm,needScale);
        return newBitMap;
    }



}
