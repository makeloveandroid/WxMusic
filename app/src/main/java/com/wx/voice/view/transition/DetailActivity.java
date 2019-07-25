package com.wx.voice.view.transition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import com.wx.voice.R;
import com.wx.voice.app.App;
import com.wx.voice.dao.AdEntityDao;
import com.wx.voice.down.DownloadAPI;
import com.wx.voice.down.DownloadProgressListener;
import com.wx.voice.entity.AdEntity;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.util.FileUtil;
import com.wx.voice.util.L;
import com.wx.voice.util.MD5Util;
import com.wx.voice.util.StringUtils;
import com.wx.voice.util.TextUtil;
import com.wx.voice.util.ToastUtil;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Field;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import me.jessyan.autosize.utils.LogUtils;

/**
 * Created by xmuSistone on 2016/9/19.
 */
public class DetailActivity extends FragmentActivity implements View.OnClickListener {

    public static final String EXTRA_ID = "EXTRA_ID";

    public static final String DOWN_TRANSITION_NAME = "downApp";
    public static final String INFO_TRANSITION_NAME = "info";
    public static final String DESC_TRANSITION_NAME = "desc";
    public static final String TUIJIAN_TRANSITION_NAME = "tuijian";
    public static final String RATINGBAR_TRANSITION_NAME = "ratingBar";
    public static final String TITLE_TRANSITION_NAME = "title";
    public static final String ICON_TRANSITION_NAME = "icon";
    public static final String COPY_TRANSITION_NAME = "copy";


    private RatingBar ratingBar;

    private LinearLayout listContainer;
    private View downApp;
    private View info;
    private TextView desc;
    private View tuijian;
    private ImageView icon;
    private TextView title;
    private AdEntity adEntity;
    private CompositeDisposable compositeDisposable;
    private WebView webView;
    private View positionView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        compositeDisposable = new CompositeDisposable();
        downApp = findViewById(R.id.downApp);
        info = findViewById(R.id.info);
        desc = findViewById(R.id.desc);
        title = findViewById(R.id.title);
        tuijian = findViewById(R.id.tuijian);
        ratingBar = findViewById(R.id.rating);
        icon = findViewById(R.id.icon);
        webView = findViewById(R.id.web_view);
        positionView = findViewById(R.id.position_view);
        View copy = findViewById(R.id.bt_copy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextUtil.copy(getApplicationContext(), adEntity.code);
                ToastUtil.getInstance().show(getApplicationContext(), "邀请码[" + adEntity.code + "],已经帮你粘贴到剪切板!");
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initTop();
        dealStatusBar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        ViewCompat.setTransitionName(downApp, DOWN_TRANSITION_NAME);
        ViewCompat.setTransitionName(info, INFO_TRANSITION_NAME);
        ViewCompat.setTransitionName(desc, DESC_TRANSITION_NAME);
        ViewCompat.setTransitionName(tuijian, TUIJIAN_TRANSITION_NAME);
        ViewCompat.setTransitionName(ratingBar, RATINGBAR_TRANSITION_NAME);
        ViewCompat.setTransitionName(icon, ICON_TRANSITION_NAME);
        ViewCompat.setTransitionName(copy, COPY_TRANSITION_NAME);
        initView();
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


    private void initView() {
        long id = getIntent().getLongExtra(EXTRA_ID, -1);
        if (id == -1) {
            ToastUtil.getInstance().show(getApplicationContext(), "数据出错!");
            finish();
            return;
        }
        adEntity = App.getDaoSession().getAdEntityDao().queryBuilder().where(AdEntityDao.Properties.Id.eq(id)).unique();
        if (adEntity == null) {
            ToastUtil.getInstance().show(getApplicationContext(), "数据出错!");
            finish();
            return;
        }
        ratingBar.setRating(adEntity.rating);
        desc.setText(adEntity.desc);
        title.setText(adEntity.title);

        initWebView();
        Glide.with(getApplicationContext()).load(adEntity.iconUrl).into(icon);
        downApp.setOnClickListener(this);
    }

    /**
     * webview初始化
     */
    private void initWebView() {

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        // 关键点
        webSettings.setUseWideViewPort(true);
        // 允许访问文件
        webSettings.setAllowFileAccess(true);
        // 支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        // 不加载缓存内容
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("wyz", "shouldOverrideUrlLoading");

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("wyz", "onPageStarted");
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("wyz", "onPageFinished");
                super.onPageFinished(view, url);
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//                int width = wm.getDefaultDisplay().getWidth();
//                int height = wm.getDefaultDisplay().getHeight();
//                //重新为WebView设置高度
//                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) webView.getLayoutParams();
//                params.width = width;
//                params.height = height;
//                webView.setLayoutParams(params);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d("wyz", "onReceivedError " + errorCode + " " + description + " " + failingUrl);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        if (!TextUtils.isEmpty(adEntity.detailUrl)) {
            webView.loadUrl(adEntity.detailUrl);
        }
    }


    private void dealListView() {
    }


    private void downApp() {
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setTitleText("下载中...");
        dialog.setContentText("感谢支持,你的支持是我更新的动力!\n邀请码[" + adEntity.code + "]\n已经帮你复制到剪切板,粘贴即可!");
        dialog.show();
        File file = new File(FileUtil.getSdWxPath(getApplicationContext()), MD5Util.getMd5(adEntity.apkUrl) + ".apk");
        TextUtil.copy(getApplicationContext(), adEntity.code);
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
                                dialog.getProgressHelper().setProgress(bytesRead * 1.0f / contentLength);
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
                            ToastUtil.getInstance().show(getApplicationContext(), "邀请码[" + adEntity.code + "],已经帮你粘贴到剪切板!");
                            FileUtil.installApk(getApplicationContext(), file.getAbsolutePath());
                        } else {
                            ToastUtil.getInstance().show(getApplicationContext(), "很遗憾下载错误!");
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.getInstance().show(getApplicationContext(), "很遗憾下载错误!");
                        dialog.dismiss();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.downApp:
                downApp();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
