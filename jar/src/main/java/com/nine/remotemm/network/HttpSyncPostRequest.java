package com.nine.remotemm.network;


import org.core.http.RequestParams;
import org.core.x;

/**
 * Created by Administrator on 2016/5/16.
 */
public class HttpSyncPostRequest {
    private String url;
    private String bodyContent;

    public HttpSyncPostRequest(String url, String bodyContent) {
        this.url = url;
        this.bodyContent = bodyContent;
    }

    public String start() throws Throwable {
//        L.d("HttpSyncPostRequest 开始.");
//        L.d("   url: " + url);
//        L.d("   bodyContent: " + bodyContent);
        RequestParams params = new RequestParams(url);
        params.setBodyContent(bodyContent);
        return x.http().postSync(params, String.class);
    }
}
