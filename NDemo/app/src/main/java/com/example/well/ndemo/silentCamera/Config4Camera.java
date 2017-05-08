package com.example.well.ndemo.silentCamera;

import android.os.Environment;

import com.example.well.ndemo.utils.SettingsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 相机配置类
 */
public class Config4Camera {
    //存储路径
	public static String POTOPATH =   Environment.getExternalStorageDirectory()+ SettingsUtils.IMAGE_SILENTPATH;
	public static List<String> silentPotoList= new ArrayList<String>();

}
