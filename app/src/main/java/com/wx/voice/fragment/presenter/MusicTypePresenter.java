package com.wx.voice.fragment.presenter;

import com.wx.voice.base.presenters.XBasePresenter;
import com.wx.voice.fragment.contract.MusicTypeContract;
import com.wx.voice.fragment.model.MusicTypeModel;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.observer.BaseObserver;
import com.wx.voice.respones.MusicTypeRespones;
import com.wx.voice.respones.ResponseEntity;
import com.wx.voice.util.L;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by wenyingzhi on 2018/12/7.
 */
public class MusicTypePresenter extends XBasePresenter<MusicTypeContract.View, MusicTypeModel> implements MusicTypeContract.Presenter {
    @Override
    public void load() {
        view.showLoading();
        model
                .requestData(view.getRequestData())
                .compose(RxTransformerHelper.<ResponseEntity<List<MusicTypeRespones>>>schedulerTransf())
                .subscribe(new BaseObserver<List<MusicTypeRespones>>(this) {

                    @Override
                    public void onNetError(int type, String errorMsg) {
                        view.showError(type,errorMsg);
                    }

                    @Override
                    public void onSuccess(List<MusicTypeRespones> musicTypeRespones) {
                        view.setData(musicTypeRespones);
                    }
                });


    }
}
