package com.example.well.ndemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

/**
 * Created by ${LuoChen} on 2017/3/3 17:08.
 * email:luochen0519@foxmail.com
 */

public class BaseActivity extends RxAppCompatActivity {
    protected BaseActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context=this;
    }
}
