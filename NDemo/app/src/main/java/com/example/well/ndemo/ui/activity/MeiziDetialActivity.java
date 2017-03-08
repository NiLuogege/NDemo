package com.example.well.ndemo.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.view.ElasticDragDismissFrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${LuoChen} on 2017/3/7 9:40.
 * email:luochen0519@foxmail.com
 */

public class MeiziDetialActivity extends BaseActivity {
    @Bind(R.id.draggable_frame)
    ElasticDragDismissFrameLayout mDraggableFrame;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_meizidetial);
        ButterKnife.bind(this);
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this);


        initListener();
    }

    private void initListener() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){//如果系统版本大于21
            getWindow().getSharedElementEnterTransition().addListener(mTransitionListener);
        }

    }

    private Transition.TransitionListener mTransitionListener=new Transition.TransitionListener() {

        @Override
        public void onTransitionStart(Transition transition) {
           if (BuildConfig.DEBUG) Log.e("MeiziDetialActivity", "onTransitionStart");

            mToolbar.animate().alpha(0).setDuration(5000).setInterpolator(new LinearOutSlowInInterpolator());

        }

        @Override
        public void onTransitionEnd(Transition transition) {

        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        mDraggableFrame.addListener(chromeFader);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDraggableFrame.removeListener(chromeFader);
    }
}
