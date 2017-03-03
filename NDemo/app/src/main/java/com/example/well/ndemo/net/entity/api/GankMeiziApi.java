package com.example.well.ndemo.net.entity.api;

import com.example.well.ndemo.net.rxretrofit.Api.BaseApi;
import com.example.well.ndemo.net.rxretrofit.listener.HttpOnNextListener;
import com.example.well.ndemo.net.rxretrofit.service.HttpPostService;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by ${LuoChen} on 2017/3/3 17:52.
 * email:luochen0519@foxmail.com
 */

public class GankMeiziApi extends BaseApi {
    private int page;

    public GankMeiziApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService httpPostService = retrofit.create(HttpPostService.class);
        return  httpPostService.getGankMeizi(getPage());
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
