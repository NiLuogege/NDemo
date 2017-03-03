package com.example.well.ndemo.net.entity.resulte;

/**
 * Created by ${LuoChen} on 2017/3/3 16:50.
 * email:luochen0519@foxmail.com
 */

public class GankMeiziReponse {
    public String _id;
    public String createdAt;//时间
    public String desc;//描述
    public String publishedAt;//展示时间
    public String source;//来源
    public String type;//类型
    public String url;//妹子图片URL
    public boolean used;//是否被使用
    public String who;//应该是谁上传的

    @Override
    public String toString() {
        return "GankMeiziReponse{" +
                "_id='" + _id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", desc='" + desc + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", source='" + source + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", used=" + used +
                ", who='" + who + '\'' +
                '}';
    }
}
