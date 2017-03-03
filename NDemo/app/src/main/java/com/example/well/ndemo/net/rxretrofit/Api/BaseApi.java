package com.example.well.ndemo.net.rxretrofit.Api;


import com.example.well.ndemo.net.rxretrofit.exception.HttpTimeException;
import com.example.well.ndemo.net.rxretrofit.listener.HttpOnNextListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.lang.ref.SoftReference;

import retrofit2.Retrofit;
import rx.Observable;
import rx.functions.Func1;

/**
 * 请求数据统一封装类  该类会对请求进行配置如 是否进行缓存,是否显示加载框等等
 *
 * 当请求完成后会在call方法中进行判断是否满足和后台约定好的成功
 */
public abstract class BaseApi<T> implements Func1<BaseResultEntity<T>, T> {
    //rx生命周期管理
    private SoftReference<RxAppCompatActivity> rxAppCompatActivity;
    /*回调*/
    private SoftReference<HttpOnNextListener> listener;
    /*是否能取消加载框*/
    private boolean cancel;
    /*是否显示加载框*/
    private boolean showProgress;
    /*是否需要缓存处理*/
    private boolean cache;
    /*基础url*/
    private String baseUrl="http://gank.io/api/";
    /*方法-如果需要缓存必须设置这个参数；不需要不用設置------>会与baseURL合并后当成键存入数据库*/
    private String mothed;
    /*超时时间-默认6秒*/
    private int connectionTime = 6;
    /*有网情况下的本地缓存时间默认60秒*/
    private int cookieNetWorkTime=60;
    /*无网络的情况下本地缓存时间默认30天*/
    private int cookieNoNetWorkTime=24*60*60*30;

    public BaseApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        setListener(listener);
        setRxAppCompatActivity(rxAppCompatActivity);
        setShowProgress(true);
        setCache(true);
    }

    /**
     * 设置参数
     *
     * @param retrofit
     * @return
     */
    public abstract Observable getObservable(Retrofit retrofit);



    public int getCookieNoNetWorkTime() {
        return cookieNoNetWorkTime;
    }

    public void setCookieNoNetWorkTime(int cookieNoNetWorkTime) {
        this.cookieNoNetWorkTime = cookieNoNetWorkTime;
    }

    public int getCookieNetWorkTime() {
        return cookieNetWorkTime;
    }

    public void setCookieNetWorkTime(int cookieNetWorkTime) {
        this.cookieNetWorkTime = cookieNetWorkTime;
    }

    public String getMothed() {
        return mothed;
    }

    public int getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(int connectionTime) {
        this.connectionTime = connectionTime;
    }

    public void setMothed(String mothed) {
        this.mothed = mothed;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUrl() {
        return baseUrl+mothed;
    }

    public void setRxAppCompatActivity(RxAppCompatActivity rxAppCompatActivity) {
        this.rxAppCompatActivity =new SoftReference(rxAppCompatActivity) ;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public boolean isCancel() {
         return cancel;
     }

     public void setCancel(boolean cancel) {
         this.cancel = cancel;
     }

     public SoftReference<HttpOnNextListener> getListener() {
         return listener;
     }

     public void setListener(HttpOnNextListener listener) {
         this.listener = new SoftReference(listener);
     }

    /*
     * 获取当前rx生命周期
     * @return
     */
    public  RxAppCompatActivity getRxAppCompatActivity() {
        return rxAppCompatActivity.get();
    }

    @Override
    public T call(BaseResultEntity<T> httpResult) {
        if(!httpResult.isError()){
            throw new HttpTimeException(0);
        }
        return httpResult.getResult();
    }
}
