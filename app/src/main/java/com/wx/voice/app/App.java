package com.wx.voice.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.facebook.stetho.Stetho;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.wx.voice.BuildConfig;
import com.wx.voice.dao.DaoMaster;
import com.wx.voice.dao.DaoSession;
import com.wx.voice.recorder.IdealRecorder;
import com.wx.voice.util.CrashHandler;
import com.wx.voice.util.L;



public class App extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private DaoMaster.DevOpenHelper helper;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private static DaoSession daoSession;
    private FFmpeg fFmpeg;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //  一般使用默认初始化配置足够使用
        initDb();
        CrashHandler.getInstance().init(getContext());
    }




    private void initDb() {

        // 下面代码仅仅需要执行一次，一般会放在application
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (BuildConfig.DEBUG){
                    Stetho.initializeWithDefaults(App.this);
                }
                IdealRecorder.init(App.this);

                helper = new DaoMaster.DevOpenHelper(App.this, "music-db", null);
                db = helper.getWritableDatabase();
                daoMaster = new DaoMaster(db);
                daoSession = daoMaster.newSession();
                fFmpeg = FFmpeg.getInstance(App.this);
                loadFFMpegBinary(fFmpeg);
            }
        }).start();

    }



    private void loadFFMpegBinary(FFmpeg fFmpeg) {
        try {
            fFmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccess() {
                    super.onSuccess();
                    L.d("初始化转码成功");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            L.d("初始化出错:" + e);
        }
    }



    public static DaoSession getDaoSession(){
        return daoSession;
    }
}
