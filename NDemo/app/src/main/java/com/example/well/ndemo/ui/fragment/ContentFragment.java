package com.example.well.ndemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.adapter.ContentFragmentListAdapter;
import com.example.well.ndemo.net.entity.api.GankMeiziApi;
import com.example.well.ndemo.net.entity.resulte.GankMeiziReponse;
import com.example.well.ndemo.net.rxretrofit.Api.BaseResultEntity;
import com.example.well.ndemo.net.rxretrofit.http.HttpManager;
import com.example.well.ndemo.net.rxretrofit.listener.HttpOnNextListener;
import com.example.well.ndemo.ui.activity.BaseActivity;
import com.example.well.ndemo.utils.SpacesItemDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${LuoChen} on 2017/3/1.
 * email:luochen0519@foxmail.com
 */

public class ContentFragment extends Fragment {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.rv)
    RecyclerView mRv;


    private BaseActivity mActivity;
    private static final int SPANCOUNT = 2;//列表列数
    public List<GankMeiziReponse> mData = new ArrayList<>();//数据集合
    private ContentFragmentListAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    
    private int[] into = new int[2];//获取RecycleView中第一个item的position
    private boolean  isLoading = false;//是否在请求网络的标志
    private boolean isLoadMore=false;//是否是加载更多
    private int page = 1;//页数标志



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, null);
        ButterKnife.bind(this, view);
        mActivity = (BaseActivity) this.getActivity();
        initView(view);
        initListener();
        initData();
        return view;
    }



    private void initView(View view) {
        mActivity.setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mLayoutManager = new StaggeredGridLayoutManager(SPANCOUNT, StaggeredGridLayoutManager.VERTICAL);
        mRv.addItemDecoration(new SpacesItemDecoration(10));
        mRv.setLayoutManager(mLayoutManager);
    }


    private void initListener() {
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy>0){//向下滚动
                    int totleChild = mLayoutManager.getItemCount();//item的总数
                    int visiableChild = mLayoutManager.getChildCount();// 可见的item数
                    int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPositions(into)[0];

                    if(!isLoadMore&&(visiableChild+firstVisibleItemPosition)>totleChild){//已经到底了
                        isLoadMore=true;
                        ++page;
                        getMeizi();

                        if (BuildConfig.DEBUG) Log.e("ContentFragment", "loadMore");
                    }

                }else{//向上滚动

                }

            }
        });
    }

    private void initData() {
        getMeizi();
    }

    private void getMeizi() {
        GankMeiziApi meiziApi = new GankMeiziApi(mGankMeiziListener, mActivity);
        meiziApi.setPage(page);
        isLoading=true;
        HttpManager.getInstance().doHttpDeal(meiziApi);
    }


    private HttpOnNextListener mGankMeiziListener = new HttpOnNextListener<List<GankMeiziReponse>>() {

        @Override
        public void onNext(List<GankMeiziReponse> gankFuLiReponses) {
            isLoading=false;
            mData = gankFuLiReponses;
            onLoadDataFinish(mData);
        }

        @Override
        public void onCacheNext(String string) {
            super.onCacheNext(string);
            isLoading=false;
            Gson gson = new Gson();
            Type type = new TypeToken<BaseResultEntity<List<GankMeiziReponse>>>() {
            }.getType();
            BaseResultEntity bre = gson.fromJson(string, type);
            mData = (List<GankMeiziReponse>) bre.getResult();
            onLoadDataFinish(mData);
        }

        @Override
        public void onCancel() {
            super.onCancel();
            isLoading=false;
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            isLoading=false;
        }
    };

    private void onLoadDataFinish(List<GankMeiziReponse> data) {
        if(isLoadMore){
            loadMoreData(data);
        }else{
            loadData(data);
        }
    }

    private void loadMoreData(List<GankMeiziReponse> data) {
        isLoadMore=false;
        mAdapter.addData(data);
    }

    private void loadData(List<GankMeiziReponse> data) {
        mAdapter = new ContentFragmentListAdapter(mActivity,data);
        mRv.setAdapter(mAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
