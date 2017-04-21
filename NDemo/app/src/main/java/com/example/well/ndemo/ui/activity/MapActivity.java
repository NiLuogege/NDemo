package com.example.well.ndemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.utils.SnackbarUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${LuoChen} on 2017/4/20 10:33.
 * email:luochen0519@foxmail.com
 * <p>
 * 在Activity中使用地图可以更好地管理地图的声明周期
 * <p>
 * http://blog.csdn.net/pan960821/article/details/50907330
 */

public class MapActivity extends BaseActivity {
    @Bind(R.id.map)
    MapView mMapView;
    @Bind(R.id.rl_root)
    RelativeLayout rl_root;
    private AMap mAMap;
    private LocationSource.OnLocationChangedListener mOnLocationChangedListener;
    private AMapLocationClient mLocationClient;
    private static final float ZOOMLEVEL = 17;//zoom - 描述了一个缩放级别。高德地图的缩放级别是在3-19 之间。


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);// 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。
        initMap();

    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
        }

    }

    private void setUpMap() {
        mAMap.setLocationSource(mLocationSource);// 设置定位监听
        MyLocationStyle style = setDot();
        mAMap.setMyLocationStyle(style);
        setMapUI();
        mAMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

    }

    /**
     * 设置地图上的空间
     */
    private void setMapUI() {
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);//显示默认的定位按钮
        uiSettings.setScaleControlsEnabled(true);  //设置比例尺控件
    }

    /**
     * 设置定位点 的样式
     *
     * @return
     */
    @NonNull
    private MyLocationStyle setDot() {
        MyLocationStyle style = new MyLocationStyle();//初始化定位蓝点样式类
        style.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        style.interval(2000);//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        return style;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 初始化并开启定位
     */
    private void startLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        mLocationClient.setLocationListener(mAMapLocationListener);//设置定位回调监听
        AMapLocationClientOption option = new AMapLocationClientOption(); //初始化定位参数
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置为高精度定位模式
        option.setInterval(2000);//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        mLocationClient.setLocationOption(option);//设置定位参数
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        mLocationClient.startLocation();//启动定位
    }

    /**
     * 在activate()中设置定位初始化及启动定位，在deactivate()中写停止定位的相关调用。
     */
    private LocationSource mLocationSource = new LocationSource() {
        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            if (BuildConfig.DEBUG) Log.e("MapActivity", "activate");
            mOnLocationChangedListener = onLocationChangedListener;
            startLocation();
        }

        @Override
        public void deactivate() {
            if (BuildConfig.DEBUG) Log.e("MapActivity", "deactivate");
            mOnLocationChangedListener = null;
            if (mLocationClient != null) {
                mLocationClient.stopLocation();
                mLocationClient.onDestroy();
            }
            mLocationClient = null;
        }
    };



    private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {//定位成功
            if (mOnLocationChangedListener != null && aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    mOnLocationChangedListener.onLocationChanged(aMapLocation);// 在更新的坐标显示系统小蓝点
                    LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());//获取经纬度
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,ZOOMLEVEL));//设置当前地图显示为当前位置
                }
            } else {//定位失败
                SnackbarUtils.showDefaultLongSnackbar(rl_root, getString(R.string.location_error));
            }
        }
    };
}
