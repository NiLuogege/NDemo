package com.example.well.ndemo.ui.activity

import android.os.Bundle
import android.util.Log
import com.example.well.ndemo.R
import com.example.well.ndemo.net.entity.api.AndroidAPI
import com.example.well.ndemo.net.entity.resulte.AndroidResult
import com.example.well.ndemo.net.rxretrofit.http.HttpManager
import com.example.well.ndemo.net.rxretrofit.listener.HttpOnNextListener

class KotlinActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        val androidAPI = AndroidAPI(listener, this)
        HttpManager.getInstance().doHttpDeal(androidAPI)
    }

    val listener = object : HttpOnNextListener<List<AndroidResult>>() {
        override fun onNext(t: List<AndroidResult>?) {
            Log.e("tag", t.toString())
        }

    }


}
