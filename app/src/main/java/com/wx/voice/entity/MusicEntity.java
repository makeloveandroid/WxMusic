package com.wx.voice.entity;


public class MusicEntity {
    public String title;
    public String typeimg;
    public String type;
    public String decoding_url;

    public boolean isPlay = false;

    public MusicEntity() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTypeimg() {
        return typeimg;
    }

    public void setTypeimg(String typeimg) {
        this.typeimg = typeimg;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDecoding_url() {
        return decoding_url;
    }

    public void setDecoding_url(String decoding_url) {
        this.decoding_url = decoding_url;
    }


}
