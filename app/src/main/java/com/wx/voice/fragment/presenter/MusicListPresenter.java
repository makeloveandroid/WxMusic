package com.wx.voice.fragment.presenter;

import com.wx.voice.base.presenters.XBasePresenter;
import com.wx.voice.entity.MusicUserContent;
import com.wx.voice.fragment.contract.MusicListContract;
import com.wx.voice.fragment.model.MusicListModel;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.observer.BaseObserver;
import com.wx.voice.respones.ResponseEntity;
import com.wx.voice.util.L;

import java.util.List;

/**
 * Created by wenyingzhi on 2018/12/10.
 */
public class MusicListPresenter extends XBasePresenter<MusicListContract.View, MusicListModel> implements MusicListContract.Presenter {

    @Override
    public void load() {
        view.showLoading();
        model
                .requestData(view.getRequestData())
                .compose(RxTransformerHelper.<ResponseEntity<List<MusicUserContent>>>schedulerTransf())
                .subscribe(new BaseObserver<List<MusicUserContent>>(this) {
                    @Override
                    public void onNetError(int type, String errorMsg) {
                        view.showError(type, errorMsg);
                    }

                    @Override
                    public void onSuccess(List<MusicUserContent> musicUserContents) {
                        view.setData(musicUserContents);
                    }
                });
    }

    @Override
    public void loadMore() {
        model
                .requestData(view.getRequestData())
                .compose(RxTransformerHelper.<ResponseEntity<List<MusicUserContent>>>schedulerTransf())
                .subscribe(new BaseObserver<List<MusicUserContent>>(this) {
                    @Override
                    public void onNetError(int type, String errorMsg) {
                        if (type==4){
                            view.loadMoreEnd();
                        }else {
                            view.loadMoreOk();
                            view.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onSuccess(List<MusicUserContent> musicUserContents) {
                        view.setMoreData(musicUserContents);
                        view.loadMoreOk();
                    }


                });
    }
}
