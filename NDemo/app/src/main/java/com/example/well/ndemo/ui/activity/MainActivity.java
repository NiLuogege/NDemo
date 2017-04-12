package com.example.well.ndemo.ui.activity;

import android.Manifest;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.widget.FrameLayout;

import com.example.well.ndemo.R;
import com.example.well.ndemo.ui.fragment.ContentFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @Bind(R.id.fl_content)
    FrameLayout mFlContent;
    @Bind(R.id.nav)
    NavigationView nav;

    private static final String TAG_CONTENTFRAGMENT = "TagContentFragment";
    private int contentId = R.id.fl_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        requestPermission();
        initView();

    }

    private void requestPermission() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermission(permissions, new PermissionHandler() {});
        }
    }

    private void initView() {
        ContentFragment contentFragment = new ContentFragment();
        if (!contentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(contentId, contentFragment, TAG_CONTENTFRAGMENT).commitAllowingStateLoss();
        }


        NavigationViewColor();
    }

    private void NavigationViewColor() {
        int[][] state = new int[][]{
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_checked}  // pressed
        };

        int[] color = new int[]{
                Color.BLACK,Color.BLACK};
        int[] iconcolor = new int[]{
                Color.GRAY,Color.BLACK};
        nav.setItemTextColor(new ColorStateList(state, color));//设置文字颜色
        nav.setItemIconTintList(new ColorStateList(state, iconcolor));//设置icon的颜色
    }





}
