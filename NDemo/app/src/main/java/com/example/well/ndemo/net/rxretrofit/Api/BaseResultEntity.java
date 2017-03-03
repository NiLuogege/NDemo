package com.example.well.ndemo.net.rxretrofit.Api;

/**
 * 回调信息统一封装类  封装了和后台约定的每个请求共有的部分
 */
public class BaseResultEntity<T> {

    private boolean error;

    //显示数据（用户需要关心的数据）
    private T result;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
