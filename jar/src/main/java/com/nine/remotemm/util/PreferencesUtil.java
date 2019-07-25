package com.nine.remotemm.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/4/25 0025.
 */
public class PreferencesUtil {

    private static final String PREFERENCE_FILE = "preference.xml";

    public static String getStringValue(Context context, String key) {
        SharedPreferences data = context.getSharedPreferences(PREFERENCE_FILE, context.MODE_PRIVATE);
        String string = data.getString(key, "");
        return string;
    }

    public static void setStringValue(Context context, String key, String value) {
        SharedPreferences data = context.getSharedPreferences(PREFERENCE_FILE, context.MODE_PRIVATE);
        SharedPreferences.Editor edit = data.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static long getLongValue(Context context, String key) {
        SharedPreferences data = context.getSharedPreferences(PREFERENCE_FILE, context.MODE_PRIVATE);
        return data.getLong(key, 0L);
    }

    public static void setLongValue(Context context, String key, long value) {
        SharedPreferences data = context.getSharedPreferences(PREFERENCE_FILE, context.MODE_PRIVATE);
        SharedPreferences.Editor edit = data.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public static int getIntValue(Context context, String key) {
        SharedPreferences data = context.getSharedPreferences(PREFERENCE_FILE, context.MODE_PRIVATE);
        return data.getInt(key, 0);
    }

    public static void setIntValue(Context context, String key, int value) {
        SharedPreferences data = context.getSharedPreferences(PREFERENCE_FILE, context.MODE_PRIVATE);
        SharedPreferences.Editor edit = data.edit();
        edit.putInt(key, value);
        edit.commit();
    }
}
