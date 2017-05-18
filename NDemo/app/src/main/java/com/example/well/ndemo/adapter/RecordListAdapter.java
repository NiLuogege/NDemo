package com.example.well.ndemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.well.ndemo.R;
import com.example.well.ndemo.bean.NodemoMapLocation;
import com.example.well.ndemo.bean.PathRecord;
import com.example.well.ndemo.ui.activity.MapRecordActivity;

import java.text.DecimalFormat;
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
    private final DecimalFormat mFormat_1;
    private final DecimalFormat mFormat_2;
    private String mStreet_start;
    private String mStreet_end;

    public RecordListAdapter(Context context, List<PathRecord> data) {
        this.data = data;
        this.context = context;

        mFormat_1 = new DecimalFormat("0.0");
        mFormat_2 = new DecimalFormat("0.00");
    }

    public void setData(List<PathRecord> data) {
        this.data = data;
    }

    @Override
    public RecordListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_record_list, null);
        return new RecordListHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordListHolder holder, int position) {
        final PathRecord record = data.get(position);
        NodemoMapLocation startPoint = record.getStartPoint();
        NodemoMapLocation endPoint = record.getEndPoint();
        holder.tv_date.setText(record.getDate());
        if (startPoint != null) {
            mStreet_start = startPoint.getStreet(); //街道
        }
        if (endPoint != null) {
            mStreet_end = endPoint.getStreet();//街道
        }
        holder.tv_fromTo.setText(mStreet_start + "--->" + mStreet_end);
        String distance = mFormat_1.format(Double.parseDouble(record.getDistance()));//距离
        double h = Double.parseDouble(record.getDuration()) / 60 / 60;
        String duration = mFormat_2.format(h);//耗时
        String averagespeed = mFormat_2.format(Double.parseDouble(record.getAveragespeed()));//速度
        holder.tv_describe.setText("距离: " + distance + "m   耗时: " + duration + "h   速度: " + averagespeed + "m/s");
        holder.ll_record_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapRecordActivity.class);
                intent.putExtra(MapRecordActivity.EXTRAID, record.getId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void loadDataEnd() {
        notifyItemInserted(0);
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
