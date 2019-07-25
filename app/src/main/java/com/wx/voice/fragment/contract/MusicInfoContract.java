package com.wx.voice.fragment.contract;

import com.wx.voice.base.contracts.XContract;
import com.wx.voice.entity.DownFile;
import com.wx.voice.entity.MusicEntity;
import com.wx.voice.entity.MusicUserContent;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.respones.ResponseEntity;

import java.io.File;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Url;

/**
 * Created by wenyingzhi on 2018/12/11.
 */
public interface MusicInfoContract {
    interface View extends XContract.View {
        BaseRequest getRequestData();

        void setData(List<MusicEntity> musicTypeRespones);

        void showToast(String msg);

        void setMoreData(List<MusicEntity> musicTypeRespones);

        void loadMoreOk();

        void loadMoreEnd();

        void showSendSuccess();
    }

    interface Presenter extends XContract.Presenter {
        void load();

        void loadMore();

        void downloadFile(SweetAlertDialog sweetAlertDialog, DownFile fileOut, String sendWxid);

        void send(SweetAlertDialog dialog, String wxid, File path);
    }

    interface Model extends XContract.Model {
        Observable<ResponseEntity<List<MusicEntity>>> requestData(BaseRequest request);

    }
}
