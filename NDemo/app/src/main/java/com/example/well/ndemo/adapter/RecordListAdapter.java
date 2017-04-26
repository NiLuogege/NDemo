package com.example.well.ndemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.bean.PathRecord;
import com.example.well.ndemo.ui.activity.MapRecordActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${LuoChen} on 2017/4/26 15:25.
 * email:luochen0519@foxmail.com
 */

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordListHolder> {
    private Context context;
    private List<PathRecord> data;

    public RecordListAdapter(Context context, List<PathRecord> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecordListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_record_list, null);
        return new RecordListHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordListHolder holder, int position) {
        PathRecord record = data.get(position);
        holder.tv_date.setText(record.getDate());
        String street_start = record.getStartpoint().getStreet();//街道
        String address_start = record.getStartpoint().getAddress();//地址
        String street_end = record.getEndpoint().getStreet();//街道
        String address_end = record.getEndpoint().getAddress();//地址
        holder.tv_fromTo.setText(street_start +"--->"+street_end);
        String distance = record.getDistance();//距离
        String duration = record.getDuration();//耗时
        String averagespeed = record.getAveragespeed();//速度
        holder.tv_describe.setText("距离: "+distance+"  耗时: "+duration+"  速度: "+averagespeed);
        if (BuildConfig.DEBUG) Log.e("RecordListAdapter", "address_start="+address_start+" address_end="+address_end);
        if (BuildConfig.DEBUG) Log.e("RecordListAdapter", record.toString());
        holder.ll_record_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapRecordActivity.class);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    static class RecordListHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_date)
        TextView tv_date;
        @Bind(R.id.tv_describe)
        TextView tv_describe;
        @Bind(R.id.tv_fromTo)
        TextView tv_fromTo;
        @Bind(R.id.ll_record_item)
        LinearLayout ll_record_item;

        public RecordListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
