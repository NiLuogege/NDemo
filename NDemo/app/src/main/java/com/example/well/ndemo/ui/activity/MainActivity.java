package com.example.well.ndemo.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.ui.fragment.ContentFragment;
import com.example.well.ndemo.utils.NetworkUtils;
import com.example.well.ndemo.utils.SPUtils;
import com.example.well.ndemo.utils.SettingsUtils;
import com.example.well.ndemo.utils.SnackbarUtils;
import com.example.well.ndemo.widget.globule_jbox2d.JboxLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @Bind(R.id.fl_content)
    FrameLayout mFlContent;
    @Bind(R.id.nav)
    NavigationView nav;
    @Bind(R.id.dl_main)
    DrawerLayout dl_main;
    private static final String TAG_CONTENTFRAGMENT = "TagContentFragment";
    private int contentId = R.id.fl_content;
    private TextView mImage_description;
    private SwitchCompat mNight_switch, mPush_switch;
    private ImageView mNav_image;
    private JboxLayout mJboxLayout;
    private Sensor mSensor;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        boolean networkAvailable = NetworkUtils.isNetworkAvailable(context);
        if (networkAvailable) {
            requestPermission();
            initShareSDK();
            initView();
            setFirstEnterView();
            initBroadcastReceiver();
            initSensor();
        } else {
            SnackbarUtils.showDefaultLongSnackbar(dl_main, getString(R.string.networkUnuseable));
        }


    }

    private void initSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mReceiver);
        }

        if (dl_main != null) {
            dl_main.removeDrawerListener(mDrawerListener);
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
//                    ,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE};
            requestPermission(permissions, new PermissionHandler() {
                @Override
                public void onGranted() {
                    super.onGranted();
                    creatBaseDir();
                }
            });
        }
    }


    private void initView() {
        ContentFragment contentFragment = new ContentFragment();
        if (!contentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(contentId, contentFragment, TAG_CONTENTFRAGMENT).commitAllowingStateLoss();
        }

        mNav_image = (ImageView) nav.getHeaderView(0).findViewById(R.id.nav_image);
        mJboxLayout = (JboxLayout) nav.getHeaderView(0).findViewById(R.id.jboxl);
        mImage_description = (TextView) nav.getHeaderView(0).findViewById(R.id.image_description);
        MenuItem item_night = nav.getMenu().findItem(R.id.nav_night);
        MenuItem item_push = nav.getMenu().findItem(R.id.nav_image_push);
        MenuItem item_map = nav.getMenu().findItem(R.id.map);
        MenuItem item_kotlin = nav.getMenu().findItem(R.id.kotlin);
        item_map.setOnMenuItemClickListener(mOnMenuItemClickListener);
        item_kotlin.setOnMenuItemClickListener(mOnMenuItemClickListener);
        mNav_image.setOnClickListener(mOnClickListener);

        mNight_switch = (SwitchCompat) MenuItemCompat.getActionView(item_night).findViewById(R.id.night_switch);
        mPush_switch = (SwitchCompat) MenuItemCompat.getActionView(item_push).findViewById(R.id.push_switch);
        mNight_switch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mPush_switch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        setSwitch();
        setNavImage();
//        mNight_switch = (ImageView) MenuItemCompat.getActionView(item).findViewById(R.id.night_switch);
//        mNight_switch.setOnClickListener(mOnClickListener);

        dl_main.addDrawerListener(mDrawerListener);
    }

    private void setFirstEnterView() {
        boolean isFirstEnter = SPUtils.getInstance(getApplication()).getBoolean(SettingsUtils.IS_FIRST_ENTER, true);
        if (BuildConfig.DEBUG) Log.e("MainActivity", "isFirstEnter:" + isFirstEnter);
        if (isFirstEnter) {
            mPush_switch.setChecked(true);
            SPUtils.getInstance(getApplication()).put(SettingsUtils.IS_FIRST_ENTER, false);
            SPUtils.getInstance(getApplication()).put(SettingsUtils.IS_LOAD_PUSH_IMAGE, true);
        }
    }


    /**
     * 改mNav_image设置缓存中的图片
     */
    private void setNavImage() {
        String url = SPUtils.getInstance(getApplication()).getString(SettingsUtils.CATCH_IMAGE_PUSH);
        if (!TextUtils.isEmpty(url)) {
            boolean matches = Patterns.WEB_URL.matcher(url).matches();
            if (matches) {
                Glide.with(context).load(url).asBitmap().into(mNav_image);
            }
        }
    }

    /**
     * 初始化切换按钮
     */
    private void setSwitch() {
        setNightSwitch();
        setPushSwitch();
    }

    private void setPushSwitch() {
        boolean isLoadImagePush = SPUtils.getInstance(getApplication()).getBoolean(SettingsUtils.IS_LOAD_PUSH_IMAGE);
        if (isLoadImagePush) {
            mPush_switch.setChecked(true);
        } else {
            mPush_switch.setChecked(false);
        }
    }


    /**
     * 设置夜间模式的switch是否开启
     */
    private void setNightSwitch() {
        boolean isNight = SPUtils.getInstance(getApplication()).getBoolean(SettingsUtils.IS_NIGHT_ON);
        if (isNight) {
            mNight_switch.setChecked(true);
        } else {
            mNight_switch.setChecked(false);
        }
    }

    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SettingsUtils.ACTION_IMAGE_PUSH);
        LocalBroadcastManager.getInstance(context).registerReceiver(mReceiver, filter);
    }


    /**
     * 是否开启加载推送来的图片的按钮
     *
     * @param isChecked
     */
    private void isLoadPushImage(boolean isChecked) {
        SPUtils.getInstance(getApplication()).put(SettingsUtils.IS_LOAD_PUSH_IMAGE, isChecked);
    }

    /**
     * 切换模式
     *
     * @param isChecked
     */
    private void changeNightMode(boolean isChecked) {
        SPUtils.getInstance(getApplication()).put(SettingsUtils.IS_NIGHT_ON, isChecked);

        if (BuildConfig.DEBUG) Log.e("MainActivity", "isChecked:" + isChecked);

        if (isChecked) {//夜间模式
            mImage_description.setTextColor(getResources().getColor(android.R.color.darker_gray));
            nav.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            int[][] state = new int[][]{
                    new int[]{-android.R.attr.state_checked}, // unchecked
                    new int[]{android.R.attr.state_checked}  // pressed
            };

            int[] color = new int[]{
                    Color.WHITE, Color.WHITE};
            int[] iconcolor = new int[]{
                    Color.WHITE, Color.WHITE};
            nav.setItemTextColor(new ColorStateList(state, color));
            nav.setItemIconTintList(new ColorStateList(state, iconcolor));

        } else {//白天
            mImage_description.setTextColor(getResources().getColor(android.R.color.white));
            nav.setBackgroundColor(getResources().getColor(android.R.color.white));
            int[][] state = new int[][]{
                    new int[]{-android.R.attr.state_checked}, // unchecked
                    new int[]{android.R.attr.state_checked}  // pressed
            };

            int[] color = new int[]{
                    Color.BLACK, Color.BLACK};
            int[] iconcolor = new int[]{
                    Color.GRAY, Color.BLACK};
            nav.setItemTextColor(new ColorStateList(state, color));
            nav.setItemIconTintList(new ColorStateList(state, iconcolor));
        }
    }


    public void openDrawer() {
        dl_main.openDrawer(nav);
    }

    //    View.OnClickListener mOnClickListener=new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            boolean isNight = SPUtils.getInstance(getApplication()).getBoolean(SettingsUtils.IS_NIGHT_ON);
//            if(isNight){
//                mNight_switch.setImageResource(R.mipmap.on);
//            }else {
//                mNight_switch.setImageResource(R.mipmap.off);
//            }
//
//            SPUtils.getInstance(getApplication()).put(SettingsUtils.IS_NIGHT_ON,!isNight);
//            recreate();
//
//        }
//    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BuildConfig.DEBUG) Log.e("MainActivity", "onReceive");
            boolean isLoadImagePush = SPUtils.getInstance(getApplication()).getBoolean(SettingsUtils.IS_LOAD_PUSH_IMAGE);
            if (isLoadImagePush) {
                String url = SPUtils.getInstance(getApplication()).getString(SettingsUtils.CATCH_IMAGE_PUSH);
                Glide.with(context).load(url).asBitmap().into(mNav_image);
            }
        }
    };


    CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            int id = buttonView.getId();
            switch (id) {
                case R.id.night_switch:
                    changeNightMode(isChecked);
                    break;
                case R.id.push_switch:
                    isLoadPushImage(isChecked);
                    break;
                default:
                    break;
            }


        }
    };

    MenuItem.OnMenuItemClickListener mOnMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.map:
                    Intent intent = new Intent(context, MapActivity.class);
                    ActivityOptionsCompat compat = ActivityOptionsCompat.makeScaleUpAnimation(mNav_image, mNav_image.getWidth() / 2, mNav_image.getHeight(), 0, 0);
                    Bundle bundle = compat.toBundle();
                    startActivity(intent, bundle);
                    break;

                case R.id.kotlin:
//                    Intent intent_kotlin = new Intent(context,KotlinActivity.class);
//                    startActivity(intent_kotlin);
                    break;
            }
            dl_main.closeDrawer(nav, false);
            return false;
        }
    };

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isLoadImagePush = SPUtils.getInstance(getApplication()).getBoolean(SettingsUtils.IS_LOAD_PUSH_IMAGE);
            if (isLoadImagePush) {
                String url = SPUtils.getInstance(getApplication()).getString(SettingsUtils.CATCH_IMAGE_PUSH);
                Intent intent = new Intent(context, BigImageActivity.class);
                intent.putExtra(BigImageActivity.EXTRA_URL, url);
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, mNav_image, getString(R.string.share_big_image_activity));
                Bundle bundle = compat.toBundle();
                ActivityCompat.startActivity(context, intent, bundle);
            }

        }
    };

    DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_UI);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mSensorManager.unregisterListener(mSensorEventListener);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    };

    SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();
            if(type==Sensor.TYPE_ACCELEROMETER){
                float[] values = event.values;
                float x = values[0];
                float y = values[1];
                mJboxLayout.onSensorChanged(-x,y*2);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}
