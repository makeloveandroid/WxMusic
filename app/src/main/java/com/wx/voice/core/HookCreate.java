package com.wx.voice.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;


import com.wx.voice.util.L;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static android.text.TextUtils.isEmpty;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class HookCreate {

    private static final String FILTER_PKGNAME = "com.tencent.mm";

    public static void hookMethod(final LoadPackageParam loadPackageParam) {
        String pkgname = loadPackageParam.packageName;
//        这里是为了解决app多dex进行hook的问题，Xposed默认是hook主dex
        XposedHelpers.findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final Activity activity = (Activity) param.thisObject;
                Intent intent = activity.getIntent();
                Bundle extras = intent.getExtras();
                if (extras == null) {
                    return;
                }
                for (String key : extras.keySet()) {
                    String string = key + " => " + extras.get(key) + ";";
                    Log.d("wyz", activity + ":" + string);
                }

                if (activity.getClass().getName().equals("com.tencent.mm.ui.LauncherUI")) {
                    boolean arg = (boolean) param.args[0];
                    if (arg) {
                        MessageQueue messageQueue = Looper.myQueue();
                        messageQueue.addIdleHandler(new MessageQueue.IdleHandler() {
                            @Override
                            public boolean queueIdle() {
                                MMCore.getInstance(activity);
                                return false;
                            }
                        });
                    }
                }
            }
        });
        XposedHelpers.findAndHookMethod(Activity.class, "onDestroy", new

                XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        if (TextUtils.equals(param.thisObject.getClass().getName(), "com.tencent.mm.ui.LauncherUI")) {
                            Activity activity = (Activity) param.thisObject;
                            //公开卸载
                            MMCore.getInstance(activity).deInt(activity.getApplication());
                        }
                    }
                });
        findAndHookMethod("com.tencent.mm.plugin.collect.reward.ui.QrRewardSelectMoneyUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                String qrcodeUrl = activity.getIntent().getStringExtra("key_qrcode_url");
                if (isEmpty(qrcodeUrl)) {
                    activity.getIntent().putExtra("key_qrcode_url", "m0kA4/Ls+/42JW(ac:FwjO");
                    return;
                }
            }
        });
    }


}
