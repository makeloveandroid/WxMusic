package com.wx.voice.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.baviux.voicechanger.services.FMODService;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wx.voice.R;
import com.wx.voice.activity.CoreDialog;
import com.wx.voice.adapter.ItemMusicInfoAdapter;
import com.wx.voice.app.App;
import com.wx.voice.base.view.base.v4.XBaseFragment;
import com.wx.voice.entity.MusicEntity;
import com.wx.voice.entity.MusicUserContent;
import com.wx.voice.entity.VoiceChangerEntity;
import com.wx.voice.fragment.contract.LocalMusicContract;
import com.wx.voice.fragment.presenter.LocalMusicPresenter;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.recorder.utils.Log;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.request.MusicListRequest;
import com.wx.voice.util.FileUtil;
import com.wx.voice.util.KCacheUtils;
import com.wx.voice.util.L;
import com.wx.voice.util.ToastUtil;
import com.wx.voice.util.Wav2Arm;
import com.wx.voice.view.emptylayout.FrameEmptyLayout;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * Created by wenyingzhi on 2018/12/21.
 */
public class LocalMusicFragment extends XBaseFragment<LocalMusicPresenter> implements LocalMusicContract.View, FMODService.ListenListener {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.emptyView)
    FrameEmptyLayout emptyView;
    private ItemMusicInfoAdapter itemMusicInfoAdapter;
    private String sendWxId;

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
        return R.layout.layout_music_type;
    }


    @Override
    public void onInitCircle() {
        super.onInitCircle();
        Intent intent = getActivity().getIntent();
        String nickName = intent.getStringExtra(CoreDialog.NICK_NAME);
        String myWxid = intent.getStringExtra(CoreDialog.MY_WXID);
        sendWxId = intent.getStringExtra(CoreDialog.SEND_WXID);

        emptyView.showContent();
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(App.getContext()));
        String data = FileUtil.loadJsonFromAssets(getContext(), "config.json");
        Gson gson = new Gson();
        List<VoiceChangerEntity> datas = gson.fromJson(data, new TypeToken<List<VoiceChangerEntity>>() {
        }.getType());
        ArrayList<MusicEntity> musics = new ArrayList<>();
        for (VoiceChangerEntity voiceChangerEntity : datas) {
            MusicEntity musicEntity = new MusicEntity();
            musicEntity.decoding_url = FileUtil.getLocalSavaMusicPath(getContext());
            musicEntity.title = voiceChangerEntity.name;
            musicEntity.type = voiceChangerEntity.type + "";
            musics.add(musicEntity);
        }
        itemMusicInfoAdapter = new ItemMusicInfoAdapter(musics);
        FMODService.getInstance().setCallBack(this);
        mRecyclerview.setAdapter(itemMusicInfoAdapter);

        itemMusicInfoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final BaseQuickAdapter adapter, View view, final int position) {
                if (new File(FileUtil.getLocalSavaMusicPath(getContext())).exists()) {
                    final MusicEntity data = (MusicEntity) adapter.getData().get(position);
                    data.isPlay = true;
                    FMODService.getInstance().playSound(data);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtil.getInstance().show(getContext(), "语音文件不存在,请重新录制!");
                }

            }

        });


        itemMusicInfoAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
                final MusicEntity data = (MusicEntity) adapter.getData().get(position);
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定发送?")
                        .setContentText("发送语音[" + data.title + "]")
                        .setConfirmText("确定")
                        .setCancelText("取消")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();

                                // 判断文件是否存在
                                if (new File(FileUtil.getLocalSavaMusicPath(getContext())).exists()) {
                                    sendFile(data);
                                } else {
                                    ToastUtil.getInstance().show(getContext(), "语音文件不存在,请重新录制!");
                                }
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();

            }
        });
    }

    /**
     * 发射文件
     */
    private void sendFile(final MusicEntity musicEntity) {
        Log.d("wyz", "1111!");
        FMODService.getInstance().end();
        Log.d("wyz", "2222");

        itemMusicInfoAdapter.notifyDataSetChanged();
        Log.d("wyz", "3333");
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setContentText("马不停蹄的发送找中...");
        sweetAlertDialog.setTitle("");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        Log.d("wyz", "4444");
        Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        Log.d("wyz", "55555");
                        String outPath = FileUtil.getLocalSavaMusicOutPath(getContext());
                        File file = new File(outPath);
                        if (file.exists()) {
                            file.delete();
                        }
                        Log.d("wyz", "66666");
//                FMODService.getInstance().create(FileUtil.getLocalSavaMusicPath(getContext()));
                        FMODService.getInstance().saveSound(FileUtil.getLocalSavaMusicPath(getContext()), file.getAbsolutePath(), musicEntity.type);
                        Log.d("wyz", "转码完成!");
                        emitter.onNext(outPath);
                        emitter.onComplete();
                    }
                })
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(final String s) throws Exception {
                        Observable<String> stringObservable = Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                String localSavaMusicAmrPath = FileUtil.getLocalSavaMusicAmrPath(getContext());
                                File file = new File(localSavaMusicAmrPath);
                                if (file.exists()) {
                                    file.delete();
                                }
                                Wav2Arm.getInstance().wav2amrNoCallBack(s, localSavaMusicAmrPath);
                                emitter.onNext(localSavaMusicAmrPath);
                                emitter.onComplete();
                            }
                        });
                        return stringObservable;
                    }
                })
                .map(new Function<String, Intent>() {


                    @Override
                    public Intent apply(String s) throws Exception {

                        //MediaExtractor, MediaFormat, MediaCodec
                        MediaExtractor mediaExtractor = new MediaExtractor();
                        long duration = 3000000;
                        //给媒体信息提取器设置源音频文件路径
                        try {
                            mediaExtractor.setDataSource(s);
                            //获取音频格式轨信息
                            MediaFormat mediaFormat = mediaExtractor.getTrackFormat(0);
                            duration = mediaFormat.containsKey(MediaFormat.KEY_DURATION) ? mediaFormat.getLong(
                                    MediaFormat.KEY_DURATION) : 3000000;
                        } catch (Exception ex) {
                            L.d("长度和偶去出错:" + ex.getMessage());

                        }
                        final int time = (int) (duration / 1000);


                        Intent intent = new Intent("mm.audio.tool.receiver");
                        intent.putExtra("ID", sendWxId);
                        intent.putExtra("IS_PAY", false);
                        intent.putExtra("PATH", s);
                        intent.putExtra("TIME", time);
                        L.d("发送路径:" + s);
                        return intent;
                    }
                })
                .compose(RxTransformerHelper.<Intent>schedulerTransf())
                .subscribe(new Observer<Intent>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        presenter.addDisposable(d);
                    }

                    @Override
                    public void onNext(Intent s) {
                        sweetAlertDialog.dismissWithAnimation();
                        App.getContext().sendBroadcast(s);
                        showSendSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.getInstance().show(sweetAlertDialog.getContext(), "发送出错:" + e.getMessage());
                        sweetAlertDialog.dismissWithAnimation();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void showSendSuccess() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog
                .setTitleText("发送成功")
                .setContentText("继续发送?")
                .setConfirmText("继续")
                .setCancelText("结束")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        getActivity().finish();
                    }
                })
                .show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isPrepared()) {
            if (!isVisibleToUser) {
                if (FMODService.getInstance().isPlay()) {
                    FMODService.getInstance().end();
                }
            } else {
                if (itemMusicInfoAdapter!=null){
                    itemMusicInfoAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onState(FMODService.MusicCallBacEntity entity, MusicEntity musicEntity) {
        if (!TextUtils.isEmpty(entity.errMsg)) {
            musicEntity.isPlay = false;
            itemMusicInfoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onOk(MusicEntity musicEntity) {
        musicEntity.isPlay = false;
        itemMusicInfoAdapter.notifyDataSetChanged();
    }
}
