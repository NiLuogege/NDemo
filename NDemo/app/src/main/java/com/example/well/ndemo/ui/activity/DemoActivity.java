package com.example.well.ndemo.ui.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;

import com.amap.api.maps.MapView;
import com.example.well.ndemo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by ${LuoChen} on 2017/4/25 13:39.
 * email:luochen0519@foxmail.com
 */

public class DemoActivity extends BaseActivity {
    @Bind(R.id.map)
    MapView mMapView;

    private SlidingDrawer mDrawer;
    private ImageView mImageView;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);// 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。

        mDrawer = (SlidingDrawer) findViewById(R.id.slidingdraw);
        mImageView = (ImageView) findViewById(R.id.handle);
        mListView = (ListView) findViewById(R.id.content);
        mListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getData()));

        mDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                mImageView.setImageResource(R.mipmap.ic_launcher);
            }
        });
        mDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                mImageView.setImageResource(R.mipmap.ic_launcher);
            }
        });


    }

    private List<String> getData(){

        List<String> data = new ArrayList<String>();
        data.add("测试数据1");
        data.add("测试数据2");
        data.add("测试数据3");
        data.add("测试数据4");
        data.add("测试数据5");
        data.add("测试数据6");
        data.add("测试数据7");
        data.add("测试数据8");
        data.add("测试数据9");
        data.add("测试数据10");
        data.add("测试数据11");
        data.add("测试数据12");
        data.add("测试数据13");
        data.add("测试数据1");
        data.add("测试数据2");
        data.add("测试数据3");
        data.add("测试数据4");
        data.add("测试数据5");
        data.add("测试数据6");
        data.add("测试数据7");
        data.add("测试数据8");
        data.add("测试数据9");
        data.add("测试数据10");
        data.add("测试数据11");
        data.add("测试数据12");
        data.add("测试数据13");
        return data;
    }
}
