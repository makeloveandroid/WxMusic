package com.nine.remotemm.network;

import android.os.Looper;

import com.nine.remotemm.util.L;

import org.core.common.Callback;
import org.core.http.RequestParams;
import org.core.x;


/**
 * Created by Administrator on 2016/5/9.
 */
public class HttpPostRequest {

    private String url;
    private String bodyContent;
    private Callback.CommonCallback commonCallback;

    public HttpPostRequest(String url, String bodyContent, Callback.CommonCallback commonCallback) {
        this.url = url;
        this.bodyContent = bodyContent;
        this.commonCallback = commonCallback;
    }

    private void doJob() {
        L.d("HttpPostRequest 开始: " + commonCallback.getClass().getSimpleName());
        L.d("   url: " + url);
        L.d("   bodyContent: " + bodyContent);
        RequestParams params = new RequestParams(url);
        params.setBodyContent(bodyContent);
        x.http().post(params, commonCallback);
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
