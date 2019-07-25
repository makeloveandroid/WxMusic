package com.wx.voice.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wx.voice.util.L;
import com.wx.voice.util.ToastUtil;

import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Administrator on 2018/1/23.
 */

public class HookView {

    public static void hookMethod(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        final ClassLoader classLoader = loadPackageParam.classLoader;
        XposedHelpers.findAndHookMethod("com.tencent.mm.pluginsdk.ui.chat.ChatFooter", classLoader, "setUserName", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                final LinearLayout layout = (LinearLayout) param.thisObject;
                String wxid = (String) param.args[0];
                if (MMCore.isInit()) {
                    MMCore.getInstance().addView(layout, wxid);
                }
            }
        });
    }


}
