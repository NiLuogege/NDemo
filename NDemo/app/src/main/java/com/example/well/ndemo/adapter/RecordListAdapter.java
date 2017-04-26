package com.example.well.ndemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.well.ndemo.R;
import com.example.well.ndemo.bean.PathRecord;

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
        holder.tv_describe.setText(record.getDuration());
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

        public RecordListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
