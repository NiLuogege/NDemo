package com.example.well.ndemo.net.entity.api

import com.example.well.ndemo.net.entity.resulte.AndroidResult
import com.example.well.ndemo.net.rxretrofit.Api.BaseApi
import com.example.well.ndemo.net.rxretrofit.listener.HttpOnNextListener
import com.example.well.ndemo.net.rxretrofit.service.HttpPostService
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import retrofit2.Retrofit
import rx.Observable

/**
 * Created by ${LuoChen} on 2017/6/2 14:06.
 * email:luochen0519@foxmail.com
 */
class AndroidAPI : BaseApi<AndroidAPI> {



    constructor(listener: HttpOnNextListener<List<AndroidResult>>, rxAppCompatActivity: RxAppCompatActivity) : super(listener, rxAppCompatActivity) {
        isCache = false
    }
    override fun getObservable(retrofit: Retrofit?): Observable<*> {
        val result = retrofit!!.create(HttpPostService::class.java)
        return result.getAndroid(1)
    }
}