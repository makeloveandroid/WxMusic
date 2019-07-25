package com.wx.voice.request;

/**
 * Created by wenyingzhi on 2018/12/10.
 */
public class MusicListRequest {
    public int page;
    public int id;

    public MusicListRequest(int page, int id) {
        this.page = page;
        this.id = id;
    }
}
