package com.wx.voice.activity.presenter;

import com.wx.voice.activity.contract.MainActivityContract;
import com.wx.voice.activity.model.MainActivityModel;
import com.wx.voice.app.App;
import com.wx.voice.base.presenters.XBasePresenter;
import com.wx.voice.entity.AdEntity;
import com.wx.voice.manager.RetrofitManager;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.observer.BaseObserver;
import com.wx.voice.recorder.utils.Log;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.respones.ResponseEntity;
import com.wx.voice.util.L;

import java.util.List;

/**
 * Created by wenyingzhi on 2019/1/8.
 */
public class MainActivityPresenter extends XBasePresenter<MainActivityContract.View, MainActivityModel> implements MainActivityContract.Presenter {
    @Override
    public void loadData() {
        List<AdEntity> list =
                App.getDaoSession()
                        .getAdEntityDao()
                        .queryBuilder()
                        .list();
        if (list == null || list.size() <= 0) {
            view.showLoadData("正在拉取数据");
            model
                    .load()
                    .compose(RxTransformerHelper.<ResponseEntity<List<AdEntity>>>schedulerTransf())
                    .subscribe(new BaseObserver<List<AdEntity>>(this) {
                        @Override
                        public void onNetError(int type, String errorMsg) {
                            view.showError(0, errorMsg);
                        }

                        @Override
                        public void onSuccess(List<AdEntity> list) {
                            if (list == null || list.size() < 0) {
                                view.showError(0, "请求数据出错!");
                            } else {
                                view.setAdList(list);
                                App.getDaoSession().getAdEntityDao().insertOrReplaceInTx(list);
                            }
                        }
                    });

        } else {
            view.setAdList(list);
            // 请求下数据
            requestData();
        }
    }

    private void requestData() {
        RetrofitManager
                .getInstance()
                .createApi()
                .getAds(new BaseRequest(App.getContext(), "core", "GET_ADS", "CORE"))
                .compose(RxTransformerHelper.<ResponseEntity<List<AdEntity>>>schedulerTransf())
                .subscribe(new BaseObserver<List<AdEntity>>(this) {
                    @Override
                    public void onNetError(int type, String errorMsg) {
                    }

                    @Override
                    public void onSuccess(List<AdEntity> list) {
                        App.getDaoSession().getAdEntityDao().insertOrReplaceInTx(list);
                    }
                });
    }


}
