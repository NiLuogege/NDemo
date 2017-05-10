package com.example.well.ndemo.silentCamera;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.SurfaceView;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.MyApplication;

/**
 * 静默拍照类
 */
public class SilentCamera {

    static Camera camera;

    /**
     * 初始化设置Camera.打开摄像头 默认是先打开前置摄像头，如果没有前置摄像头的话就打开后置摄像头
     */
    public static void openCamera() {
        Camera.CameraInfo cameraInfo = new CameraInfo();
        // 获得设备上的硬件camera数量
        int count = Camera.getNumberOfCameras();

        if (BuildConfig.DEBUG) Log.e("SilentCamera", "摄像头数量= " + count);

        for (int i = 0; i < count; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//前置摄像头
                try {
                    if (BuildConfig.DEBUG) Log.e("SilentCamera", " 尝试打开前置摄像头");
                    camera = Camera.open(i); // 尝试打开前置摄像头
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (camera == null) {//如果没有前置摄像头才起打开后置摄像头
            for (int i = 0; i < count; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//后置摄像头
                    try {
                        if (BuildConfig.DEBUG) Log.e("SilentCamera", "尝试打开后置摄像头");
                        camera = Camera.open(i); // 尝试打开后置摄像头
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        try {
            if (camera != null) {
                SurfaceView dummy = new SurfaceView(MyApplication.getInstence());
                camera.setPreviewDisplay(dummy.getHolder());
                camera.startPreview(); // 打开预览画面
                camera.autoFocus(new SilentAutoFocusCallback()); // 自动聚焦
            } else {
                // Toast.makeText(mContext, "没有前置摄像头",
                // Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
