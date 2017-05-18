package com.example.well.ndemo.bean;

import java.io.Serializable;

/**
 * Created by ${LuoChen} on 2017/5/4 14:25.
 * email:luochen0519@foxmail.com
 *
 * 因为AMapLocation不能直接通过Intent传输 所以用这个类作为载体 进行数据传输
 */

public class NodemoMapLocation implements Serializable{

    private String district;//城区信息
    private String street;//街道信息
    private double latitude;//经度
    private double longitude;//纬度
    private String mProvider;
    private long mTime = 0;
    private float mTotalSpeed = 0.0f;
    private float mSpeed = 0.0f;

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    private float mBearing = 0.0f;


    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProvider() {
        return mProvider;
    }

    public void setProvider(String provider) {
        mProvider = provider;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public float getTotalSpeed() {
        return mTotalSpeed;
    }

    public void setTotalSpeed(float totalSpeed) {
        mTotalSpeed = totalSpeed;
    }

    public float getBearing() {
        return mBearing;
    }

    public void setBearing(float bearing) {
        mBearing = bearing;
    }

    @Override
    public String toString() {
        return "NodemoMapLocation{" +
                "district='" + district + '\'' +
                ", street='" + street + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", mProvider='" + mProvider + '\'' +
                ", mTime=" + mTime +
                ", mTotalSpeed=" + mTotalSpeed +
                ", mSpeed=" + mSpeed +
                ", mBearing=" + mBearing +
                '}';
    }
}
