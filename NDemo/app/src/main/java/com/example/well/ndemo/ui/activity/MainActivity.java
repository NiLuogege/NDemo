package com.example.well.ndemo.ui.activity;

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

    private static final String TAG_CONTENTFRAGMENT="TagContentFragment";
    private static final String TAG_LEFTFRAGMENT="TagLeftFragment";
    private int contentId=R.id.fl_content;
    private int leftId=R.id.fl_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ContentFragment contentFragment = new ContentFragment();
        getSupportFragmentManager().beginTransaction().add(contentId,contentFragment,TAG_CONTENTFRAGMENT).commitAllowingStateLoss();
        LeftFragment leftFragment = new LeftFragment();
        getSupportFragmentManager().beginTransaction().add(leftId,leftFragment,TAG_LEFTFRAGMENT).commitAllowingStateLoss();

    }


}
