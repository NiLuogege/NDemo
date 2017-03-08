package com.example.well.ndemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

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

    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_meizidetial);
        ButterKnife.bind(this);

        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this);

    }

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
