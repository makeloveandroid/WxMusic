package com.wx.voice.activity.model;

import com.wx.voice.activity.contract.MainActivityContract;
import com.wx.voice.app.App;
import com.wx.voice.entity.AdEntity;
import com.wx.voice.manager.RetrofitManager;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.respones.ResponseEntity;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by wenyingzhi on 2019/1/8.
 */
public class MainActivityModel implements MainActivityContract.Model {
    @Override
    public Observable<ResponseEntity<List<AdEntity>>> load() {
        return RetrofitManager
                .getInstance()
                .createApi()
                .getAds(new BaseRequest(App.getContext(), "core", "GET_ADS", "CORE"));
    }
}
