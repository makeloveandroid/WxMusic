package com.wx.voice.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by wenyingzhi on 2019/1/9.
 */
@Entity
public class AdEntity {
    @Id(autoincrement = true)
    public long id;
    /**
     * 广告图片
     */
    @SerializedName("ad_image")
    public String adImage;

    /**
     * 图片url
     */
    @SerializedName("icon_url")
    public String iconUrl;
    /**
     * 推广apk下载地址
     */
    @SerializedName("apk_url")
    public String apkUrl;
    /**
     * 邀请码
     */
    public String code;

    /**
     * 简介使用
     */
    public String desc;

    /**
     * 详情url
     */
    @SerializedName("detail_url")
    public String detailUrl;

    /**
     * 标题
     */
    public String title;

    /**
     * 评分
     */
    @SerializedName("rating")
    public float rating;

    @Generated(hash = 1362028739)
    public AdEntity(long id, String adImage, String iconUrl, String apkUrl,
            String code, String desc, String detailUrl, String title,
            float rating) {
        this.id = id;
        this.adImage = adImage;
        this.iconUrl = iconUrl;
        this.apkUrl = apkUrl;
        this.code = code;
        this.desc = desc;
        this.detailUrl = detailUrl;
        this.title = title;
        this.rating = rating;
    }

    @Generated(hash = 1495001001)
    public AdEntity() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAdImage() {
        return this.adImage;
    }

    public void setAdImage(String adImage) {
        this.adImage = adImage;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getApkUrl() {
        return this.apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDetailUrl() {
        return this.detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getRating() {
        return this.rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
