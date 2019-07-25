package com.nine.remotemm.network;

import android.os.Looper;

import com.nine.remotemm.util.L;


import org.core.common.Callback;
import org.core.http.RequestParams;
import org.core.x;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/5/16.
 */
public class HttpGetRequest {
    private String url;
    private HashMap<String, String> paramMap;
    private Callback.CommonCallback commonCallback;

    public HttpGetRequest(String url, HashMap<String, String> paramMap, Callback.CommonCallback commonCallback) {
        this.url = url;
        this.paramMap = paramMap;
        this.commonCallback = commonCallback;
    }

    private void doJob() {
        L.d("HttpGetRequest 开始: " + commonCallback.getClass().getSimpleName());
        L.d("   url: " + url);
        L.d("   paramMap: " + paramMap == null ? null : paramMap.toString());
        RequestParams params = new RequestParams(url);
        if (paramMap != null) {
            for (String key : paramMap.keySet()) {
                params.addParameter(key, paramMap.get(key));
            }
        }
        x.http().get(params, commonCallback);
    }

    public void start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                doJob();
            }
        });
        thread.start();
    }
}
