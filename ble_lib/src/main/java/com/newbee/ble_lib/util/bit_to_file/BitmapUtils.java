package com.newbee.ble_lib.util.bit_to_file;

import android.graphics.Bitmap;
import android.os.Environment;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
    
    /**
     * 将Bitmap保存为文件
     * @param bitmap 要保存的Bitmap对象
     * @param fileName 保存的文件名（不含路径）
     * @param format 图片格式（JPEG/PNG/WEBP）
     * @param quality 压缩质量（0-100）
     * @return 保存成功的File对象，失败返回null
     */
    public static File bitmapToFile(Bitmap bitmap, String fileName, Bitmap.CompressFormat format, int quality) {
        // 检查存储权限
        if (!isExternalStorageWritable()) {
            return null;
        }
        
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, fileName);
        
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imageFile));
            bitmap.compress(format, quality, bos);
            bos.flush();
            bos.close();
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 检查外部存储是否可写
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    
    /**
     * 简化方法：使用默认参数保存为JPEG
     */
    public static File bitmapToJpegFile(Bitmap bitmap, String fileName) {
        return bitmapToFile(bitmap, fileName, Bitmap.CompressFormat.JPEG, 85);
    }
}