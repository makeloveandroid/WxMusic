package com.wx.voice.request;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wx.voice.app.App;

/**
 * Created by wenyingzhi on 2018/12/7.
 */
public class BaseRequest<T> {

    /**
     * model : {"imei":"imei","model":"XiaoMi"}
     * nickName : 哈哈
     * type : AllType
     * wxId : ROOT
     */

    private ModelBean model;
    @SerializedName("nickName")
    private String nickname;
    private String type;
    @SerializedName("wxId")
    private String wxid;
    public T data;

    public static BaseRequest objectFromData(String str) {

        return new Gson().fromJson(str, BaseRequest.class);
    }

    public BaseRequest(Context context, String nickname, String type, String wxid) {
        model = new ModelBean(context);
        this.nickname = nickname;
        this.type = type;
        this.wxid = wxid;
    }

    public ModelBean getModel() {
        return model;
    }

    public void setModel(ModelBean model) {
        this.model = model;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWxid() {
        return wxid;
    }

    public void setWxid(String wxid) {
        this.wxid = wxid;
    }

    public static class ModelBean {
        /**
         * imei : imei
         * model : XiaoMi
         */

        private String imei;
        private String model;

        public ModelBean(Context context) {
            this.imei = getIMEI(context);
            this.model = android.os.Build.BRAND;
        }
    }


    /**
     * 获取手机IMEI号
     * <p>
     * 需要动态权限: android.permission.READ_PHONE_STATE
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
        return imei;
    }

}
