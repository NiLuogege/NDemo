package com.example.well.ndemo.utils;

/**
 * Created by ${LuoChen} on 2017/4/10 14:45.
 * email:luochen0519@foxmail.com
 * <p>
 * app中配置类
 */

public interface SettingsUtils {
    /**
     * SD卡中文件夹名称
     */
    String SD_DIR = "NDemo";

    /**
     * 地图路径保存文件夹名称
     */
    String RECORDPATH = "/NDemo/recordPath";

    /**
     * 外部内存权限的请求吗
     */
    int PREMIERE_REQUEST_CODE_STORAGE = 1;

    /**
     * sp的名称
     */
    String SP_NAME = "nDemo";

    /**
     * 夜间模式是否开启
     */
    String IS_NIGHT_ON = "IS_NIGHT_ON";

    /**
     * 推送了一张图片
     */
    String ACTION_IMAGE_PUSH = "COM_EXAMPLE_WELL_NDEMO_IMAGE_PUSH";

    /**
     * 缓存推送过来图片的URL
     */
    String CATCH_IMAGE_PUSH = "CATCH_IMAGE_PUSH";

    /**
     *是否加载推送来的图片
     */
    String IS_LOAD_PUSH_IMAGE = "IS_LOAD_PUSH_IMAGE";

    /**
     *是否第一次进入app
     */
    String IS_FIRST_ENTER = "IS_FIRST_ENTER";

    /**
     *ShareSDK 的appkey
     */
    String SHAREAPPKEY = "1d12fcf88ad69";

    /**
     *mapActivity中最后一个定位点
     */
    String LASTLATLNG="LASTLATLNG";
}
