package com.wx.voice.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

public class KCacheUtils {
    private static Context mContext;
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static void init(Context context) {
        mContext = context;
    }
    //获取一个可用的路径
    public static String getCacheDirectory() {
        String cachedDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission()) {
            cachedDir = mContext.getExternalFilesDir("cache")+"";
        }
        if (TextUtils.isEmpty(cachedDir)) {
            cachedDir = mContext.getCacheDir()+"";
        }
        if (TextUtils.isEmpty(cachedDir)) {
            cachedDir = mContext.getFilesDir().getParentFile().getPath() + mContext.getPackageName() + "/cache";
        }
        return cachedDir;
    }

    private static File getExternalCacheDir() {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, mContext.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission() {
        int perm = mContext.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
}
