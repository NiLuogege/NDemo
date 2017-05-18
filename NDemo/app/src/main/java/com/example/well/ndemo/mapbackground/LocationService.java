package com.example.well.ndemo.mapbackground;

import android.content.Intent;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.bean.NodemoMapLocation;
import com.example.well.ndemo.ui.activity.MapActivity;

/**
 * 包名： com.amap.locationservicedemo
 * <p>
 * 创建时间：2016/10/27
 * 项目名称：LocationServiceDemo
 *
 * @author guibao.ggb
 * @email guibao.ggb@alibaba-inc.com
 * <p>
 * 类说明：后台服务定位
 * <p>
 * <p>
 * modeified by liangchao , on 2017/01/17
 * update:
 * 1. 只有在由息屏造成的网络断开造成的定位失败时才点亮屏幕
 * 2. 利用notification机制增加进程优先级
 * </p>
 */
public class LocationService extends NotiService {
    private LocationService context;
    private AMapLocationClient mLocationClient;
    public  static final String ACTION_NODEMOMAPLOCATION="NodemoMapLocation";
    public  static final String ACTION_AMAPLOCATION="AMapLocation";



    /**
     * 处理息屏关掉wifi的delegate类
     */
    private IWifiAutoCloseDelegate mWifiAutoCloseDelegate = new WifiAutoCloseDelegate();

    /**
     * 记录是否需要对息屏关掉wifi的情况进行处理
     */
    private boolean mIsWifiCloseable = false;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        this.context=this;
        applyNotiKeepMech(); //开启利用notification提高进程优先级的机制

        if (BuildConfig.DEBUG) Log.e("LocationService", "onStartCommand");

//        if (mWifiAutoCloseDelegate.isUseful(getApplicationContext())) {
//            mIsWifiCloseable = true;
//            mWifiAutoCloseDelegate.initOnServiceStarted(getApplicationContext());
//        }
        mIsWifiCloseable = true;
        mWifiAutoCloseDelegate.initOnServiceStarted(getApplicationContext());

        startLocation();

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) Log.e("LocationService", "LocationServiceonDestroy");
        unApplyNotiKeepMech();
        stopLocation();

        super.onDestroy();
    }

    /**
     * 停止定位
     */
    void stopLocation() {
        if (null != mLocationClient) {
            mLocationClient.stopLocation();
        }
    }

    /**
     * 初始化并开启定位
     */
    private void startLocation() {
        stopLocation();
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        mLocationClient.setLocationListener(mAMapLocationListener);//设置定位回调监听
        AMapLocationClientOption option = new AMapLocationClientOption(); //初始化定位参数
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置为高精度定位模式
        option.setInterval(3000);//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        option.setSensorEnable(true);//设置传感器可用  自Android 定位 SDK V3.1.0版本开始，AMapLocationClientOption会有这样一个接口：setSensorEnable(boolean sensorEnable)；这个接口控制是否使用设备传感器，默认关闭，可以打开。当上述接口被设置为true后，定位的Client将会采用设备传感器计算海拔，角度和速度。意味着高精度定位模式下也可以返回速度、角度、海拔数据。
        mLocationClient.setLocationOption(option);//设置定位参数
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        mLocationClient.startLocation();//启动定位
    }


    //                    //定位成功回调信息，设置相关消息
//                    aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                    aMapLocation.getLatitude();//获取纬度
//                    aMapLocation.getLongitude();//获取经度
//                    aMapLocation.getAccuracy();//获取精度信息
//                    SimpleDateF ormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    Date date = new Date(amapLocation.getTime());
//                    df.format(date);//定位时间
//                    aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                    aMapLocation.getCountry();//国家信息
//                    aMapLocation.getProvince();//省信息
//                    aMapLocation.getCity();//城市信息
//                    aMapLocation.getDistrict();//城区信息
//                    aMapLocation.getStreet();//街道信息
//                    aMapLocation.getStreetNum();//街道门牌号信息
//                    aMapLocation.getCityCode();//城市编码
//                    aMapLocation.getAdCode();//地区编码
//                    aMapLocation.getAoiName();//获取当前定位点的AOI信息
    private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {//定位成功
            if (BuildConfig.DEBUG) Log.e("LocationService", "onLocationChanged"+" mIsWifiCloseable="+mIsWifiCloseable);
            if (!mIsWifiCloseable) {
                return;
            }


            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //发送结果的通知
                    mWifiAutoCloseDelegate.onLocateSuccess(getApplicationContext(), PowerManagerUtil.getInstance().isScreenOn(getApplicationContext()), NetUtil.getInstance().isMobileAva(getApplicationContext()));
                    sendLocationBroadcast(aMapLocation);
                }
            } else {//定位失败
                mWifiAutoCloseDelegate.onLocateFail(getApplicationContext(), aMapLocation.getErrorCode(), PowerManagerUtil.getInstance().isScreenOn(getApplicationContext()), NetUtil.getInstance().isWifiCon(getApplicationContext()));
            }
        }
    };


    private void sendLocationBroadcast(AMapLocation aMapLocation) {
        NodemoMapLocation nodemoMapLocation = new NodemoMapLocation();
        nodemoMapLocation.setDistrict(aMapLocation.getDistrict());
        nodemoMapLocation.setStreet(aMapLocation.getStreet());
        nodemoMapLocation.setLatitude(aMapLocation.getLatitude());
        nodemoMapLocation.setLongitude(aMapLocation.getLongitude());
        nodemoMapLocation.setSpeed(aMapLocation.getSpeed());
        if (BuildConfig.DEBUG)
            Log.d("LocationService", "aMapLocation.getSpeed():" + aMapLocation.getSpeed());

        Intent mIntent = new Intent(MapActivity.RECEIVER_ACTION);
        mIntent.putExtra(ACTION_AMAPLOCATION, aMapLocation);
        mIntent.putExtra(ACTION_NODEMOMAPLOCATION,nodemoMapLocation);
        //发送广播
        sendBroadcast(mIntent);
    }

}
