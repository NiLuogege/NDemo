package com.example.well.ndemo.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.net.entity.api.AppUploadApi;
import com.example.well.ndemo.net.entity.api.GankMeiziApi;
import com.example.well.ndemo.net.entity.resulte.GankMeiziReponse;
import com.example.well.ndemo.net.entity.resulte.Temp;
import com.example.well.ndemo.net.rxretrofit.Api.BaseResultEntity;
import com.example.well.ndemo.net.rxretrofit.http.HttpManager;
import com.example.well.ndemo.net.rxretrofit.listener.HttpOnNextListener;
import com.example.well.ndemo.ui.fragment.ContentFragment;
import com.example.well.ndemo.ui.fragment.LeftFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

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


//        getTemp();

        getMeizi();
    }

    private void getMeizi() {
        GankMeiziApi meiziApi = new GankMeiziApi(mGankMeiziListener,this);
        meiziApi.setPage(1);
        HttpManager.getInstance().doHttpDeal(meiziApi);
    }


    private HttpOnNextListener mGankMeiziListener = new HttpOnNextListener<List<GankMeiziReponse>>() {

        @Override
        public void onNext(List<GankMeiziReponse> gankFuLiReponses) {
          Log.e("MainActivity", gankFuLiReponses.toString());
        }
    };

    private void getTemp(){
        AppUploadApi appUploadApi = new AppUploadApi(mListener,this);
        appUploadApi.setCellphone("18729440250");
        appUploadApi.setBeginTime(1470499200000L);
        appUploadApi.setEndTime(1471881600000L);
        appUploadApi.setUnit("day");
        HttpManager.getInstance().doHttpDeal(appUploadApi);
    }

    HttpOnNextListener mListener = new HttpOnNextListener<List<Temp>>() {
        @Override
        public void onNext(List<Temp> temps) {
            if (BuildConfig.DEBUG) Log.e("MainActivity", temps.toString());
        }

        @Override
        public void onCacheNext(String cache) {
            super.onCacheNext(cache);
            Gson gson=new Gson();
            java.lang.reflect.Type type = new TypeToken<BaseResultEntity<List<Temp>>>() {}.getType();
            BaseResultEntity resultEntity= gson.fromJson(cache, type);
            if (BuildConfig.DEBUG) Log.e("MainActivity", cache.toString());
        }
    };
}
