package com.wx.voice.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wx.voice.BuildConfig;
import com.wx.voice.R;
import com.wx.voice.app.App;
import com.wx.voice.base.view.base.v4.XBaseFragment;
import com.wx.voice.down.DownloadAPI;
import com.wx.voice.down.DownloadProgressListener;
import com.wx.voice.fragment.LocalMusicFragment;
import com.wx.voice.fragment.MainFragment;
import com.wx.voice.fragment.MusicInfoListFragment;
import com.wx.voice.fragment.MusicListFragment;
import com.wx.voice.fragment.MusicTypeFrament;
import com.wx.voice.fragment.RecorederFragment;
import com.wx.voice.manager.RetrofitManager;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.media.NetMediaPlayManager;
import com.wx.voice.observer.BaseObserver;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.respones.AppVersion;
import com.wx.voice.respones.ResponseEntity;
import com.wx.voice.respones.Version;
import com.wx.voice.util.FileUtil;
import com.wx.voice.util.L;
import com.wx.voice.util.MD5Util;
import com.wx.voice.util.SPUtils;
import com.wx.voice.util.StringUtils;
import com.wx.voice.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;


/**
 * Created by wenyingzhi on 2018/12/7.
 */
public class CoreDialog extends AppCompatActivity {
    public static final String NICK_NAME = "NICK_NAME", MY_WXID = "MY_WXID", SEND_WXID = "SEND_WXID";

    protected static final TransitionConfig SLIDE_TRANSITION_CONFIG = new TransitionConfig(
            R.anim.slide_in_right, R.anim.slide_out_left,
            R.anim.slide_in_left, R.anim.slide_out_right);

    protected static final TransitionConfig SCALE_TRANSITION_CONFIG = new TransitionConfig(
            R.anim.scale_enter, R.anim.slide_still,
            R.anim.slide_still, R.anim.scale_exit);


    @BindView(R.id.content)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Stack<Integer> fragmentsStack = new Stack<>();

    private List<XBaseFragment> cache = new ArrayList<>();
    private CompositeDisposable compositeDisposable;
    private CompositeDisposable dialogCompositeDisposable;

    public static void startActivity(Context context, String wxid, String sendWxid, String nickName) {
        Intent intent = new Intent(context, CoreDialog.class);
        intent.putExtra(MY_WXID, wxid);
        intent.putExtra(SEND_WXID, sendWxid);
        intent.putExtra(NICK_NAME, nickName);
        context.startActivity(intent);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_music1);

        compositeDisposable = new CompositeDisposable();
        dialogCompositeDisposable = new CompositeDisposable();
        ButterKnife.bind(this);
        initToolBar();

        RxPermissions rxPermissions = new RxPermissions(this);
        Disposable subscribe = rxPermissions
                .requestEach(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (!permission.granted) {
                            ToastUtil.getInstance().show(getApplicationContext(), "请给应用权限,否则无法使用!");
                            finish();
                            return;
                        }
                        initView();
                    }
                });
        compositeDisposable.add(subscribe);
    }


    private void initToolBar() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            //设置返回键可用
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        toolbar.setSubtitle("很皮微信");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    // 对应
    public static final int MAINFRAGMENT = 0, RECOREDERFRAGMENT = 1, MUSICTYPEFRAMENT = 2, MUSICLISTFRAGMENT = 3, MUSICINFOLISTFRAGMENT = 4, LOCALMUSICFRAGMENT = 5;

    public void initView() {
        requestVersion();


        boolean isFirst = SPUtils.getInstance().getBoolean("IS_FIRST", true);
        if (isFirst) {
            SPUtils.getInstance().putBoolean("IS_FIRST", false);
            SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            dialog.setTitle("注意");
            dialog.setConfirmText("我知道了");
            dialog.setContentText("本软件游戏娱乐，请勿用于商业及非法用途，如产生法律纠纷与本人无关");
            dialog.show();
        }
        MainFragment mainFragment = new MainFragment();
        RecorederFragment recorederFragment = new RecorederFragment();
        MusicTypeFrament musicTypeFrament = new MusicTypeFrament();
        MusicListFragment musicListFragment = new MusicListFragment();
        MusicInfoListFragment musicInfoListFragment = new MusicInfoListFragment();
        LocalMusicFragment localInfoListFragment = new LocalMusicFragment();

        viewPager.setOffscreenPageLimit(5);
        cache.add(mainFragment);
        cache.add(recorederFragment);
        cache.add(musicTypeFrament);
        cache.add(musicListFragment);
        cache.add(musicInfoListFragment);
        cache.add(localInfoListFragment);

        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                // 第0页和第1页不用加载数据
                if (i > 2) {
                    cache.get(i).onLazyLoad();
                } else if (i == 1) {
                    getToolbar().setSubtitle("百变小音");
                } else if (i == 2) {
                    getToolbar().setSubtitle("很皮语音");
                } else {
                    getToolbar().setSubtitle("很皮微信");
                }
                NetMediaPlayManager.getInstance().stop();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void requestVersion() {
        RetrofitManager
                .getInstance()
                .createApi()
                .getAppVersion(new BaseRequest(getApplicationContext(), "version", "APP_VERSION", "version"))
                .compose(RxTransformerHelper.<ResponseEntity<AppVersion>>schedulerTransf())
                .subscribe(new Observer<ResponseEntity<AppVersion>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(final ResponseEntity<AppVersion> versionResponseEntity) {
                        if (versionResponseEntity.type == 0) {
                            if (versionResponseEntity.data != null) {
                                if (versionResponseEntity.data.getVersioncode() != BuildConfig.VERSION_CODE) {
                                    SweetAlertDialog dialog = new SweetAlertDialog(CoreDialog.this, SweetAlertDialog.WARNING_TYPE);
                                    dialog.setTitle("升级提醒");
                                    dialog.setContentText(versionResponseEntity.data.getMsg());

                                    if (versionResponseEntity.data.isForcibly()) {
                                        // 强制升级
                                        dialog.setCancelable(false);
                                        dialog.setConfirmText("升级")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        sweetAlertDialog.dismiss();
                                                        upApp(versionResponseEntity.data);
                                                    }


                                                });
                                        dialog.show();
                                    } else {
                                        // 建议升级
                                        dialog.setCanceledOnTouchOutside(true);
                                        dialog.setConfirmText("升级")
                                                .setCancelText("取消")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        sweetAlertDialog.dismiss();
                                                        upApp(versionResponseEntity.data);
                                                    }
                                                });
                                        dialog.show();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * app 升级
     */
    private void upApp(final AppVersion data) {
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setTitle("下载中...");
        dialog.setContentText("正在下载apk中...");
        dialog.show();
        Observable
                .create(new ObservableOnSubscribe<File>() {
                    @Override
                    public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                        File file = new File(FileUtil.getSdWxPath(getApplicationContext()), data.getMd5() + ".apk");

                        emitter.onNext(file);

                        emitter.onComplete();
                    }
                })
                .flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(final File file) throws Exception {

                        if (file.exists() && MD5Util.getMd5ByFile(file).equals(data.getMd5())) {
                            // install 安装
                            return Observable.just(file);
                        } else {
                            file.delete();
                            DownloadProgressListener listener = new DownloadProgressListener() {
                                @Override
                                public void update(long bytesRead, long contentLength, boolean done) {
                                    L.d("下载进度:" + bytesRead + " " + contentLength + " " + done + " " + " " + (bytesRead / contentLength));
                                    dialog.getProgressHelper().setProgress(bytesRead * 1.0f / contentLength);
                                }
                            };
                            String url = "http://118.24.156.31:8080/mmvoice/version/" + data.getFileName();
                            String baseUrl = StringUtils.getHostName(url);
                            Observable<File> fileObservable = new DownloadAPI(baseUrl, listener).downloadAPK(url, file);
                            return fileObservable;
/*
                            return RetrofitManager
                                    .getInstance()
                                    .createApi()
                                    .download(data.getFileName())
                                    .map(new Function<ResponseBody, File>() {
                                        @Override
                                        public File apply(ResponseBody responseBody) throws Exception {
                                            boolean b = FileUtil.copyFile(responseBody.byteStream(), new FileOutputStream(file));
                                            return file;
                                        }
                                    });*/
                        }
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
                        if (file.exists() && MD5Util.getMd5ByFile(file).equals(data.getMd5())) {
                            // install 安装
                            FileUtil.installApk(getApplicationContext(), file.getAbsolutePath());
                        } else {
                            ToastUtil.getInstance().show(getApplicationContext(), "安装文件下载出错");
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.getInstance().show(getApplicationContext(), "安装文件下载出错:" + e.getMessage());
                        dialog.dismiss();
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    public void setCurrentItem(int index) {

        fragmentsStack.push(viewPager.getCurrentItem());
        if (viewPager.getCurrentItem() + 1 == index) {
            viewPager.setCurrentItem(index);
        } else {
            viewPager.setCurrentItem(index, false);
        }
    }

    public XBaseFragment getFragment(int index) {
        return cache.get(index);
    }


    public static final class TransitionConfig {
        public final int enter;
        public final int exit;
        public final int popenter;
        public final int popout;

        public TransitionConfig(int enter, int popout) {
            this(enter, 0, 0, popout);
        }

        public TransitionConfig(int enter, int exit, int popenter, int popout) {
            this.enter = enter;
            this.exit = exit;
            this.popenter = popenter;
            this.popout = popout;
        }
    }

    public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return cache.get(i);
        }

        @Override
        public int getCount() {
            return cache.size();
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentsStack.size() == 0) {
            super.onBackPressed();
        } else {
            Integer pop = fragmentsStack.pop();

            if (viewPager.getCurrentItem() - 1 != pop) {
                viewPager.setCurrentItem(pop, false);
            } else {
                viewPager.setCurrentItem(pop);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.ali_icon:
                aPay();
                break;
            case R.id.wx_icon:
                wxPay();
                break;
            case R.id.mian_ze:
                mianZe();
                break;
            default:
                break;
        }
        return true;
    }

    private void mianZe() {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        dialog.setTitle("素材来源");
        dialog.setConfirmText("我知道了");
        dialog.setContentText("软件素材来源于网络,语音素材来源于[很皮语音],挺不错的软件,大家可以下载玩一玩!");
        dialog.show();
    }

    private void wxPay() {
        ToastUtil.getInstance().show(getApplicationContext(), "谢谢老板的大力支持,你的支持是我更新的动力!");
        Intent intent = new Intent("mm.audio.tool.receiver");
        intent.putExtra("IS_PAY", true);
        App.getContext().sendBroadcast(intent);
    }

    private void aPay() {
        ToastUtil.getInstance().show(getApplicationContext(), "谢谢老板的大力支持,你的支持是我更新的动力!");
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        String payUrl = "https://qr.alipay.com/fkx044578xo7lvx0ptwzd6a";
        intent.setData(Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + payUrl));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            intent.setData(Uri.parse(payUrl));
            startActivity(intent);
        }
    }


}
