package com.wx.voice.manager;

import android.app.Activity;

import java.util.ArrayList;

public class ActivityManager {

    private final ArrayList<Activity> activities;

    private ActivityManager() {
        if (SingletonHolder.instance != null) {
            throw new IllegalStateException();
        }

        activities = new ArrayList<>();
    }

    private static class SingletonHolder {
        private static ActivityManager instance = new ActivityManager();
    }

    public static ActivityManager getInstance() {
        return SingletonHolder.instance;
    }


    /**
     * 加入activity
     *
     * @param activity
     */
    public void put(Activity activity) {
        if (activities.contains(activity)) {
            activities.add(activity);
        }
    }

    /**
     * 销毁activity
     * @param activity
     */
    public void pop(Activity activity) {
        if (activities.contains(activity)) {
            activities.remove(activity);
        }
    }

    /**
     * 安全的退出app
     */
    public void close(){
        for (Activity activity : activities) {
            activity.finish();
        }
    }
}
