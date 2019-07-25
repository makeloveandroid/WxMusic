package com.wx.voice.media;

import android.net.Uri;
import android.util.Log;

import com.danikula.videocache.file.FileNameGenerator;
import com.wx.voice.util.L;
import com.wx.voice.util.MD5Util;

import java.util.List;

public class CacheFileNameGenerator implements FileNameGenerator {

    private static final String TAG = "CacheFileNameGenerator";

    /**
     * @param url
     * @return
     */
    @Override
    public String generate(String url) {
        return MD5Util.getMd5(url);
    }
}