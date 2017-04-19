package com.example.well.ndemo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
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
    private Activity context;
    private List<GankMeiziReponse> data = null;
    private boolean isLoadMore=false;//是否是加载更多

    public ContentFragmentListAdapter(Activity context, List<GankMeiziReponse> data) {
        this.data = data;
        this.context = context;
    }

    public void addData(final List<GankMeiziReponse> newData){
        data.addAll(newData);
//        notifyDataSetChanged();
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
//        holder.mTv.setText("position="+position);
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

    private void bindListener(final InnerViewHolder holder, final GankMeiziReponse reponse) {
        final InnerViewHolder innerHolder=holder;
        holder.mIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MeiziDetialActivity.class);
                intent.putExtra(MeiziDetialActivity.URL,reponse.url);
//                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, Pair.create((View)holder.mIv,R.string.share_meizi_detial_activity));
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeClipRevealAnimation(holder.mIv, (int) holder.mIv.getX(), (int) holder.mIv.getY(),holder.mIv.getWidth(),holder.mIv.getHeight());
                Bundle bundle = optionsCompat.toBundle();
                context.startActivity(intent,bundle);
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

    public void loadingMoreStart() {
        if(!isLoadMore) return;
        isLoadMore=true;
        notifyItemInserted(getLoadingMoreItemPosition());
    }

    public void loadingMoreEnd() {
        if(isLoadMore) return;
        isLoadMore=false;
        notifyItemRemoved(getLoadingMoreItemPosition());
    }

    private  int getLoadingMoreItemPosition(){
        return  getItemCount()-1;
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
