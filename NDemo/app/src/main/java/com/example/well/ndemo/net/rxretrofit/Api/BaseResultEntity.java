package com.example.well.ndemo.net.rxretrofit.Api;

/**
 * 回调信息统一封装类  封装了和后台约定的每个请求共有的部分
 */
public class BaseResultEntity<T> {

    private boolean error;

    //显示数据（用户需要关心的数据）
    private T results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getResult() {
        return results;
    }

    public void setResult(T result) {
        this.results = result;
    }

    @Override
    public String toString() {
        return "BaseResultEntity{" +
                "error=" + error +
                ", result=" + results +
                '}';
    }
}
