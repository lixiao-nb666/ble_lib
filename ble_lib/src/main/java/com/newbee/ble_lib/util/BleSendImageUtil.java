package com.newbee.ble_lib.util;


import android.graphics.Bitmap;
import android.util.Log;

import com.newbee.ble_lib.config.BlueToothGattConfig;

import com.nrmyw.ble_event_lib.type.BleSendBitmapQualityType;


public class BleSendImageUtil {





    public static byte[] bitmap2Bytes(Bitmap bm, BleSendBitmapQualityType qualityType) {
                if(null==qualityType){
                    qualityType=BleSendBitmapQualityType.LOW;
                }
                int qualityV= qualityType.getQualityV();
                if(qualityV<0){
                    qualityV=15;
                }else if(qualityV>=100){
                    qualityV=80;
                }


//                switch (qualityType){
//                    case ULTRA_LOW:
//                        qualityV=6;
//                        break;
//                    case LOW:
//                        qualityV=15;
//                        break;
//                    case DEF:
//                        qualityV=30;
//                        break;
//                    case HIGH:
//                        qualityV=50;
//                        break;
//                    case ULTRA_HIGH:
//                        qualityV=75;
//                        break;
//                }
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
