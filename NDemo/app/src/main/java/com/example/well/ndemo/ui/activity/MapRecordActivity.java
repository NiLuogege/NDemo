package com.example.well.ndemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.bean.NodemoMapLocation;
import com.example.well.ndemo.bean.PathRecord;
import com.example.well.ndemo.db.MapDbAdapter;
import com.example.well.ndemo.utils.MapUtils;
import com.example.well.ndemo.utils.SnackbarUtils;
import com.example.well.ndemo.utils.SystemUtils;
import com.example.well.ndemo.utils.map.TraceRePlay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${LuoChen} on 2017/4/26 17:09.
 * email:luochen0519@foxmail.com
 * <p>
 * 历史行踪显示Activity
 */

public class MapRecordActivity extends BaseActivity {
    @Bind(R.id.map)
    MapView mMapView;
    @Bind(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @Bind(R.id.rl_root)
    RelativeLayout rl_root;

    private static final int SLEEPTIME = 20;//回调间隔时间
    private static final int TRACEID = 1;//纠偏轨迹ID
    public static final String EXTRAID = "recordId";
    private MenuItem mRecord_switch;//切换是纠偏路线还是未纠偏路线
    private boolean isTrace = false;//是否是纠偏轨迹
    private AMap mMap;
    private List<LatLng> mLatLngList;//真实轨迹集合
    public List<LatLng> tracePathList;//纠偏轨迹集合
    private Polyline mPoly_line;//真实轨迹的线
    private Marker mStartMarker_line;//真实轨迹的起点
    private Marker mEndMarker_line;//真实轨迹的结束点
    private Polyline mPoly_trace;//纠偏轨迹的线
    private Marker mStartMarker_trace;//纠偏轨迹的起点
    private Marker mEndMarker_trace;//纠偏轨迹的结束点
    private List<Integer> mColorList;
    private ExecutorService mThreadPool;
    private Marker mMarker_line_walker;
    private Marker mMarker_trace_walker;
    private TraceRePlay mRePlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_record);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_record, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mRecord_switch = menu.findItem(R.id.record_switch);
        mRecord_switch.setChecked(false);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRePlay != null) {
            mRePlay.stopTrace();
        }
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
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

    private void initView() {
        initToolbar();
        initMap();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setOnMenuItemClickListener(mOnMenuItemClickListener);
        toolbar.setNavigationOnClickListener(mOnClickListener);
        int statusBarHeight = getStatusBarHeight();
        if (statusBarHeight > 0) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
            layoutParams.topMargin = statusBarHeight;
        }
    }

    private void initMap() {
        if (mMap == null) {
            mMap = mMapView.getMap();
            mMap.setOnMapLoadedListener(mOnMapLoadedListener);//设计地图加载成功的回调
        }
        initColorList();
        initTraceRePlay();
    }

    private void initTraceRePlay() {
        int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 3;
        mThreadPool = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * 初始化ColorList
     */
    private void initColorList() {
        int[] intArray = getResources().getIntArray(R.array.colorList);
        mColorList = new ArrayList<>();
        for (int i = 0; i < intArray.length; i++) {
            mColorList.add(intArray[i]);
        }
    }


    /**
     * 从数据库中取出数据
     *
     * @return
     */
    private PathRecord initData() {
        Intent intent = getIntent();
        PathRecord mRecord = null;
        if (intent != null) {
            int recordId = intent.getIntExtra(EXTRAID, 0);
            MapDbAdapter mapDbAdapter = new MapDbAdapter(context);
            mapDbAdapter.open();
            mRecord = mapDbAdapter.queryRecordById(recordId);
            mapDbAdapter.close();
        }
        return mRecord;
    }

    AMap.OnMapLoadedListener mOnMapLoadedListener = new AMap.OnMapLoadedListener() {
        @Override
        public void onMapLoaded() {
            setUpRecord();
        }
    };

    /**
     * 对绘制轨迹进行准备
     */
    private void setUpRecord() {
        PathRecord record = initData();
        if (record != null) {
            List<NodemoMapLocation> pathline = record.getPathline();
            NodemoMapLocation startpoint = record.getStartPoint();
            NodemoMapLocation endpoint = record.getEndPoint();
            if (pathline == null || endpoint == null || startpoint == null) {
                return;
            }

            String street_start = record.getStartPoint().getStreet();//街道
            String street_end = record.getEndPoint().getStreet();//街道
            toolbar.setTitle(street_start + "->" + street_end);
            toolbar.setTitleMarginStart(SystemUtils.dp2px(-5, getResources()));

            LatLng startLatLng = new LatLng(startpoint.getLatitude(), startpoint.getLongitude());
            LatLng endLatLng = new LatLng(endpoint.getLatitude(), endpoint.getLongitude());
            mLatLngList = MapUtils.parseLatLngList(pathline);
            //绘制原始轨迹
            drawLine(startLatLng, endLatLng, mLatLngList);
            //进行轨迹纠偏
            initTrace(pathline);
        } else {
            SnackbarUtils.showDefaultLongSnackbar(rl_root, "轨迹出现错误");
        }

    }

    private void initTrace(List<NodemoMapLocation> pathline) {
        LBSTraceClient traceClient = new LBSTraceClient(getApplicationContext());
        List<TraceLocation> traceLocationList = MapUtils.parseTraceLocationList(pathline);
        //        queryProcessedTrace方法参数解析
//        lineID - 用于标示一条轨迹，支持多轨迹纠偏，如果多条轨迹调起纠偏接口，则lineID需不同
//        locations - 一条轨迹的点集合，目前支持该点集合为一条行车GPS高精度定位轨迹
//        type - 轨迹坐标系，目前支持高德 LBSTraceClient.TYPE_AMAP;GPS LBSTraceClient.TYPE_GPSTYPE_GPS;百度 LBSTraceClient.TYPE_BAIDU
//        listener - 轨迹纠偏回调
        traceClient.queryProcessedTrace(TRACEID, traceLocationList, LBSTraceClient.TYPE_AMAP, mTraceListener);
    }

    /**
     * 绘制原始轨迹(地图上添加原始轨迹线路及起终点、轨迹动画小人)
     *
     * @param startLatLng
     * @param endLatLng
     * @param latLngList
     */
    private void drawLine(LatLng startLatLng, LatLng endLatLng, List<LatLng> latLngList) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(15f)
                .useGradient(true)
                .colorValues(mColorList)
                .zIndex(10)
                .addAll(latLngList);
        MarkerOptions options_walker = new MarkerOptions()
                .position(startLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.run_big));
        MarkerOptions options_start = new MarkerOptions()
                .position(startLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.point_start));
        MarkerOptions options_end = new MarkerOptions()
                .position(endLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.point_end));
        LatLngBounds bounds = getBounds();
        mPoly_line = mMap.addPolyline(polylineOptions);
        mStartMarker_line = mMap.addMarker(options_start);
        mEndMarker_line = mMap.addMarker(options_end);
        mMarker_line_walker = mMap.addMarker(options_walker);
        //newLatLngBounds-->返回CameraUpdate对象，这个对象包含一个经纬度限制的区域，并且是最大可能的缩放级别。 你可以设置一个边距数值来控制插入区域与view的边框之间的空白距离。 方法必须在地图初始化完成之后使用。
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        reWark();
    }

    /**
     * 绘制纠偏轨迹(地图上添加纠偏后轨迹线路及起终点、轨迹动画小人)
     *
     * @param list
     */
    private void drawTrace(List<LatLng> list) {
        if (list == null || list.size() < 2) {
            return;
        }
        LatLng latLng_start = list.get(0);
        LatLng latLng_end = list.get(list.size() - 1);


        PolylineOptions polylineOptions = new PolylineOptions()
                .width(20f)
                .addAll(list);
        MarkerOptions options_walker = new MarkerOptions()
                .position(latLng_start)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.run_big));
        MarkerOptions options_start = new MarkerOptions()
                .position(latLng_start)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.point_start));
        MarkerOptions options_end = new MarkerOptions()
                .position(latLng_end)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.point_end));
        mPoly_trace = mMap.addPolyline(polylineOptions);
        mStartMarker_trace = mMap.addMarker(options_start);
        mEndMarker_trace = mMap.addMarker(options_end);
        mMarker_trace_walker = mMap.addMarker(options_walker);

        //对纠偏轨迹进行隐藏
        updateMap();
    }

    private void reWark() {
        if (mRePlay != null) {
            mRePlay.stopTrace();
        }
        if (isTrace) {
            mRePlay = startWark(tracePathList, mMarker_trace_walker);
        } else {
            mRePlay = startWark(mLatLngList, mMarker_line_walker);
        }
    }

    private TraceRePlay startWark(List<LatLng> latLngList, final Marker marker) {
        TraceRePlay rePlay = new TraceRePlay(latLngList, SLEEPTIME, new TraceRePlay.TraceRePlayListener() {
            @Override
            public void onTraceUpdating(LatLng latLng) {
                if (marker != null) {
                    marker.setPosition(latLng);
                }
            }

            @Override
            public void onTraceUpdateFinish() {

            }
        });
        mThreadPool.execute(rePlay);
        return rePlay;
    }

    /**
     * 设置那根线显示
     */
    private void updateMap() {
        //真是轨迹
        mPoly_line.setVisible(!isTrace);
        mStartMarker_line.setVisible(!isTrace);
        mEndMarker_line.setVisible(!isTrace);
        mMarker_line_walker.setVisible(!isTrace);
        //纠偏轨迹
        if (mPoly_trace != null) {
            mPoly_trace.setVisible(isTrace);
        }
        mStartMarker_trace.setVisible(isTrace);
        mEndMarker_trace.setVisible(isTrace);
        mMarker_trace_walker.setVisible(isTrace);
    }


    private LatLngBounds getBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (mLatLngList == null) {
            return b.build();
        }
        for (int i = 0; i < mLatLngList.size(); i++) {
            b.include(mLatLngList.get(i));
        }
        return b.build();

    }

    /**
     * 纠偏轨迹的回调
     */
    TraceListener mTraceListener = new TraceListener() {
//        public List<LatLng> tracePathList;

        @Override
        public void onRequestFailed(int i, String s) {
            SnackbarUtils.showDefaultLongSnackbar(rl_root, "轨迹纠偏失败");
        }

        @Override
        public void onTraceProcessing(int i, int i1, List<LatLng> list) {

        }

        @Override
        public void onFinished(int i, List<LatLng> list, int i1, int i2) {
            tracePathList = list;
            mRecord_switch.setChecked(true);
            drawTrace(list);
            if (BuildConfig.DEBUG) Log.e("MapRecordActivity", "纠偏完成");

        }
    };


    private Toolbar.OnMenuItemClickListener mOnMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.record_switch:
                    isTrace = !isTrace;
                    if (isTrace) {//是纠偏轨迹
                        mRecord_switch.setIcon(R.mipmap.line);
                        SnackbarUtils.showDefaultLongSnackbar(rl_root, "已经切换到纠偏路线");
                    } else {//是真实轨迹
                        mRecord_switch.setIcon(R.mipmap.road);
                        SnackbarUtils.showDefaultLongSnackbar(rl_root, "已经切换到真实路线");
                    }
                    updateMap();
                    reWark();
                    break;
            }
            return false;
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MapRecordActivity.this.finish();
        }
    };
}
