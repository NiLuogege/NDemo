package com.example.well.ndemo.ui.activity;

import android.Manifest;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.ui.fragment.ContentFragment;
import com.example.well.ndemo.utils.SPUtils;
import com.example.well.ndemo.utils.SettingsUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @Bind(R.id.fl_content)
    FrameLayout mFlContent;
    @Bind(R.id.nav)
    NavigationView nav;
    private static final String TAG_CONTENTFRAGMENT = "TagContentFragment";
    private int contentId = R.id.fl_content;
    private TextView mImage_description;
    private SwitchCompat mNight_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestPermission();
        initView();

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermission(permissions, new PermissionHandler() {
            });
        }
    }

    private void initView() {
        ContentFragment contentFragment = new ContentFragment();
        if (!contentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(contentId, contentFragment, TAG_CONTENTFRAGMENT).commitAllowingStateLoss();
        }

        mImage_description = (TextView) nav.getHeaderView(0).findViewById(R.id.image_description);

        MenuItem item = nav.getMenu().findItem(R.id.nav_night);

        mNight_switch = (SwitchCompat) MenuItemCompat.getActionView(item).findViewById(R.id.night_switch);
        mNight_switch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        boolean isNight = SPUtils.getInstance(getApplication()).getBoolean(SettingsUtils.IS_NIGHT_ON);
        if(isNight){
            mNight_switch.setChecked(true);
        }else{
            mNight_switch.setChecked(false);
        }
//        mNight_switch = (ImageView) MenuItemCompat.getActionView(item).findViewById(R.id.night_switch);
//        mNight_switch.setOnClickListener(mOnClickListener);
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


    CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
                        Color.WHITE,Color.WHITE};
                int[] iconcolor = new int[]{
                        Color.WHITE,Color.WHITE};
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
                        Color.BLACK,Color.BLACK};
                int[] iconcolor = new int[]{
                        Color.GRAY,Color.BLACK};
                nav.setItemTextColor(new ColorStateList(state, color));
                nav.setItemIconTintList(new ColorStateList(state, iconcolor));
            }
        }
    };
}
