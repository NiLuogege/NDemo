package com.example.well.ndemo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.example.well.ndemo.R
import com.example.well.ndemo.net.entity.resulte.AndroidResult
import kotlinx.android.synthetic.main.item_android.view.*

/**
 * Created by ${LuoChen} on 2017/6/2 16:02.
 * email:luochen0519@foxmail.com
 */
class AndroidAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private var data: ArrayList<AndroidResult>

    private var context: Context? = null

    constructor(context: Context, data: ArrayList<AndroidResult>) {
        this.data = data
        this.context = context
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        Log.e("tag","position= ${position}")
        val item = data.get(position)
        holder?.itemView?.tv_who?.text = item.who
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = View.inflate(context, R.layout.item_android, null)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        Log.e("tag","size= ${data.size}")
        return data.size
    }



    class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView)
}