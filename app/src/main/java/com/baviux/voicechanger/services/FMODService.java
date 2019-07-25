package com.baviux.voicechanger.services;

import android.os.SystemClock;

import com.wx.voice.entity.MusicEntity;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.util.FileUtil;
import com.wx.voice.util.L;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class FMODService {
    public MusicEntity currentItem;

    private ListenListener listener;

    private static class SingletonHolder {
        private static FMODService instance = new FMODService();
    }


    private FMODService() {

    }

    public static FMODService getInstance() {
        return SingletonHolder.instance;
    }

    static {
        System.loadLibrary("fmod");
        System.loadLibrary("fmod_wrapper");
    }

    public static class MusicCallBacEntity {
        public int count;
        public int position;
        public String errMsg;
    }

    public interface ListenListener {
        void onState(MusicCallBacEntity entity, MusicEntity musicEntity);

        void onOk(MusicEntity musicEntity);
    }


    //这个播放只用调用一次就好 只对一个音乐做音效处理
    public void create(String str) {
        cBegin(0, 0);
        cCreateSound(str);
    }


    public boolean isPlay() {
        return cIsPlaying();
    }

    public void pause(boolean flag) {
        cPause(flag);
    }


    //这里可以用 AxJava 写一下
    public void playSound(final MusicEntity musicEntity) {
        FMODService.getInstance().end();
        currentItem = musicEntity;
        Observable
                .create(new ObservableOnSubscribe<MusicCallBacEntity>() {

                    @Override
                    public void subscribe(ObservableEmitter<MusicCallBacEntity> emitter) throws Exception {
                        String mode = musicEntity.type;

                        //要播放保存 或 播放音效先调用这个  如果重新录制 调用 FMODService.getInstance().end(); 释放语音
                        FMODService.getInstance().create(musicEntity.decoding_url);
                        cPrepareForPlay(getValue1(Integer.parseInt(mode)), getVlue2(Integer.parseInt(mode)));
                        cRemoveEffects(16000);
                        cAddEffect(Integer.parseInt(mode), 16000);
                        cPlayPrepared();
                        int i = cGetSoundLength();
                        MusicCallBacEntity musicCallBacEntity = new MusicCallBacEntity();
                        musicCallBacEntity.count = i;
                        while (true) {
                            L.d("当前播放:" + musicEntity.title + " " + musicEntity.isPlay);
                            if (!musicEntity.isPlay) {
                                break;
                            }
                            int value = cGetPosition();
                            musicCallBacEntity.position = value;
                            emitter.onNext(musicCallBacEntity);
                            if (value == i) {
                                break;
                            }
                            SystemClock.sleep(200);
                        }
                        emitter.onComplete();
                    }
                })
                .compose(RxTransformerHelper.<MusicCallBacEntity>schedulerTransf())
                .subscribe(new Observer<MusicCallBacEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MusicCallBacEntity integer) {
                        listener.onState(integer, musicEntity);
                        if (integer.position == integer.count) {
                            listener.onOk(musicEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        MusicCallBacEntity musicCallBacEntity = new MusicCallBacEntity();
                        musicCallBacEntity.errMsg = "播放出错:" + e.getMessage();
                        listener.onState(musicCallBacEntity, musicEntity);

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void setCallBack(final ListenListener listener) {
        this.listener = listener;
    }

    private int getVlue2(int arg3) {
        int v0;
        switch (arg3) {
            case 700: {
                v0 = 1;
                break;
            }
            default: {
                v0 = 0;
                break;
            }
        }

        return v0;
    }


    private int getValue1(int arg3) {
        int v0;
        switch (arg3) {
            case 4000:
            case 4100:
            case 4200:
            case 4400: {
                v0 = 2;
                break;
            }
            case 4300:
            case 4500:
            case 4600: {
                v0 = 3;
                break;
            }
            default: {
                v0 = 1;
                break;
            }
        }

        return v0;
    }

    public interface SaveListener {
        void onOk(String outPath);
    }

    //这里可以用 AxJava 写一下
    public void saveSound(String str, String out, String mode) {
        cBegin(0, 0);

        cSetFileOutput(out, 8000);
        cCreateSound(str);

        cPrepareForPlay(getValue1(Integer.parseInt(mode)), getVlue2(Integer.parseInt(mode)));
        cRemoveEffects(16000);
        cAddEffect(Integer.parseInt(mode), 16000);

        cPlayPrepared();
        while (true) {
            cUpdate();
            boolean b = cIsPlaying();
            if (!b) {
                break;
            }
        }
        cSetSpeakerOutput();
    }

    public void end() {
        if (currentItem != null) {
            currentItem.isPlay = false;
        }
        cEnd();
    }

    private native void cAddEffect(int i, int i2);  //添加音效

    private native void cBegin(int i, int i2); // 开始

    private native boolean cCreateSound(String str);

    private native boolean cCreateStream(String str);

    private native void cEnd(); //结束

    private native int cGetDSPBufferSize();

    private native int cGetDSPNumBuffers();

    private native int cGetPosition(); //得到播放位置

    private native float cGetSoundFrequency(); //得到播放频率

    private native int cGetSoundLength();//得到播放总仓度

    private native boolean cIsPlaying();// 是否在播放

    private native void cPause(boolean z); //播放暂停 开启

    private native void cPlayPrepared();//最备好播放

    private native void cPrepareForPlay(int i, int i2);

    private native void cRemoveEffects(int i); //删除音效

    private native void cSetEchoDelay(int i, float f);

    private native void cSetFileOutput(String str, int i);//设置输出路径

    private native void cSetFrequency(float f);

    private native void cSetPitchShift(float f);

    private native void cSetPosition(int i);

    private native void cSetSpeakerOutput();

    private native void cSetVolume(float f);

    private native void cUpdate();

}