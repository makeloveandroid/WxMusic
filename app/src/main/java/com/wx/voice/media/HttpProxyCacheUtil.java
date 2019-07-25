package com.wx.voice.media;

import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.wx.voice.app.App;
import com.wx.voice.util.L;

import java.io.File;

public class HttpProxyCacheUtil {

    private static HttpProxyCacheServer audioProxy;

    public static HttpProxyCacheServer getAudioProxy() {
        if (audioProxy== null) {
            audioProxy= new HttpProxyCacheServer.Builder(App.getContext())
                    .cacheDirectory(App.getContext().getExternalCacheDir())
                    // 缓存大小
                    .maxCacheSize(1024 * 1024 * 1024)
                    .fileNameGenerator(new CacheFileNameGenerator())
                    .build();
        }
        return audioProxy;
    }
}