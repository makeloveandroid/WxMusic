package com.wx.voice.decode;


import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.facebook.stetho.common.LogUtil;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.wx.voice.app.App;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.media.ManagedMediaPlayer;
import com.wx.voice.util.L;
import com.wx.voice.util.Wav2Arm;

import java.io.File;
import java.io.FileInputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * Created by wenyingzhi on 2018/12/18.
 */
public class DecodeUtil {
    private DecodeUtil() {
        if (SingletonHolder.instance != null) {
            throw new IllegalStateException();
        }
    }

    private static class SingletonHolder {
        private static DecodeUtil instance = new DecodeUtil();
    }

    public static DecodeUtil getInstance() {
        return SingletonHolder.instance;
    }

    public void mp3ToAmr(final File fileIn, final File outFile, final Wav2Arm.OnStateListener listener) {
        Observable
                .create(new ObservableOnSubscribe<File>() {
                    @Override
                    public void subscribe(final ObservableEmitter<File> emitter) throws Exception {
                        if (outFile.exists()) {
                            outFile.delete();
                        }
                        final File wavFile = new File(fileIn.getParent(), "tmp.wav");
                        if (wavFile.exists()) {
                            wavFile.delete();
                        }
                        String cmd[] = {"-i", fileIn.getAbsolutePath(), "-ar", "8000", "-ac", "1", wavFile.getAbsolutePath()};
                        FFmpeg
                                .getInstance(App.getContext())
                                .execute(cmd, new FFmpegExecuteResponseHandler() {
                                    @Override
                                    public void onSuccess(String message) {
                                        L.d("转码成功wav:" + message);
                                        emitter.onNext(wavFile);
                                        emitter.onComplete();
                                    }

                                    @Override
                                    public void onProgress(String message) {

                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        throw new RuntimeException("转wav失败!");
                                    }

                                    @Override
                                    public void onStart() {
                                    }

                                    @Override
                                    public void onFinish() {

                                    }
                                });
                    }
                })
                .flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(final File file) throws Exception {

                        if (outFile.exists()) {
                            outFile.delete();
                        }
                        return Observable.create(new ObservableOnSubscribe<File>() {
                            @Override
                            public void subscribe(final ObservableEmitter<File> emitter) throws Exception {
                                // 获取时间长度


                                Wav2Arm
                                        .getInstance()
                                        .wav2amr(file.getAbsolutePath(), outFile.getAbsolutePath(), new Wav2Arm.OnStateListener() {
                                            @Override
                                            public void onStart() {

                                            }

                                            @Override
                                            public void onError(String errMsg) {
                                                throw new RuntimeException("转amr失败:" + errMsg);

                                            }

                                            @Override
                                            public void onOk(String outPath) {
                                                emitter.onNext(outFile);
                                                emitter.onComplete();
                                            }
                                        });
                            }
                        });
                    }
                }).compose(RxTransformerHelper.<File>schedulerTransf())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        listener.onStart();
                    }

                    @Override
                    public void onNext(File file) {
                        listener.onOk(file.getAbsolutePath());

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onError("出错:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
