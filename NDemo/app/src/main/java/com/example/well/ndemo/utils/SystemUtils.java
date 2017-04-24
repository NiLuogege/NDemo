package com.example.well.ndemo.utils;

import android.content.res.Resources;

/**
 * Created by ${LuoChen} on 2017/4/24 17:01.
 * email:luochen0519@foxmail.com
 * 获取系统级数据的工具类
 */

public class SystemUtils {

    public static int dp2px(int dp, Resources resources) {

        float density = resources.getDisplayMetrics().density;

        if (density > 0.0f) {

            return (int) Math.ceil(density * dp);

        } else {
            return dp;
        }
    }

    public static float dp2px(float dp, Resources resources) {

        float density = resources.getDisplayMetrics().density;

        if (density > 0.0f) {
            return density * dp;
        } else {
            return dp;
        }
    }

    public static int px2dp(float px, Resources resources) {
        float scale = resources.getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
