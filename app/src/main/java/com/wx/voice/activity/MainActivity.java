package com.wx.voice.activity;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;


import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wx.voice.R;
import com.wx.voice.activity.contract.MainActivityContract;
import com.wx.voice.activity.presenter.MainActivityPresenter;
import com.wx.voice.app.App;
import com.wx.voice.base.view.base.XBaseActivity;
import com.wx.voice.down.DownloadAPI;
import com.wx.voice.down.DownloadProgressListener;
import com.wx.voice.entity.AdEntity;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.respones.AppVersion;
import com.wx.voice.util.FileUtil;
import com.wx.voice.util.L;
import com.wx.voice.util.MD5Util;
import com.wx.voice.util.StringUtils;
import com.wx.voice.util.ToastUtil;
import com.wx.voice.view.emptylayout.FrameEmptyLayout;
import com.wx.voice.view.transition.CommonFragment;
import com.wx.voice.view.transition.CustPagerTransformer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class MainActivity extends XBaseActivity<MainActivityPresenter> implements MainActivityContract.View {


    @BindView(R.id.indicator_tv)
    TextView indicatorTv;
    @BindView(R.id.frame_empty)
    FrameEmptyLayout emptyLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.position_view)
    View positionView;

    @BindView(R.id.btn_down_360)
    View btnDown360;

    // 供ViewPager使用
    private List<CommonFragment> fragments = new ArrayList<>();


    @Override
    public int layoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onInitCircle() {
        super.onInitCircle();
        RxPermissions rxPermissions = new RxPermissions(this);
        Disposable subscribe = rxPermissions
                .requestEach(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        L.d("permission:" + permission.name + " " + " " + permission.granted);
                        if (!permission.granted) {
                            ToastUtil.getInstance().show(getApplicationContext(), "请给应用权限,否则无法使用!");
                            finish();
                            return;
                        }
                        initView();
                    }
                });

        ArrayList<Integer> objects = new ArrayList<>();

        objects.add(1);
        objects.add(2);
        Observable.fromIterable(objects)
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
//                        if (integer == 2) {
//                            throw new IllegalArgumentException("出错");
//                        }
                        return Observable.just(integer);
                    }
                })
                .buffer(objects.size())
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Log.d("wyz", integers.size() + "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("wyz", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initView() {
        // 1. 沉浸式状态栏
        initTop();
        // 调整状态栏高度
        dealStatusBar();
        // 数据库中取数据
        presenter.loadData();

        emptyLayout.setRetryListener(new FrameEmptyLayout.OnRetryClickListener() {
            @Override
            public void onClick() {
                presenter.loadData();
            }
        });
        btnDown360.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                down360();
            }
        });

//        // todo 测试加入数据
//        AdEntity adEntity = new AdEntity();
//        adEntity.id = 0;
//        adEntity.adImage = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2653065978,1260798368&fm=26&gp=0.jpg";
//        adEntity.apkUrl = "http://wxz.myapp.com/16891/B2F4EAB9F73C2576CC908C6A7FA5660B.apk?fsname=com.jf.lkrj_3.7.4_30704.apk&hsr=4d5s";
//        adEntity.desc = "花生日记买东西好便宜!";
//        adEntity.detailUrl = "";
//        adEntity.code = "我是邀请码";
//        adEntity.rating = 4.5f;
//        adEntity.iconUrl = "http://static.yingyonghui.com/icon/128/6348870.png";
//        adEntity.title = "花生日记";
//        App.getDaoSession().getAdEntityDao().insertOrReplace(adEntity);
    }

    /**
     * 下载360分身大师
     */
    private void down360() {
        final String url = "http://shouji.360tpcdn.com/181018/c9bf7678751ce468ecc7b75b6b4baaac/com.qihoo.magic.xposed_18.apk";
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("免root使用xposed");
        dialog.setContentText("正在下载分身大师中...");
        dialog.show();
        Observable
                .create(new ObservableOnSubscribe<File>() {
                    @Override
                    public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                        File file = new File(FileUtil.getSdWxPath(getApplicationContext()), MD5Util.getMd5(url) + ".apk");

                        emitter.onNext(file);

                        emitter.onComplete();
                    }
                })
                .flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(final File file) throws Exception {

                        if (file.exists()) {
                            // install 安装
                            file.delete();
                        }

                        file.delete();
                        DownloadProgressListener listener = new DownloadProgressListener() {
                            @Override
                            public void update(long bytesRead, long contentLength, boolean done) {
                                L.d("下载进度:" + bytesRead + " " + contentLength + " " + done + " " + " " + (bytesRead / contentLength));
                                dialog.getProgressHelper().setProgress(bytesRead * 1.0f / contentLength);
                            }
                        };
                        String baseUrl = StringUtils.getHostName(url);
                        Observable<File> fileObservable = new DownloadAPI(baseUrl, listener).downloadAPK(url, file);
                        return fileObservable;

                    }
                })
                .compose(RxTransformerHelper.<File>schedulerTransf())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        presenter.addDisposable(d);
                    }

                    @Override
                    public void onNext(File file) {
                        if (file.exists()) {
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


    /**
     * 填充ViewPager
     */
    private void fillViewPager(final List<AdEntity> ads) {
        // 1. viewPager添加parallax效果，使用PageTransformer就足够了
        viewPager.setPageTransformer(false, new CustPagerTransformer(this));

        // 2. viewPager添加adapter
        for (int i = 0; i < 10; i++) {
            // 预先准备10个fragment
            fragments.add(new CommonFragment());
        }
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                CommonFragment fragment = fragments.get(position % 10);
                int value = ads.size() % 10;
                fragment.bindData(ads.get(value - 1));
                return fragment;
            }

            @Override
            public int getCount() {
                if (ads.size() < 10) {
                    return 10;
                }
                return ads.size();
            }
        });


        // 3. viewPager滑动时，调整指示器
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateIndicatorTv();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        updateIndicatorTv();
    }


    /**
     * 更新指示器
     */
    private void updateIndicatorTv() {
        int totalNum = viewPager.getAdapter().getCount();
        int currentItem = viewPager.getCurrentItem() + 1;
        L.d("totalNum" + totalNum + " " + currentItem);
        indicatorTv.setText(currentItem + "  /  " + totalNum);
    }


    private void initTop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                getWindow()
                        .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    /**
     * 调整沉浸式菜单的title
     */
    private void dealStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = getStatusBarHeight();
            ViewGroup.LayoutParams lp = positionView.getLayoutParams();
            lp.height = statusBarHeight;
            positionView.setLayoutParams(lp);
        }
    }


    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }


    @Override
    public void showLoading() {
        emptyLayout.showLoading();
    }

    @Override
    public void showError(int type, String msg) {
        emptyLayout.showError(R.mipmap.net_error, msg, "点击重试");
    }


    @Override
    public void showLoadData(String msg) {
        emptyLayout.showLoading();
    }

    @Override
    public void setAdList(List<AdEntity> ads) {
        emptyLayout.showContent();
        fillViewPager(ads);
    }
}
