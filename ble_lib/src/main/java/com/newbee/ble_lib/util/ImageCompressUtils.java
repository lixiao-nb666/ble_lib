package com.newbee.ble_lib.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompressUtils {


//    /**
//     * 压缩图片到指定最大大小（单位 KB），并返回压缩后的字节数组
//     *
//     * @param context       上下文
//     * @param uri           图片 Uri
//     * @param maxKb         最大允许大小（KB）
//     * @param format        压缩格式（Bitmap.CompressFormat）
//     * @param inSampleSize  采样率（2 的幂次，例如 1, 2, 4, 8...）
//     * @return              压缩后的图片字节数组
//     */
//    public static byte[] compressImage(Context context, Bitmap bitmap,int needW,int needH int maxKb, Bitmap.CompressFormat format) throws IOException {
//        int w=bitmap.getWidth();
//        int h=bitmap.getHeight();
//        int inSampleSize= calculateInSampleSize(w,h,needW,needH);
//        bitmap.o
//        Bitmap bitmap = decodeBitmapFromUri(context, uri, inSampleSize);
//        return compressBitmap(bitmap, maxKb, format);
//    }
//
//
//    private Bitmap compressSampling(Bitmap bitmap){
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 2;
//
//        return BitmapFactory.decodeByteArray()
//    }




    /**
     * 压缩图片到指定最大大小（单位 KB），并返回压缩后的字节数组
     *
     * @param context       上下文
     * @param uri           图片 Uri
     * @param maxKb         最大允许大小（KB）
     * @param format        压缩格式（Bitmap.CompressFormat）
     * @param inSampleSize  采样率（2 的幂次，例如 1, 2, 4, 8...）
     * @return              压缩后的图片字节数组
     */
    public static byte[] compressImage(Context context, Uri uri, int maxKb, Bitmap.CompressFormat format, int inSampleSize) throws IOException {
        Bitmap bitmap = decodeBitmapFromUri(context, uri, inSampleSize);
        return compressBitmap(bitmap, maxKb, format);
    }

    /**
     * 缩放并压缩图片到指定宽高和大小
     *
     * @param context       上下文
     * @param uri           图片 Uri
     * @param targetWidth   目标宽度
     * @param targetHeight  目标高度
     * @param maxKb         最大允许大小（KB）
     * @param format        压缩格式
     * @param inSampleSize  采样率（2 的幂次）
     * @return              压缩后的图片字节数组
     */
    public static byte[] compressToSize(Context context, Uri uri, int targetWidth, int targetHeight, int maxKb, Bitmap.CompressFormat format, int inSampleSize) throws IOException {
        Bitmap original = decodeBitmapFromUri(context, uri, inSampleSize);
        Bitmap scaled = scaleBitmap(original, targetWidth, targetHeight);
        return compressBitmap(scaled, maxKb, format);
    }

    /**
     * 从 Uri 解码图片，支持指定采样率，并自动处理 EXIF 旋转
     */
    private static Bitmap decodeBitmapFromUri(Context context, Uri uri, int inSampleSize) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        InputStream inputStream = resolver.openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize; // 设置采样率

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();

        // 处理 EXIF 旋转
        ExifInterface exif = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            exif = new ExifInterface(resolver.openInputStream(uri));
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                break;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true);
    }

    /**
     * 缩放图片到指定宽高
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int targetWidth, int targetHeight) {
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }

    /**
     * 压缩 Bitmap 到指定大小以内（KB）
     */
    public static byte[] compressBitmap(Bitmap bitmap, int maxKb, Bitmap.CompressFormat format) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(format, quality, outputStream);
        Log.i("kankantupian","kankantubianzenmhuishi:1111333--444");
        while (outputStream.toByteArray().length > maxKb * 1024 && quality > 10) {
            outputStream.reset();
            quality -= 5;
            bitmap.compress(format, quality, outputStream);
//            Log.i("kankantupian","kankantubianzenmhuishi:1111333--555--"+outputStream.toByteArray().length);
        }
        Log.i("kankantupian","kankantubianzenmhuishi:1111333--666--"+outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    /**
     * 自动计算合适的采样率（inSampleSize）
     *
     * @param actualWidth   实际宽度
     * @param actualHeight  实际高度
     * @param reqWidth      请求宽度
     * @param reqHeight     请求高度
     * @return              合适的采样率
     */
    public static int calculateInSampleSize(int actualWidth, int actualHeight, int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        if (actualHeight > reqHeight || actualWidth > reqWidth) {
            int halfWidth = actualWidth / 2;
            int halfHeight = actualHeight / 2;

            while ((halfWidth / inSampleSize) >= reqWidth
                    && (halfHeight / inSampleSize) >= reqHeight) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
