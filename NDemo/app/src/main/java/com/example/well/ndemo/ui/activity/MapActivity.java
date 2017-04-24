package com.example.well.ndemo.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.TraceOverlay;
import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.bean.PathRecord;
import com.example.well.ndemo.db.MapDbAdapter;
import com.example.well.ndemo.utils.SnackbarUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @Bind(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @Bind(R.id.ib_location)
    ImageButton ib_location;
    private AMap mAMap;
    private LocationSource.OnLocationChangedListener mOnLocationChangedListener;
    private AMapLocationClient mLocationClient;
    private static final float ZOOMLEVEL = 17;//zoom - 描述了一个缩放级别。高德地图的缩放级别是在3-19 之间。
    private PolylineOptions mRealPolylineOptions;//真是的轨迹
    private boolean recording = false;//是否正在记录行程
    private boolean isFirstEnter = true;//是否刚刚进入
    private MenuItem mMapSwitch;
    private PathRecord mRecord;
    private long mStartTime;//行程的起始时间
    private long mEndTime;//行程结束时间
    private TraceOverlay mTraceOverlay;
    private List<TraceOverlay> mOverlayList = new ArrayList<>();
    private Polyline mPolyline;
    public AMapLocation currentAmapLocation = null;
    private MapDbAdapter mMapDbAdapter;
    private int mTotleDistance = 0;
    private LatLng preLatLng;//记录上一个坐标
    private Marker mMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);// 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。
        initView();
        initMap();
        initMapLine();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setOnMenuItemClickListener(mOnMenuItemClickListener);
        int statusBarHeight = getStatusBarHeight();
        if (statusBarHeight > 0) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
            layoutParams.topMargin = statusBarHeight;
        }
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
        }
        mTraceOverlay = new TraceOverlay(mAMap);//用于绘制轨迹纠偏
    }

    /**
     * 初始化轨迹线段
     */
    private void initMapLine() {
        mRealPolylineOptions = new PolylineOptions();
        mRealPolylineOptions.width(10f);//设置线段的宽度，单位像素。
        mRealPolylineOptions.color(R.color.colorToolbar);//设置线段颜色
    }


    private void setUpMap() {
        mAMap.setLocationSource(mLocationSource);// 设置定位监听
        MyLocationStyle style = setDot();
        mAMap.setMyLocationStyle(style);
        setMapUI();
        mAMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

    }

    /**
     * 设置地图上的控件
     */
    private void setMapUI() {
        UiSettings uiSettings = mAMap.getUiSettings();
//        uiSettings.setMyLocationButtonEnabled(true);//显示默认的定位按钮
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

    @OnClick(R.id.ib_location)
    void location() {
        if (currentAmapLocation != null) {
            mOnLocationChangedListener.onLocationChanged(currentAmapLocation);// 在更新的坐标显示系统小蓝点 ,该方法会将定位点拖动到屏幕重点
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMapSwitch = menu.findItem(R.id.map_switch);
        return true;
    }

//    /**
//     * menuItem 选中时调用
//     * @param item
//     * @return
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return true;//说明消费事件
//    }

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
     * 设置Toolbar的标题
     *
     * @param aMapLocation
     */
    private void setToolbarTitle(AMapLocation aMapLocation) {
        String district = aMapLocation.getDistrict();//城区信息
        String street = aMapLocation.getStreet();//街道信息
        String mCurrentLocation = district + "-" + street;
        toolbar.setTitle(mCurrentLocation);
    }

    /**
     * 开始记录行程
     */
    private void beginRecord() {
//        mAMap.clear();//将地图清空
        if (mRecord != null) {
            mRecord = null;
        }
        mRecord = new PathRecord();
        mStartTime = System.currentTimeMillis();
        mRecord.setDate(getcueDate(mStartTime));
    }

    /**
     * 停止记录行程
     */
    private void stopRecord() {
        mEndTime = System.currentTimeMillis();
        mOverlayList.add(mTraceOverlay);
//        LBSTraceClient traceClient = new LBSTraceClient(getApplicationContext());//进行轨迹纠偏
//        queryProcessedTrace方法参数解析
//        lineID - 用于标示一条轨迹，支持多轨迹纠偏，如果多条轨迹调起纠偏接口，则lineID需不同
//        locations - 一条轨迹的点集合，目前支持该点集合为一条行车GPS高精度定位轨迹
//        type - 轨迹坐标系，目前支持高德 LBSTraceClient.TYPE_AMAP;GPS LBSTraceClient.TYPE_GPSTYPE_GPS;百度 LBSTraceClient.TYPE_BAIDU
//        listener - 轨迹纠偏回调
//        traceClient.queryProcessedTrace(LINEID_2, MapUtils.parseTraceLocationList(mRecord.getPathline()), LBSTraceClient.TYPE_AMAP, mTraceListener);//绘制纠偏轨迹
        saveRecord(mRecord.getPathline(), mRecord.getDate());
        reset();
    }

    /**
     * 还原数据
     */
    private void reset() {
        mAMap.clear();//清除地图上的东西
        location();//定位到当前位置
        mMarker = null;
    }

    /**
     * 保存轨迹
     *
     * @param list
     * @param time
     */
    private void saveRecord(List<AMapLocation> list, String time) {

        if (list != null && list.size() > 0) {
            mMapDbAdapter = new MapDbAdapter(context);
            mMapDbAdapter.open();
            String duration = getDuration();
            float distance = getDistance(list);
            String average = getAverage(distance);
            String pathlineSring = getPathLineString(list);
            AMapLocation firstLocaiton = list.get(0);
            AMapLocation lastLocaiton = list.get(list.size() - 1);
            String stratpoint = amapLocationToString(firstLocaiton);
            String endpoint = amapLocationToString(lastLocaiton);
            mMapDbAdapter.addRecord(String.valueOf(distance), duration, average,
                    pathlineSring, stratpoint, endpoint, time);
            mMapDbAdapter.close();
        } else {
            SnackbarUtils.showDefaultLongSnackbar(rl_root, "没有记录到路径");
        }
    }

    /**
     * 计算用时
     *
     * @return
     */
    private String getDuration() {
        return String.valueOf((mEndTime - mStartTime) / 1000f);
    }

    /**
     * 计算距离
     *
     * @param list
     * @return
     */
    private float getDistance(List<AMapLocation> list) {
        float distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            AMapLocation firstpoint = list.get(i);
            AMapLocation secondpoint = list.get(i + 1);
            LatLng firstLatLng = new LatLng(firstpoint.getLatitude(),
                    firstpoint.getLongitude());
            LatLng secondLatLng = new LatLng(secondpoint.getLatitude(),
                    secondpoint.getLongitude());
            double betweenDis = AMapUtils.calculateLineDistance(firstLatLng,
                    secondLatLng);
            distance = (float) (distance + betweenDis);
        }
        return distance;
    }

    /**
     * 计算速度
     *
     * @param distance
     * @return
     */
    private String getAverage(float distance) {
        return String.valueOf(distance / (float) (mEndTime - mStartTime));
    }

    /**
     * 将地图的信息连接起来
     *
     * @param list
     * @return
     */
    private String getPathLineString(List<AMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuffer pathline = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            AMapLocation location = list.get(i);
            String locString = amapLocationToString(location);
            pathline.append(locString).append(";");
        }
        String pathLineString = pathline.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    /**
     * 将定位到的信息append 成字符串
     *
     * @param location
     * @return
     */
    private String amapLocationToString(AMapLocation location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.getLatitude()).append(",");
        locString.append(location.getLongitude()).append(",");
        locString.append(location.getProvider()).append(",");
        locString.append(location.getTime()).append(",");
        locString.append(location.getSpeed()).append(",");
        locString.append(location.getBearing());
        return locString.toString();
    }

    @SuppressLint("SimpleDateFormat")
    private String getcueDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd  HH:mm:ss ");
        Date curDate = new Date(time);
        String date = formatter.format(curDate);
        return date;
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
                    if (BuildConfig.DEBUG)
                        Log.e("MapActivity", "定位成功" + " mTotleDistance= " + mTotleDistance);
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

                    setToolbarTitle(aMapLocation);
                    currentAmapLocation = aMapLocation;

                    LatLng currentLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());//获取经纬度


                    if (isFirstEnter) {
                        if (BuildConfig.DEBUG) Log.e("MapActivity", "isFirstEnter=" + isFirstEnter);
                        mOnLocationChangedListener.onLocationChanged(aMapLocation);// 在更新的坐标显示系统小蓝点 ,该方法会将定位点拖动到屏幕重点
                        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOMLEVEL));//设置当前地图显示为当前位置
                        isFirstEnter = false;
                        preLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());//获取经纬度
                    }

                    if (recording) {
                        mRecord.addpoint(aMapLocation);
                        mRealPolylineOptions.add(currentLatLng);
                        reDrawline();
                        float distance = AMapUtils.calculateLineDistance(currentLatLng, preLatLng);
                        mTotleDistance += distance;
                        setMarker(currentLatLng);
                        mOnLocationChangedListener.onLocationChanged(aMapLocation);

                    }
                    preLatLng = currentLatLng;
                }
            } else {//定位失败
                SnackbarUtils.showDefaultLongSnackbar(rl_root, getString(R.string.location_error));
            }
        }
    };

    /**
     * 设置maker
     *
     * @param currentLatLng
     */

    private void setMarker(LatLng currentLatLng) {
        if (mMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).title(mTotleDistance + "m");
            // MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)).title(mTotleDistance + "m");
            mMarker = mAMap.addMarker(markerOptions);
            mMarker.showInfoWindow();
        } else {
            mMarker.setTitle(mTotleDistance + "m");
            mMarker.setPosition(currentLatLng);
            mMarker.showInfoWindow();
        }
    }


    /**
     * 绘制实时轨迹
     */
    private void reDrawline() {
        if (mRealPolylineOptions.getPoints().size() > 1) {
            if (mPolyline == null) {
                mPolyline = mAMap.addPolyline(mRealPolylineOptions);
            } else {
                mPolyline.setPoints(mRealPolylineOptions.getPoints());
            }

        }
    }


    private Toolbar.OnMenuItemClickListener mOnMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.map_switch:
                    if (BuildConfig.DEBUG) Log.e("MapActivity", "onMenuItemClick");
                    if (recording) {//正在记录
                        mMapSwitch.setIcon(R.mipmap.begin);
                        SnackbarUtils.showDefaultLongSnackbar(rl_root, "行程记录已关闭");
                        stopRecord();
                    } else {//没有记录
                        mMapSwitch.setIcon(R.mipmap.end);
                        SnackbarUtils.showDefaultLongSnackbar(rl_root, "行程记录已开启");
                        beginRecord();
                    }
                    recording = !recording;
                    break;
            }
            return false;
        }
    };
}
