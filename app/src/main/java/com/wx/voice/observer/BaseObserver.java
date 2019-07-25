package com.wx.voice.observer;

import com.wx.voice.base.presenters.XBasePresenter;
import com.wx.voice.respones.MusicTypeRespones;
import com.wx.voice.respones.ResponseEntity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by wenyingzhi on 2018/12/10.
 */
public abstract class BaseObserver<T> implements Observer<ResponseEntity<T>> {
    private final XBasePresenter presenter;

    public BaseObserver(XBasePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onSubscribe(Disposable d) {
        presenter.addDisposable(d);
    }

    @Override
    public void onNext(ResponseEntity<T> tResponseEntity) {
        if (tResponseEntity.type == 0) {
            onSuccess(tResponseEntity.data);
        } else {
            onNetError(tResponseEntity.type, tResponseEntity.errorMsg);
        }
    }

    public abstract void onNetError(int type, String errorMsg);

    public abstract void onSuccess(T t);

    @Override
    public void onError(Throwable e) {
        onNetError(-1, e.getMessage());
        onFinish();
    }

    @Override
    public void onComplete() {
        onFinish();
    }

    public void onFinish() {
    }
}
