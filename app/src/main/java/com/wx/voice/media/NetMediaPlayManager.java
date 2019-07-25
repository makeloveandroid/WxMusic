package com.wx.voice.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.text.TextUtils;

import com.danikula.videocache.HttpProxyCacheServer;
import com.wx.voice.app.App;
import com.wx.voice.entity.MusicEntity;
import com.wx.voice.util.L;

import java.io.IOException;

/**
 * Created by wenyingzhi on 2018/12/12.
 */
public class NetMediaPlayManager implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {
    private ManagedMediaPlayer mMediaPlayer;
    private WifiManager.WifiLock wifiLock;
    private AudioFocusManager audioFocusManager;
    private HttpProxyCacheServer proxy;
    private MediaPlayer mediaPlayer;
    private MediaListener callBack;
    private MusicEntity currentMusicEntity;

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }


    private static class SingletonHolder {
        private static NetMediaPlayManager instance = new NetMediaPlayManager();
    }

    public static NetMediaPlayManager getInstance() {
        return SingletonHolder.instance;
    }


    private NetMediaPlayManager() {
        if (SingletonHolder.instance != null) {
            throw new IllegalStateException();
        }
    }

    /**
     * 播放器回调方法
     *
     * @param mp
     * @param percent
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (callBack != null) {
            callBack.onCompletion(currentMusicEntity);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        release();
        if (callBack != null) {
            callBack.onError(currentMusicEntity);
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // 准备完成,进行播放
        if (callBack != null) {
            callBack.onStart(currentMusicEntity);
        }
        start();
    }

    /**
     * 播放器私有操作方法
     */
    private void start() {
        // 获取音频焦点
        if (!audioFocusManager.requestAudioFocus()) {
            L.d("获取音频焦点失败");
        }
        mMediaPlayer.start();
        // 启用wifi锁
        wifiLock.acquire();
    }


    private void startPrepare(MusicEntity musicEntity) {
        try {
            String urlP = proxy.getProxyUrl(musicEntity.decoding_url);
            mMediaPlayer.setDataSource(urlP);
            mMediaPlayer.prepareAsync();
            if (currentMusicEntity != null) {
                currentMusicEntity.isPlay = false;
            }
            currentMusicEntity = musicEntity;
            musicEntity.isPlay = true;
        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    /**
     * 播放器公开操作方法,播放
     */
    public void play(MusicEntity musicEntity) {
        if (musicEntity == null || TextUtils.isEmpty(musicEntity.decoding_url)) {
            return;
        }
        if (mMediaPlayer == null) {
            init();
        }

        // 更新播放器状态
        mMediaPlayer.reset();
        startPrepare(musicEntity);
    }

    public void pause() {
        if (getStatus() == ManagedMediaPlayer.Status.STARTED) {
            mMediaPlayer.pause();
            // 关闭wifi锁
            if (wifiLock.isHeld()) {
                wifiLock.release();
            }

            // 取消音频焦点
            if (audioFocusManager != null) {
                audioFocusManager.abandonAudioFocus();
            }
        }
    }


    /**
     * 恢复
     */
    public void resume() {
        if (getStatus() == ManagedMediaPlayer.Status.PAUSED) {
            start();
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (getStatus() == ManagedMediaPlayer.Status.STARTED
                || getStatus() == ManagedMediaPlayer.Status.PAUSED
                || getStatus() == ManagedMediaPlayer.Status.COMPLETED || getStatus() == ManagedMediaPlayer.Status.INITIALIZED) {
            mMediaPlayer.stop();

            // 取消音频焦点
            if (audioFocusManager != null) {
                audioFocusManager.abandonAudioFocus();
            }
        }
        if (currentMusicEntity != null) {
            currentMusicEntity.isPlay = false;
            currentMusicEntity = null;
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
        // 取消音频焦点
        if (audioFocusManager != null) {
            audioFocusManager.abandonAudioFocus();
        }
        // 关闭wifi锁
        if (wifiLock.isHeld()) {
            wifiLock.release();
        }
        wifiLock = null;
        audioFocusManager = null;
        proxy = null;
    }

    /**
     * 移动到指定位置
     *
     * @param msec
     */
    public void seekTo(int msec) {
        if (getStatus() == ManagedMediaPlayer.Status.STARTED
                || getStatus() == ManagedMediaPlayer.Status.PAUSED
                || getStatus() == ManagedMediaPlayer.Status.COMPLETED) {
            mMediaPlayer.seekTo(msec);
        }
    }


    private void init() {
        mMediaPlayer = new ManagedMediaPlayer();
        // 使用唤醒锁
        mMediaPlayer.setWakeMode(App.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);
        // 初始化wifi锁
        wifiLock = ((WifiManager) App.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        // 初始化音频焦点管理器
        audioFocusManager = new AudioFocusManager(App.getContext());
        // 初始化AndroidVideoCache
        proxy = HttpProxyCacheUtil.getAudioProxy();
    }


    public ManagedMediaPlayer.Status getStatus() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getState();
        } else {
            return ManagedMediaPlayer.Status.STOPPED;
        }
    }


    public interface MediaListener {
        void onError(MusicEntity entity);

        void onStart(MusicEntity entity);

        void onCompletion(MusicEntity entity);
    }


    public void setCallBack(MediaListener callBack) {
        this.callBack = callBack;
    }
}
