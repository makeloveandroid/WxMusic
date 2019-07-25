package com.wx.voice.util;

import android.content.Context;
import android.widget.Toast;

import com.wx.voice.app.App;

/**
 * Created by wenyingzhi on 2018/12/10.
 */
public class ToastUtil {

    private Toast toast = null;

    private ToastUtil() {
        if (SingletonHolder.instance != null) {
            throw new IllegalStateException();
        }

    }

    private static class SingletonHolder {
        private static ToastUtil instance = new ToastUtil();
    }

    public static ToastUtil getInstance() {
        return SingletonHolder.instance;
    }

    public void show(Context context, String msg) {
        if (toast==null){
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }
}
