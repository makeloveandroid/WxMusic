package com.nine.remotemm.util;

import android.util.Log;

import com.nine.remotemm.base.Constant;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Administrator on 2016/4/15 0015.
 */
public class L {
    public static String TAG = "------ WX_AUDIO ------";

    public static void i(String str) {
        if (Constant.CONFIG.DEBUG)
            Log.i(TAG, str);
    }

    public static void d(String str) {
        if (Constant.CONFIG.DEBUG)
            Log.d(TAG, str);
    }

    public static void v(String str) {
        if (Constant.CONFIG.DEBUG)
            Log.v(TAG, str);
    }

    public static void w(String str) {
        if (Constant.CONFIG.DEBUG)
            Log.w(TAG, str);
    }

    public static void e(String str) {
        if (Constant.CONFIG.DEBUG)
            Log.e(TAG, str);
    }

    public static void e(Exception e) {
        if (Constant.CONFIG.DEBUG) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            Log.e(TAG, stringWriter.toString());
        }
    }
}
