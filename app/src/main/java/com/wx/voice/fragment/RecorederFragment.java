package com.wx.voice.fragment;

import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wx.voice.R;
import com.wx.voice.activity.CoreDialog;
import com.wx.voice.base.view.base.v4.XBaseFragment;
import com.wx.voice.entity.MusicEntity;
import com.wx.voice.fragment.contract.RecorederContract;
import com.wx.voice.fragment.presenter.RecorederPresenter;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.media.LocalMediaPlayManager;
import com.wx.voice.media.NetMediaPlayManager;
import com.wx.voice.recorder.IdealRecorder;
import com.wx.voice.recorder.StatusListener;
import com.wx.voice.recorder.utils.Log;
import com.wx.voice.util.FileUtil;
import com.wx.voice.util.L;
import com.wx.voice.util.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * Created by wenyingzhi on 2018/12/20.
 */
public class RecorederFragment extends XBaseFragment<RecorederPresenter> implements RecorederContract.View {

    /**
     * -1 空闲状态
     * 1 录制状态
     * 2 暂停状态
     */
    private static int STATE = -1;
    /**
     * IdealRecorder的实例
     */
    private IdealRecorder idealRecorder;
    /**
     * Recorder的配置信息 采样率 采样位数
     */
    private IdealRecorder.RecordConfig recordConfig;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");


    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.timer)
    TextView mTimer;
    @BindView(R.id.restart)
    ImageButton mRestart;
    @BindView(R.id.record)
    ImageButton mRecord;
    @BindView(R.id.play)
    ImageButton mPlay;


    private Disposable timerOb;


    @Override
    public void method() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showError(int type, String msg) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public int layoutId() {
        return R.layout.fragment_audio_recorder;
    }

    @OnClick({R.id.status, R.id.timer, R.id.restart, R.id.record, R.id.play, R.id.content})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.status:
                break;
            case R.id.timer:
                break;
            case R.id.restart:
                use();
                break;
            case R.id.record:
                record();
                break;
            case R.id.play:
                play();
                break;
            case R.id.content:
                break;
        }
    }

    private void use() {
        if (new File(getSaveFilePath()).exists()) {
            CoreDialog activity = (CoreDialog) getActivity();
            activity.setCurrentItem(CoreDialog.LOCALMUSICFRAGMENT);
        } else {
            ToastUtil.getInstance().show(getContext(), "语音文件不存在,请重新录制!");
        }

    }

    private void play() {
        MusicEntity item = new MusicEntity();
        if (new File(getSaveFilePath()).exists()) {
            item.decoding_url = getSaveFilePath();
            LocalMediaPlayManager.getInstance().play(item);
        } else {
            ToastUtil.getInstance().show(getContext(), "语音文件不存在,请重新录制!");
        }
    }

    public void reset() {
        super.onLazyLoad();
        if (timerOb != null) {
            timerOb.dispose();
        }
        // 停止以前的播放
        LocalMediaPlayManager.getInstance().stop();

        // 停止录音
        idealRecorder.stop();
        STATE = -1;
        mRecord.setImageResource(R.mipmap.aar_ic_rec);
        mPlay.setVisibility(View.GONE);
        mRestart.setVisibility(View.GONE);
        mTimer.setText("00:00");
    }


    private void record() {
        if (STATE == -1) {

            //设置录音时各种状态的监听
            idealRecorder.setStatusListener(new StatusListener(){
                @Override
                public void onStartRecording() {
                    mStatus.setText("开始录音");
                }

                @Override
                public void onRecordData(short[] data, int length) {
                }

                @Override
                public void onVoiceVolume(int volume) {

                }

                @Override
                public void onRecordError(int code, String errorMsg) {
                    mStatus.setText("录音错误" + errorMsg);
                }

                @Override
                public void onFileSaveFailed(String error) {
                    ToastUtil.getInstance().show(getContext(), "文件保存失败");
                }

                @Override
                public void onFileSaveSuccess(final String fileUri) {
//            FMODService.getInstance().create(fileUri);//要播放保存 或 播放音效先调用这个  如果重新录制 调用 FMODService.getInstance().end(); 释放语音
                    if (timerOb != null && timerOb.isDisposed()) {
                        timerOb.dispose();
                    }
                    mPlay.setVisibility(View.VISIBLE);
                    mRestart.setVisibility(View.VISIBLE);

                }

                @Override
                public void onStopRecording() {
                    mStatus.setText("录音结束");
                }
            });

            mPlay.setVisibility(View.GONE);
            mRestart.setVisibility(View.GONE);

            // 停止以前的播放
            LocalMediaPlayManager.getInstance().stop();

            File file = new File(getSaveFilePath());
            if (file.exists()) {
                file.delete();
            }
            STATE = 1;
            mRecord.setImageResource(R.mipmap.aar_ic_stop);
            //开始录音
            idealRecorder.start();
            timerOb = Observable.interval(1, TimeUnit.SECONDS)
                    .compose(RxTransformerHelper.<Long>schedulerTransf())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            String format = simpleDateFormat.format(aLong * 1000);
                            mTimer.setText(format);
                        }
                    });
        } else if (STATE == 1) {
            //停止录音
            STATE = -1;
            mRecord.setImageResource(R.mipmap.aar_ic_rec);
            idealRecorder.stop();
            if (timerOb != null) {
                timerOb.dispose();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timerOb!=null){
            timerOb.dispose();
        }
    }


    @Override
    public void onInitCircle() {
        super.onInitCircle();
        idealRecorder = IdealRecorder.getInstance();

        recordConfig = new IdealRecorder.RecordConfig(MediaRecorder.AudioSource.MIC, IdealRecorder.RecordConfig.SAMPLE_RATE_16K_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        //如果需要保存录音文件  设置好保存路径就会自动保存  也可以通过onRecordData 回调自己保存  不设置 不会保存录音
        idealRecorder.setRecordFilePath(getSaveFilePath());
        //设置录音配置 最长录音时长 以及音量回调的时间间隔
        idealRecorder.setRecordConfig(recordConfig).setMaxRecordTime(60 * 10000).setVolumeInterval(200);

    }

    public String getSaveFilePath() {
        return FileUtil.getLocalSavaMusicPath(getContext());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isPrepared()) {
            reset();
        }
    }
}
