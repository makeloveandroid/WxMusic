package com.wx.voice.fragment.model;

import com.wx.voice.api.Api;
import com.wx.voice.fragment.contract.MusicTypeContract;
import com.wx.voice.manager.RetrofitManager;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.respones.MusicTypeRespones;
import com.wx.voice.respones.ResponseEntity;

import java.util.List;

import io.reactivex.Observable;


/**
 * Created by wenyingzhi on 2018/12/7.
 */
public class MusicTypeModel implements MusicTypeContract.Model {

    private final Api api;

    public MusicTypeModel() {
        api = RetrofitManager.getInstance().createApi();
    }

    @Override
    public Observable<ResponseEntity<List<MusicTypeRespones>>> requestData(BaseRequest request) {
        return api.getMusicType(request);
    }
}
