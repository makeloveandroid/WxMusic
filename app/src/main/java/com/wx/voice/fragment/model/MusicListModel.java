package com.wx.voice.fragment.model;

import com.wx.voice.api.Api;
import com.wx.voice.entity.MusicUserContent;
import com.wx.voice.fragment.contract.MusicListContract;
import com.wx.voice.manager.RetrofitManager;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.respones.MusicTypeRespones;
import com.wx.voice.respones.ResponseEntity;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by wenyingzhi on 2018/12/10.
 */
public class MusicListModel implements MusicListContract.Model {
    private final Api api;

    public MusicListModel() {
        api = RetrofitManager.getInstance().createApi();
    }

    @Override
    public Observable<ResponseEntity<List<MusicUserContent>>> requestData(BaseRequest request) {
        return api.getMusicListByType(request);
    }
}
