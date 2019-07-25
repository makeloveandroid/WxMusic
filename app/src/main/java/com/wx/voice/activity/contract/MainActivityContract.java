package com.wx.voice.activity.contract;

import com.wx.voice.base.contracts.XContract;
import com.wx.voice.entity.AdEntity;
import com.wx.voice.respones.ResponseEntity;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by wenyingzhi on 2019/1/8.
 */
public interface MainActivityContract {
    interface View extends XContract.View {
        void showLoadData(String msg);

        void setAdList(List<AdEntity> ads);
    }

    interface Presenter extends XContract.Presenter {
        void loadData();
    }

    interface Model extends XContract.Model {
        Observable<ResponseEntity<List<AdEntity>>> load();
    }
}
