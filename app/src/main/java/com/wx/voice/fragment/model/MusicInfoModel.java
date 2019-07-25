package com.wx.voice.fragment.model;

import com.wx.voice.api.Api;
import com.wx.voice.entity.MusicEntity;
import com.wx.voice.entity.MusicUserContent;
import com.wx.voice.fragment.contract.MusicInfoContract;
import com.wx.voice.manager.RetrofitManager;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.respones.ResponseEntity;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by wenyingzhi on 2018/12/11.
 */
public class MusicInfoModel implements MusicInfoContract.Model {
    private final Api api;

    public MusicInfoModel() {
        api = RetrofitManager.getInstance().createApi();
    }

    @Override
    public Observable<ResponseEntity<List<MusicEntity>>> requestData(BaseRequest request) {
        return api.getMusicListInfo(request);
    }


}
