package com.example.well.ndemo.ui.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.well.ndemo.R;
import com.example.well.ndemo.ui.fragment.ContentFragment;
import com.example.well.ndemo.ui.fragment.LeftFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @Bind(R.id.fl_content)
    FrameLayout mFlContent;
    @Bind(R.id.fl_left)
    FrameLayout mFlLeft;

    private static final String TAG_CONTENTFRAGMENT = "TagContentFragment";
    private static final String TAG_LEFTFRAGMENT = "TagLeftFragment";
    private int contentId = R.id.fl_content;
    private int leftId = R.id.fl_left;

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

        LeftFragment leftFragment = new LeftFragment();
        if (!leftFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(leftId, leftFragment, TAG_LEFTFRAGMENT).commitAllowingStateLoss();
        }
    }


}
