package com.example.well.ndemo.ui.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.well.ndemo.R
import com.example.well.ndemo.adapter.AndroidAdapter
import com.example.well.ndemo.net.entity.api.AndroidAPI
import com.example.well.ndemo.net.entity.resulte.AndroidResult
import com.example.well.ndemo.net.rxretrofit.http.HttpManager
import com.example.well.ndemo.net.rxretrofit.listener.HttpOnNextListener
import kotlinx.android.synthetic.main.activity_kotlin.*
import java.util.*

class KotlinActivity : BaseActivity() {
    private var data = ArrayList<AndroidResult>()
//    private  var adapter : AndroidAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        rv.layoutManager= LinearLayoutManager(context)
        getData()
    }

    private fun getData() {
        val androidAPI = AndroidAPI(listener, this)
        HttpManager.getInstance().doHttpDeal(androidAPI)
    }

    val listener = object : HttpOnNextListener<List<AndroidResult>>() {
        override fun onNext(t: List<AndroidResult>?) {
            data= t as ArrayList<AndroidResult>
            val adapter = AndroidAdapter(context, data)
            rv.adapter=adapter
            Log.e("tag", t.toString())
        }

    }


}
