package com.wx.voice.fragment.contract;

import com.wx.voice.base.contracts.XContract;
import com.wx.voice.entity.MusicUserContent;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.respones.MusicTypeRespones;
import com.wx.voice.respones.ResponseEntity;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by wenyingzhi on 2018/12/10.
 */
public interface MusicListContract {
    interface View extends XContract.View {
        BaseRequest getRequestData();

        void setData(List<MusicUserContent> musicTypeRespones);

        void showToast(String msg);

        void setMoreData(List<MusicUserContent> musicTypeRespones);

        void loadMoreOk();

        void loadMoreEnd();
    }

    interface Presenter extends XContract.Presenter {
        void load();

        void loadMore();
    }

    interface Model extends XContract.Model {
        Observable<ResponseEntity<List<MusicUserContent>>> requestData(BaseRequest request);
    }
}
