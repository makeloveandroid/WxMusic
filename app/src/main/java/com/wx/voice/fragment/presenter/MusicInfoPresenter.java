package com.wx.voice.fragment.presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.wx.voice.app.App;
import com.wx.voice.base.presenters.XBasePresenter;
import com.wx.voice.dao.DownFileDao;
import com.wx.voice.decode.DecodeUtil;
import com.wx.voice.down.DownloadAPI;
import com.wx.voice.down.DownloadProgressListener;
import com.wx.voice.entity.DownFile;
import com.wx.voice.entity.MusicEntity;
import com.wx.voice.entity.MusicUserContent;
import com.wx.voice.fragment.contract.MusicInfoContract;
import com.wx.voice.fragment.model.MusicInfoModel;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.observer.BaseObserver;
import com.wx.voice.respones.ResponseEntity;
import com.wx.voice.util.L;
import com.wx.voice.util.MD5Util;
import com.wx.voice.util.StringUtils;
import com.wx.voice.util.ToastUtil;
import com.wx.voice.util.Wav2Arm;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by wenyingzhi on 2018/12/11.
 */
public class MusicInfoPresenter extends XBasePresenter<MusicInfoContract.View, MusicInfoModel> implements MusicInfoContract.Presenter {
    @Override
    public void load() {
        view.showLoading();
        model
                .requestData(view.getRequestData())
                .compose(RxTransformerHelper.<ResponseEntity<List<MusicEntity>>>schedulerTransf())
                .subscribe(new BaseObserver<List<MusicEntity>>(this) {
                    @Override
                    public void onNetError(int type, String errorMsg) {
                        view.showError(type, errorMsg);
                    }

                    @Override
                    public void onSuccess(List<MusicEntity> musicUserContents) {
                        view.setData(musicUserContents);
                    }
                });
    }

    @Override
    public void loadMore() {
        model
                .requestData(view.getRequestData())
                .compose(RxTransformerHelper.<ResponseEntity<List<MusicEntity>>>schedulerTransf())
                .subscribe(new BaseObserver<List<MusicEntity>>(this) {
                    @Override
                    public void onNetError(int type, String errorMsg) {
                        if (type == 4) {
                            view.loadMoreEnd();
                        } else {
                            view.loadMoreOk();
                            view.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onSuccess(List<MusicEntity> musicUserContents) {
                        view.setMoreData(musicUserContents);
                        view.loadMoreOk();
                    }
                });
    }

    @Override
    public void downloadFile(final SweetAlertDialog sweetAlertDialog, final DownFile downFile, final String sendWxid) {
        DownloadProgressListener listener = new DownloadProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                L.d("下载进度:" + bytesRead + " " + contentLength + " " + done);
                downFile.size = contentLength;
//                sweetAlertDialog.getProgressHelper().setProgress(bytesRead / contentLength);

            }
        };
        final File outputFile = new File(downFile.file);
        String baseUrl = StringUtils.getHostName(downFile.url);

        new DownloadAPI(baseUrl, listener).downloadAPK(downFile.url, outputFile, new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.getInstance().show(sweetAlertDialog.getContext(),"下载文件出错!");
                sweetAlertDialog.dismissWithAnimation();
            }

            @Override
            public void onComplete() {
                App.getDaoSession().getDownFileDao().insertOrReplace(downFile);
                send(sweetAlertDialog, sendWxid, outputFile);
            }
        });
    }

    @Override
    public void send(final SweetAlertDialog dialog, final String wxid, final File path) {

        //MediaExtractor, MediaFormat, MediaCodec
        MediaExtractor mediaExtractor = new MediaExtractor();
        long duration = 3000000;
        //给媒体信息提取器设置源音频文件路径
        try {
            mediaExtractor.setDataSource(path.getAbsolutePath());
            //获取音频格式轨信息
            MediaFormat mediaFormat = mediaExtractor.getTrackFormat(0);
            duration = mediaFormat.containsKey(MediaFormat.KEY_DURATION) ? mediaFormat.getLong(
                    MediaFormat.KEY_DURATION) : 3000000;
        } catch (Exception ex) {
            L.d("长度和偶去出错:" + ex.getMessage());

        }
        final int time = (int) (duration / 1000);
        // 转码
        dialog.setConfirmText("正在转码");
        File out = new File(path.getParent(), "tmp.amr");

        DecodeUtil.getInstance().mp3ToAmr(path, out, new Wav2Arm.OnStateListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onError(String errMsg) {
                dialog.dismissWithAnimation();
                ToastUtil.getInstance().show(dialog.getContext(),"文件转码出错:" + errMsg);

            }

            @Override
            public void onOk(String outPath) {
                dialog.dismissWithAnimation();
                Intent intent = new Intent("mm.audio.tool.receiver");
                intent.putExtra("ID", wxid);
                intent.putExtra("IS_PAY", false);
                intent.putExtra("PATH", outPath);
                intent.putExtra("TIME", time);
                L.d("发送路径:" + outPath);
                App.getContext().sendBroadcast(intent);
                view.showSendSuccess();
            }
        });


    }
}
