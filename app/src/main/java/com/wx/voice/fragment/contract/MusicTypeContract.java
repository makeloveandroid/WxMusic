package com.wx.voice.fragment.contract;

import com.wx.voice.base.contracts.XContract;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.respones.MusicTypeRespones;
import com.wx.voice.respones.ResponseEntity;

import java.util.List;

import io.reactivex.Observable;


/**
 * Created by wenyingzhi on 2018/12/7.
 */
public interface MusicTypeContract {
    interface View extends XContract.View {
        BaseRequest getRequestData();

        void setData(List<MusicTypeRespones> musicTypeRespones);
    }

    interface Presenter extends XContract.Presenter {
        void load();
    }

    interface Model extends XContract.Model {
        Observable<ResponseEntity<List<MusicTypeRespones>>> requestData(BaseRequest request);
    }
}
