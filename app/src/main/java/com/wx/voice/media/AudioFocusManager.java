package com.wx.voice.media;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import static android.content.Context.AUDIO_SERVICE;

public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener{

    private static final String TAG = "AudioFocusManager";
    private AudioManager audioManager;
    private boolean isPausedByFocusLossTransient;

    public AudioFocusManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    public boolean requestAudioFocus() {
        return audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            // 重新获得焦点
            case AudioManager.AUDIOFOCUS_GAIN:
                if (isPausedByFocusLossTransient) {
                    // 通话结束，恢复播放
                    NetMediaPlayManager.getInstance().resume();
                }
                // 恢复音量
                NetMediaPlayManager.getInstance().getMediaPlayer().setVolume(1f, 1f);
                isPausedByFocusLossTransient = false;
                Log.d(TAG, "重新获得焦点");
                break;
            // 永久丢失焦点，如被其他播放器抢占
            case AudioManager.AUDIOFOCUS_LOSS:
                NetMediaPlayManager.getInstance().stop();
                abandonAudioFocus();
                Log.d(TAG, "永久丢失焦点，如被其他播放器抢占");
                break;
            // 短暂丢失焦点，如来电
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                NetMediaPlayManager.getInstance().pause();
                isPausedByFocusLossTransient = true;
                Log.d(TAG, "短暂丢失焦点，如来电");
                break;
            // 瞬间丢失焦点，如通知
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // 音量减小为一半
                NetMediaPlayManager.getInstance().getMediaPlayer().setVolume(0.5f, 0.5f);
                Log.d(TAG, "瞬间丢失焦点，如通知");
                break;
            default:
                break;
        }

    }
}
