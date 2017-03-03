package com.example.well.ndemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.well.ndemo.R;
import com.example.well.ndemo.ui.activity.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by ${LuoChen} on 2017/3/1.
 * email:luochen0519@foxmail.com
 */

public class ContentFragment extends Fragment {

    private BaseActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, null);
        ButterKnife.bind(this,view);
        mActivity = (BaseActivity) this.getActivity();

        return view;
    }


}
