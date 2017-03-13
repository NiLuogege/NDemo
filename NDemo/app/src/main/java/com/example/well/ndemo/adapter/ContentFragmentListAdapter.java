package com.example.well.ndemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.example.well.ndemo.R;
import com.example.well.ndemo.net.entity.resulte.GankMeiziReponse;
import com.example.well.ndemo.ui.activity.MeiziDetialActivity;
import com.example.well.ndemo.utils.SnackbarUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${LuoChen} on 2017/3/6 13:56.
 * email:luochen0519@foxmail.com
 */

public class ContentFragmentListAdapter extends RecyclerView.Adapter<ContentFragmentListAdapter.InnerViewHolder> {

    private static final int LIMIT = 48;
    private Context context;
    private List<GankMeiziReponse> data = null;

    public ContentFragmentListAdapter(Context context, List<GankMeiziReponse> data) {
        this.data = data;
        this.context = context;
    }


    @Override
    public InnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_content_fragment_list, null);
        return new InnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InnerViewHolder holder, int position) {
        GankMeiziReponse reponse = data.get(position);

        String text = reponse.desc.length() > LIMIT ? reponse.desc.substring(0, LIMIT) +
                "..." : reponse.desc;

        holder.mTv.setText(text);


        Glide.with(context)
                .load(reponse.url)
                .into(holder.mIv)
                .getSize(new SizeReadyCallback() {
            @Override
            public void onSizeReady(int width, int height) {
                if (!holder.mIv.isShown()) {//当holder.mIv的父类又不现实的时候调用
                    holder.mIv.setVisibility(View.VISIBLE);
                }
            }
        });

        bindListener(holder,reponse);
    }

    private void bindListener(InnerViewHolder holder,  final GankMeiziReponse reponse) {
        final InnerViewHolder innerHolder=holder;
        holder.mIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MeiziDetialActivity.class);
                intent.putExtra(MeiziDetialActivity.URL,reponse.url);
                context.startActivity(intent);
            }
        });

        holder.mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SnackbarUtils.showDefaultShortSnackbar(innerHolder.mCv, "正在开发");
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class InnerViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv)
        ImageView mIv;
        @Bind(R.id.tv)
        TextView mTv;
        @Bind(R.id.cv)
        CardView mCv;

        public InnerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
