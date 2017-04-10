package com.example.well.ndemo.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.example.well.ndemo.MyApplication;
import com.example.well.ndemo.ui.Interface.SucceedOrFaild;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ${LuoChen} on 2017/4/10 13:45.
 * email:luochen0519@foxmail.com
 * <p>
 * 图片的工具类
 */

public class ImageUtils {



    /**
     * 保存图片到指定路径
     *
     * @param bitmap
     * @param fileName
     */
    public static void saveImage(Bitmap bitmap, String fileName) {
        File dir = new File(Environment.getExternalStorageDirectory(), SettingsUtils.SD_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        File file = new File(dir, fileName);

        if (file.exists()) {
            return;
        }

        try {

            FileOutputStream outputStream = new FileOutputStream(file);//如果文件不存在 会自动创建
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();


            notifyImageSaved(file);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 保存图片到指定路径并添加回调
     *
     * @param bitmap
     * @param fileName
     * @param succeedOrFaild
     */
    public static void saveImage(Bitmap bitmap, String fileName, SucceedOrFaild succeedOrFaild) {
        if(bitmap==null || TextUtils.isEmpty(fileName))  return;

        File dir = new File(Environment.getExternalStorageDirectory(), SettingsUtils.SD_DIR);

        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        File file = new File(dir, fileName);

        if (file.exists()) {
            if (succeedOrFaild != null) {
                succeedOrFaild.succeed();
            }
            return;
        }

        try {

            FileOutputStream outputStream = new FileOutputStream(file);//如果文件不存在 会自动创建
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            notifyImageSaved(file);

            if (succeedOrFaild != null) {
                succeedOrFaild.succeed();
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (succeedOrFaild != null) {
                succeedOrFaild.faild();
            }
        }
    }

    /**
     * 通知图库更新
     *
     * @param file
     */
    private static void notifyImageSaved(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        MyApplication.getInstence().sendBroadcast(intent);
    }


}
