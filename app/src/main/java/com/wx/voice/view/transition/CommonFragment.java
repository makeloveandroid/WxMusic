package com.wx.voice.view.transition;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wx.voice.R;
import com.wx.voice.down.DownloadAPI;
import com.wx.voice.down.DownloadProgressListener;
import com.wx.voice.entity.AdEntity;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.observer.BaseObserver;
import com.wx.voice.util.FileUtil;
import com.wx.voice.util.L;
import com.wx.voice.util.MD5Util;
import com.wx.voice.util.StringUtils;
import com.wx.voice.util.TextUtil;
import com.wx.voice.util.ToastUtil;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;


/**
 * Created by xmuSistone on 2016/9/18.
 */
public class CommonFragment extends Fragment implements DragLayout.GotoDetailListener, View.OnClickListener {
    private ImageView imageView;
    private RatingBar ratingBar;
    private AdEntity adEntity;
    private View downApp;
    private View info;
    private TextView desc;
    private View tuijian;
    private TextView title;
    private ImageView icon;
    private CompositeDisposable compositeDisposable;
    private View copy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_common, null);
        DragLayout dragLayout = rootView.findViewById(R.id.drag_layout);
        imageView = dragLayout.findViewById(R.id.image);
        downApp = dragLayout.findViewById(R.id.downApp);
        info = dragLayout.findViewById(R.id.info);
        copy = dragLayout.findViewById(R.id.bt_copy);
        desc = dragLayout.findViewById(R.id.desc);
        title = dragLayout.findViewById(R.id.title);
        tuijian = dragLayout.findViewById(R.id.tuijian);
        ratingBar = dragLayout.findViewById(R.id.rating);
        icon = dragLayout.findViewById(R.id.icon);
        dragLayout.setGotoDetailListener(this);
        copy.setOnClickListener(this);
        compositeDisposable = new CompositeDisposable();
        initView();
        return rootView;
    }

    private void initView() {
        L.d(adEntity + "");
        ratingBar.setRating(adEntity.rating);
        desc.setText(adEntity.desc);
        title.setText(adEntity.title);
        Glide.with(getActivity()).load(adEntity.adImage).into(imageView);
        Glide.with(getActivity()).load(adEntity.iconUrl).into(icon);

        downApp.setOnClickListener(this);
    }

    @Override
    public void gotoDetail() {
        Activity activity = (Activity) getContext();
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair(downApp, DetailActivity.DOWN_TRANSITION_NAME),
                new Pair(info, DetailActivity.INFO_TRANSITION_NAME),
                new Pair(desc, DetailActivity.DESC_TRANSITION_NAME),
                new Pair(tuijian, DetailActivity.TUIJIAN_TRANSITION_NAME),
                new Pair(ratingBar, DetailActivity.RATINGBAR_TRANSITION_NAME),
                new Pair(title, DetailActivity.TITLE_TRANSITION_NAME),
                new Pair(icon, DetailActivity.ICON_TRANSITION_NAME),
                new Pair(copy, DetailActivity.COPY_TRANSITION_NAME)

        );
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_ID, adEntity.id);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public void bindData(AdEntity adEntity) {
        this.adEntity = adEntity;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.downApp:
                downApp();
                break;

            case R.id.bt_copy:
                TextUtil.copy(getContext(), adEntity.code);
                ToastUtil.getInstance().show(getContext(), "邀请码[" + adEntity.code + "],已经帮你粘贴到剪切板!");
                break;
            default:
                break;

        }
    }

    private void downApp() {
        TextUtil.copy(getContext(), adEntity.code);
        final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitleText("感谢");
        dialog.setContentText("感谢支持,你的支持是我更新的动力!\n邀请码[" + adEntity.code + "]\n已经帮你复制到剪切板,粘贴即可!");
        dialog.setConfirmText("下载");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                ToastUtil.getInstance().show(getContext(), "程序正在后台快速下载中,稍后自动安装...");
                //获取NotificationManager实例
                final NotificationManager notifyManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                final NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "down")
                        //设置小图标
                        .setSmallIcon(R.drawable.my_ic_launcher)
                        //设置通知标题
                        .setContentTitle(adEntity.title)
                        //设置通知内容
                        .setContentText(adEntity.desc);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channelID = "1";
                    String channelName = "down";
                    builder.setChannelId(channelID);
                    NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
                    notifyManager.createNotificationChannel(channel);
                }


                notifyManager.notify(1, builder.build());
                File file = new File(FileUtil.getSdWxPath(getActivity()), MD5Util.getMd5(adEntity.apkUrl) + ".apk");
                Observable.just(file)
                        .flatMap(new Function<File, ObservableSource<File>>() {
                            @Override
                            public ObservableSource<File> apply(File file) throws Exception {
                                if (file.exists()) {
                                    file.delete();
                                }

                                DownloadProgressListener listener = new DownloadProgressListener() {
                                    @Override
                                    public void update(long bytesRead, long contentLength, boolean done) {
                                        L.d("下载进度:" + bytesRead + " " + contentLength + " " + done + " " + " " + (bytesRead / contentLength));

                                    }
                                };
                                String url = adEntity.apkUrl;
                                String baseUrl = StringUtils.getHostName(url);
                                Observable<File> fileObservable = new DownloadAPI(baseUrl, listener).downloadAPK(url, file);
                                return fileObservable;
                            }
                        })
                        .compose(RxTransformerHelper.<File>schedulerTransf())
                        .subscribe(new Observer<File>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onNext(File file) {
                                if (file.exists()) {
                                    ToastUtil.getInstance().show(getContext(), "邀请码[" + adEntity.code + "],已经帮你粘贴到剪切板!");
                                    FileUtil.installApk(getContext(), file.getAbsolutePath());
                                } else {
                                    ToastUtil.getInstance().show(getContext(), "很遗憾下载错误!");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtil.getInstance().show(getContext(), "很遗憾下载错误!");
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
