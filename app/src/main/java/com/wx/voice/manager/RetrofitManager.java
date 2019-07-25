package com.wx.voice.manager;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.wx.voice.api.Api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private Retrofit client;

    private RetrofitManager() {
        if (SingletonHolder.instance != null) {
            throw new IllegalStateException();
        }
        initRetrofit();
    }

    private void initRetrofit() {
        client = new Retrofit
                .Builder()
//                .baseUrl("http://172.18.1.206:8080")
                .baseUrl("http://118.24.156.31:8080/mmvoice/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(initOkHttp())
                .build();
    }

    private OkHttpClient initOkHttp() {
        HttpLoggingInterceptor paramContext = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

            @Override
            public void log(String message) {

            }
        });

        return new OkHttpClient
                .Builder()
                .addInterceptor(paramContext)
                .addInterceptor(new StethoInterceptor())
                .connectTimeout(15L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .writeTimeout(60L, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();


    }

    private static class SingletonHolder {
        private static RetrofitManager instance = new RetrofitManager();
    }

    public static RetrofitManager getInstance() {
        return SingletonHolder.instance;
    }

    public Api createApi() {
        return client.create(Api.class);
    }
}
