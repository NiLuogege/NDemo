package com.example.well.ndemo.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SlidingDrawer;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.TraceOverlay;
import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.adapter.RecordListAdapter;
import com.example.well.ndemo.bean.NodemoMapLocation;
import com.example.well.ndemo.bean.PathRecord;
import com.example.well.ndemo.db.MapDbAdapter;
import com.example.well.ndemo.mapbackground.LocationService;
import com.example.well.ndemo.utils.SPUtils;
import com.example.well.ndemo.utils.SettingsUtils;
import com.example.well.ndemo.utils.SnackbarUtils;
import com.example.well.ndemo.utils.map.SensorEventHelper;

import java.io.Serializable;
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
 * <p>
 * 以后做的时候 我们可以吧地图自带的箭头和那个圆圈去掉或者变为透明,然后自己加marker 这样自定义的程度更高
 * <p>
 * <p>
 * api:
 * 1.mOnLocationChangedListener.onLocationChanged(currentAmapLocation);// 在更新的坐标显示系统小蓝点 ,该方法会将定位点拖动到屏幕重点
 */

public class MapActivity extends BaseActivity {
    @Bind(R.id.map)
    MapView mMapView;
    @Bind(R.id.fl_root)
    FrameLayout rl_root;
    @Bind(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @Bind(R.id.ib_location)
    ImageButton ib_location;
    @Bind(R.id.content)
    RecyclerView rv_RecordList;
    @Bind(R.id.sd)
    SlidingDrawer sd;

    //    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
//    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private static final int STROKE_COLOR = Color.argb(0, 0, 0, 0);
    private static final int FILL_COLOR = Color.argb(0, 0, 0, 0);
    public static final String RECEIVER_ACTION = "location_in_background";
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
    public Location currentAmapLocation = null;
    private MapDbAdapter mMapDbAdapter;
    private int mTotleDistance = 0;
    private LatLng preLatLng;//记录上一个坐标
    private SensorEventHelper mSensorEventHelper;//负责屏幕旋转的时候使箭头也跟着旋转
    private Marker mLocMarker;//旋转的marker
    private Circle mCircle;
    private LatLng mCurrentLatLng;
    private List<PathRecord> mRecords;
    private RecordListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);// 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。
        initView();
        requestPermission();
        initBroadcast();
    }

    private void initView() {
        initToolbar();
        initRecycalView();
        initSlidingDrawer();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setOnMenuItemClickListener(mOnMenuItemClickListener);
        toolbar.setNavigationOnClickListener(mOnClickListener);
        int statusBarHeight = getStatusBarHeight();
        if (statusBarHeight > 0) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
            layoutParams.topMargin = statusBarHeight;
        }
    }

    private void initRecycalView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        rv_RecordList.setLayoutManager(layoutManager);
        mRecords = loadRecords();
        mAdapter = new RecordListAdapter(context, mRecords);
        rv_RecordList.setAdapter(mAdapter);
    }

    /**
     * 从数据库拉取数据
     * @return
     */
    private List<PathRecord> loadRecords() {
        if (mMapDbAdapter == null) {
            mMapDbAdapter = new MapDbAdapter(context);
        }
        mMapDbAdapter.open();
        List<PathRecord> records = mMapDbAdapter.queryRecordAll();
        mMapDbAdapter.close();
        return records;
    }

    private void initSlidingDrawer() {

        //view绘制完成后在调用--> 此方法只调用一次，其原理是将自定义的runnable放入到消息队列的尾部，当looper调用到它时，view已经初始化完成了
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                int statusBarHeight = getStatusBarHeight();
                int toolbarHeight = toolbar.getHeight();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) sd.getLayoutParams();
                layoutParams.topMargin = statusBarHeight + toolbarHeight;
                sd.setLayoutParams(layoutParams);
            }
        });


    }

    private void requestPermission() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        requestPermission(permissions, mPermissionHandler);
    }

    private void initBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVER_ACTION);
        registerReceiver(locationChangeBroadcastReceiver, intentFilter);
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
        }
        moveMap();
        mSensorEventHelper = new SensorEventHelper(context);
        if (mSensorEventHelper != null) {
            mSensorEventHelper.registerSensorListener();
        }
        mTraceOverlay = new TraceOverlay(mAMap);//用于绘制轨迹纠偏
    }

    /**
     * 将地图挪到上次退出的位置
     */
    private void moveMap() {
        String lastLatlng = SPUtils.getInstance(getApplication()).getString(SettingsUtils.LASTLATLNG);
        if(!TextUtils.isEmpty(lastLatlng)) {
            if (BuildConfig.DEBUG) Log.e("MapActivity", "moveMap-->lastLatlng=" + lastLatlng);
            String[] split = lastLatlng.split(":");
            double latitude = Double.parseDouble(split[0]);
            double longitude = Double.parseDouble(split[1]);
            mCurrentLatLng = new LatLng(latitude, longitude);
            location();
        }
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
     * 设置定位点 的样式(去掉自带的定位图标)
     *
     * @return
     */
    @NonNull
    private MyLocationStyle setDot() {
        MyLocationStyle style = new MyLocationStyle();//初始化定位蓝点样式类
        style.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.point));//设置定位圆点图标
        style.radiusFillColor(FILL_COLOR);
        style.strokeColor(STROKE_COLOR);
        style.anchor(0.5f, 0.5f);//这只锚点
        style.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        style.interval(2000 * 100);//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        return style;
    }

    /**
     * 设置地图上的控件
     */
    private void setMapUI() {
        UiSettings uiSettings = mAMap.getUiSettings();
//        uiSettings.setMyLocationButtonEnabled(true);//显示默认的定位按钮
        uiSettings.setScaleControlsEnabled(true);  //设置比例尺控件
    }


    @OnClick(R.id.ib_location)
    void location() {
        if (mAMap != null) {
            mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mCurrentLatLng));//移动地图到中心
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

        if (mSensorEventHelper != null) {
            mSensorEventHelper.unRegisterSensorListener();
            mSensorEventHelper.setCurrentMarker(null);
            mSensorEventHelper = null;
        }

        if (locationChangeBroadcastReceiver != null)
            unregisterReceiver(locationChangeBroadcastReceiver);

        catchCurrentLatLng();
    }

    /**
     * 缓存最后一个点
     */
    private void catchCurrentLatLng() {
        double latitude = mCurrentLatLng.latitude;
        double longitude = mCurrentLatLng.longitude;
        String lastLatlng = latitude + ":" + longitude;
        if (BuildConfig.DEBUG) Log.e("MapActivity", "onDestroy-->lastLatlng=" + lastLatlng);
        SPUtils.getInstance(getApplication()).put(SettingsUtils.LASTLATLNG, lastLatlng);
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
     * 设置Toolbar的标题
     *
     * @param aMapLocation
     */
    private void setToolbarTitle(NodemoMapLocation aMapLocation) {
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
        saveRecord(mRecord.getPathline(), mRecord.getDate());
        reset();
        loadRecords();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 还原数据
     */
    private void reset() {
        mAMap.clear();//清除地图上的东西
        mRecord = null;
        mRealPolylineOptions = null;
        initMapLine();
        mLocMarker = null;
        isFirstEnter = true;
        location();//定位到当前位置
    }

    /**
     * 保存轨迹
     *
     * @param list
     * @param time
     */
    private void saveRecord(List<NodemoMapLocation> list, String time) {

        if (list != null && list.size() > 0) {
            if (mMapDbAdapter == null) {
                mMapDbAdapter = new MapDbAdapter(context);
            }
            mMapDbAdapter.open();
            String duration = getDuration();
            float distance = getDistance(list);
            String average = getAverage(distance);
            String pathlineSring = getPathLineString(list);
            NodemoMapLocation firstLocaiton = list.get(0);
            NodemoMapLocation lastLocaiton = list.get(list.size() - 1);
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
    private float getDistance(List<NodemoMapLocation> list) {
        float distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            NodemoMapLocation firstpoint = list.get(i);
            NodemoMapLocation secondpoint = list.get(i + 1);
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
        float s = (mEndTime - mStartTime) / 1000;
        return String.valueOf(distance / s);
    }

    /**
     * 将地图的信息连接起来
     *
     * @param list
     * @return
     */
    private String getPathLineString(List<NodemoMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuffer pathline = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            NodemoMapLocation location = list.get(i);
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
    private String amapLocationToString(NodemoMapLocation location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.getLatitude()).append(",");
        locString.append(location.getLongitude()).append(",");
        locString.append(location.getProvider()).append(",");
        locString.append(location.getTime()).append(",");
        locString.append(location.getSpeed()).append(",");
        locString.append(location.getBearing()).append(",");
        locString.append(location.getStreet());
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
     * 请求权限的回调
     */
    PermissionHandler mPermissionHandler = new PermissionHandler() {
        @Override
        public void onGranted() {
            super.onGranted();
            initMap();
            initMapLine();
        }

        @Override
        public void onDenied() {
            super.onDenied();
            SnackbarUtils.showDefaultLongSnackbar(rl_root, "该功能需要位置权限");
        }

        @Override
        public boolean onNeverAsk() {
            SnackbarUtils.showDefaultLongSnackbar(rl_root, "该功能需要位置权限");
            return super.onNeverAsk();
        }
    };

    /**
     * 在activate()中设置定位初始化及启动定位，在deactivate()中写停止定位的相关调用。
     */
    private LocationSource mLocationSource = new LocationSource() {
        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            if (BuildConfig.DEBUG) Log.e("MapActivity", "定位监听mLocationSource--> activate");
            mOnLocationChangedListener = onLocationChangedListener;
            startLocationService();
        }

        @Override
        public void deactivate() {
            if (BuildConfig.DEBUG) Log.e("MapActivity", "定位监听mLocationSource--> deactivate");
            mOnLocationChangedListener = null;
            if (mLocationClient != null) {
                mLocationClient.stopLocation();
                mLocationClient.onDestroy();
            }
            mLocationClient = null;
        }
    };

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

    /**
     * 开始定位服务
     */
    private void startLocationService() {
        Intent service = new Intent(this, LocationService.class);
        getApplicationContext().startService(service);
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

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MapActivity.this.finish();
        }
    };

    private BroadcastReceiver locationChangeBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECEIVER_ACTION)) {

                Parcelable p_location = intent.getParcelableExtra(LocationService.ACTION_AMAPLOCATION);
                Serializable nodemoMapLocation = intent.getSerializableExtra(LocationService.ACTION_NODEMOMAPLOCATION);
                Location location = (Location) p_location;
                NodemoMapLocation aMapLocation = (NodemoMapLocation) nodemoMapLocation;

                if (BuildConfig.DEBUG)
                    Log.e("MapActivity", "接受广播成功" + " nodemoMapLocation= " + aMapLocation.toString());
                if (BuildConfig.DEBUG)
                    Log.e("MapActivity", "aMapLocation.getLatitude():" + aMapLocation.getLatitude() + " aMapLocation.getLongitude()=" + aMapLocation.getLongitude());
                if (null != aMapLocation) {
                    setToolbarTitle(aMapLocation);
                    currentAmapLocation = location;

                    //获取经纬度
                    mCurrentLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                    if (isFirstEnter) {
                        if (BuildConfig.DEBUG) Log.e("MapActivity", "isFirstEnter=" + isFirstEnter);
                        addMarker(mCurrentLatLng);//添加定位图标
                        isFirstEnter = false;
                        preLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());//获取经纬度
                        mSensorEventHelper.setCurrentMarker(mLocMarker);
                        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, ZOOMLEVEL));//设置当前地图显示为当前位置
                    } else {
                        mLocMarker.setPosition(mCurrentLatLng);
                    }

                    if (recording) {
                        mRecord.addpoint(aMapLocation);
                        mRealPolylineOptions.add(mCurrentLatLng);
                        reDrawline();
                        float distance = AMapUtils.calculateLineDistance(mCurrentLatLng, preLatLng);
                        mTotleDistance += distance;
                    }
                    preLatLng = mCurrentLatLng;

                }
            }
        }
    };


    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = mAMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.navi_map_gps_locked)));
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = mAMap.addMarker(options);
    }
}
