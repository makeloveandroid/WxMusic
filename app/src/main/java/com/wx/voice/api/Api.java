package com.wx.voice.api;


import com.wx.voice.entity.AdEntity;
import com.wx.voice.entity.MusicEntity;
import com.wx.voice.entity.MusicUserContent;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.respones.AppVersion;
import com.wx.voice.respones.MusicTypeRespones;
import com.wx.voice.respones.ResponseEntity;
import com.wx.voice.respones.Version;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface Api {
    /**
     * 获取主页的头图
     *
     * @param paramMap
     * @return
     */
    @POST("core")
    Observable<ResponseEntity<List<MusicTypeRespones>>> getMusicType(@Body BaseRequest paramMap);


    /**
     * 根据type获取语音
     *
     * @param paramMap
     * @return
     */
    @POST("core")
    Observable<ResponseEntity<List<MusicUserContent>>> getMusicListByType(@Body BaseRequest paramMap);

    /**
     * 根据type获取语音最终数据
     *
     * @param paramMap
     * @return
     */
    @POST("core")
    Observable<ResponseEntity<List<MusicEntity>>> getMusicListInfo(@Body BaseRequest paramMap);


    /**
     * 获取版本号
     *
     * @param paramMap
     * @return
     */
    @POST("core")
    Observable<ResponseEntity<Version>> getVersion(@Body BaseRequest paramMap);


    /**
     * 获取App版本号
     *
     * @param paramMap
     * @return
     */
    @POST("core")
    Observable<ResponseEntity<AppVersion>> getAppVersion(@Body BaseRequest paramMap);


    /**
     * 获取所有广告数据
     *
     * @param paramMap
     * @return
     */
    @POST("core")
    Observable<ResponseEntity<List<AdEntity>>> getAds(@Body BaseRequest paramMap);


    @Streaming
    @GET("version/{fileName}")
    Observable<ResponseBody> download(@Path("fileName") String fileName);


    @Streaming
    @GET
    Observable<ResponseBody> downloadMusic(@Url String url);

}
