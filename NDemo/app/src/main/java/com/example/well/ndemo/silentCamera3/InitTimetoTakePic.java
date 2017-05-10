package com.example.well.ndemo.silentCamera3;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Handler;
import android.os.Message;
import android.support.compat.BuildConfig;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.well.ndemo.silentCamera.Config4Camera;
import com.example.well.ndemo.utils.SettingsUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static com.igexin.sdk.GTServiceManager.context;

/**
 * 设置定时拍照功能--->这里进行了相机的初始化和设置
 *
 * @author <p>
 *         创建定时拍照任务
 *         cameraType  摄像头
 *         resolutionString  分辨率
 *         tvSaveLocation 保存地址
 *         etExtension  拓展名
 *         cameraStart, 开始拍摄时间
 *         cameraNumber, 拍摄次数
 *         cameraStop  拍摄张数
 */
public class InitTimetoTakePic {

    private static InitTimetoTakePic mInstance;
    private static int cameraType = 1;
    Context mContext;
    static FrameLayout mSurfaceViewFrame;
    private static Camera mCamera;
    private static CameraPreview mPreview;
    private static String resolutionString = "1920x1080";
    private static String saveLocation = AppUtils.getSDCardPath();
    private static String extension = "JPG";
    private static String cameraStart = "1";
    private static String cameraNumber = "1";
    private static String cameraStop = "10";
    private static int number = 0;
    private static boolean clearVoice = false;
    private Intent intent;

    private InitTimetoTakePic(Context context) {
        this.mContext = context;
    }

    public synchronized static InitTimetoTakePic getInstance(Context context) {
        mInstance = null;
        mInstance = new InitTimetoTakePic(context);

        return mInstance;
    }

    public void initView(FrameLayout surfaceViewFrame) {
        mSurfaceViewFrame = surfaceViewFrame;
    }

    /**
     * 启动定时拍照并上传功能
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (BuildConfig.DEBUG) Log.e("InitTimetoTakePic", "mHandler  case 1");
                    initCarema();
                    break;
                case 2:
                    if (BuildConfig.DEBUG) Log.e("InitTimetoTakePic", "mCamera="+mCamera);
                    if (mCamera == null) {
                        releaseCarema();
                        number = 0;
                        mHandler.removeCallbacksAndMessages(null);
                    } else {
                        if (number < Integer.valueOf(cameraStop)) {
                            mCamera.autoFocus(new AutoFocusCallback() {
                                @Override
                                public void onAutoFocus(boolean success, Camera camera) {
                                    // 从Camera捕获图片
                                    LogUtils.e("自动聚焦111" + success);
                                    try {
                                        mCamera.takePicture(null, null, mPicture2);
//                                        mHandler.sendEmptyMessageDelayed(1, Integer.valueOf(cameraNumber) * 1000);//循环拍照
                                    } catch (Exception e) {
                                        releaseCarema();
                                        mHandler.removeCallbacksAndMessages(null);
                                    }
                                }
                            });
                        } else {
                            releaseCarema();
                            number = 0;
                            mHandler.removeCallbacksAndMessages(null);
                        }
                    }
                    break;
            }
        }
    };

    public void start() {
        if (BuildConfig.DEBUG) Log.e("InitTimetoTakePic", "start");
        mHandler.sendEmptyMessageDelayed(1, 1 * 1000); //1s 后开始启动相机
    }

    private void initCarema() {
        LogUtils.e("initCarema");
        if (mCamera == null) {
            LogUtils.e("camera=null");
            mCamera = getCameraInstance();
            mPreview = new CameraPreview(mContext, mCamera);
            mSurfaceViewFrame.removeAllViews();
            mSurfaceViewFrame.addView(mPreview);//将预览视图添加到桌面的父容器中
        }
        LogUtils.v(mCamera == null ? "mCamera is null" : "mCamera is not null");
        mCamera.startPreview();
        mHandler.sendEmptyMessageDelayed(2, Integer.valueOf(cameraStart) * 1000); //3s后拍照
    }

    /**
     * 检测设备是否存在Camera硬件
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // 存在
            return true;
        } else {
            // 不存在
            return false;
        }
    }

    /**
     * 打开一个Camera
     */
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(cameraType);
            c.setDisplayOrientation(90);
            Camera.Parameters mParameters = c.getParameters();
            //快门声音
//            c.enableShutterSound(clearVoice);
            //可以用得到当前所支持的照片大小，然后
            //List<Size> ms = mParameters.getSupportedPictureSizes();
            //mParameters.setPictureSize(ms.get(0).width, ms.get(0).height);  //默认最大拍照取最大清晰度的照片
            String[] xes = resolutionString.split("x");
            // LogUtils.i("ms.get(0).width==>"+ms.get(0).width);
            // LogUtils.i("ms.get(0).height==>"+ms.get(0).height);
            // LogUtils.i("Integer.valueOf(xes[0])==>"+Integer.valueOf(xes[0]));
            // LogUtils.i("Integer.valueOf(xes[1])==>"+Integer.valueOf(xes[1]));
            mParameters.setPictureSize(Integer.valueOf(xes[0]), Integer.valueOf(xes[1]));  //默认最大拍照取最大清晰度的照片
            c.setParameters(mParameters);
            if (BuildConfig.DEBUG) Log.e("InitTimetoTakePic", "打开Camera成功");
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("InitTimetoTakePic", "打开Camera失败失败");
        }
        return c;
    }

    private PictureCallback mPicture2 = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 使用当前的时间拼凑图片的名称
            String name = DateFormat.format("yyyy_MM_dd_hhmmss",
                    Calendar.getInstance(Locale.CHINA))
                    + ".jpg";
            File file = new File(Config4Camera.POTOPATH);
            if (!file.exists() || !file.isDirectory()) {
                file.mkdirs(); // 创建文件夹保存照片
            }
            String filename = file.getPath() + File.separator + name;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                    data.length);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream( filename);
                boolean b = bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                Config4Camera.silentPotoList.add(filename);
                    /*
					 * if (b) { Toast.makeText(mContext, "照片保存成功",
					 * Toast.LENGTH_LONG) .show(); } else {
					 * Toast.makeText(mContext, "照片保存失败", Toast.LENGTH_LONG)
					 * .show(); }
					 */

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
//                releaseCamera(camera);// 释放Camera
                releaseCarema();
                sendImageIntent();
            }
        }
    };

    private void sendImageIntent() {
        Intent intent = new Intent();
        intent.setAction(SettingsUtils.ACTION_SILENT_MASTER_OK);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * 释放摄像头资源
     */
    private void releaseCamera(Camera camera) {
        if (camera != null) {
            try {
                camera.setPreviewDisplay(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 获取Jpeg图片，并保存在sd卡上
            String path = saveLocation;
            if (BuildConfig.DEBUG) Log.e("InitTimetoTakePic", "path="+path);
            File dirF = new File(path);
            if (!dirF.exists() || !dirF.isDirectory()) {
                dirF.mkdirs();
            }
            File pictureFile = new File(path + "/" + System.currentTimeMillis() + "." + extension);//扩展名
            if (BuildConfig.DEBUG) Log.e("InitTimetoTakePic", "pictureFile="+pictureFile);
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                LogUtils.v("保存图成功");
                number++;
                intent = new Intent();
                intent.setAction("CameraFragment.start");
                intent.putExtra("number", number);
                mContext.sendBroadcast(intent);
            } catch (Exception e) {
                LogUtils.v("保存图片失败");
                e.printStackTrace();
            }
            releaseCarema();
        }
    };

    public void releaseCarema() {
        if (mCamera != null) {
            Log.e("InitTimetoTakePic", "releaseCarema");
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
