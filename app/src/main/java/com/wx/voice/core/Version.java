package com.wx.voice.core;

/**
 * Created by wenyingzhi on 2018/12/17.
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.wx.voice.request.VersionData;
import com.wx.voice.util.FileUtil;
import com.wx.voice.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 版本控制工具类
 */
public class Version {
    public static final String CALL_MY_WXID = "CALL_MY_WXID",CALL_MY_MANE = "CALL_MY_MANE";
    private static Context mContext;
    public Map<String, CoreClassMethod> map;

    private Version() {
        if (SingletonHolder.instance != null) {
            throw new IllegalStateException();
        }
        map = new HashMap<>();
        initMap();
    }

    private void initMap() {
        if (mContext != null) {
            try {
                PackageManager packageManager = mContext.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
                int versionCode = packageInfo.versionCode;
                switch (versionCode) {
                    case 1360:
                        map.put(CALL_MY_WXID, new CoreClassMethod("com.tencent.mm.model.q", "Gj"));
                        map.put(CALL_MY_MANE, new CoreClassMethod("com.tencent.mm.model.q", "Gl"));
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public  CoreClassMethod getMethod(String key){
        return map.get(key);
    }
    private static class SingletonHolder {
        private static Version instance = new Version();
    }

    public static Version getInstance(Context context) {
        mContext = context;
        return SingletonHolder.instance;
    }



    public static class CoreClassMethod {
        public String className;
        public String method;

        public CoreClassMethod(String className, String method) {
            this.className = className;
            this.method = method;
        }
    }
}
