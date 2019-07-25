package com.wx.voice.xposed;



import com.wx.voice.core.HookCreate;
import com.wx.voice.core.HookHide;
import com.wx.voice.core.HookView;
import com.wx.voice.core.MMCore;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedInit implements IXposedHookLoadPackage {
    private static final String FILTER_PKGNAME = "com.tencent.mm";

    @Override
    public void handleLoadPackage(final LoadPackageParam loadPackageParam) {
        String pkgname = loadPackageParam.packageName;
        if (pkgname.equals("com.wx.audiotool")) {
            XposedHelpers.findAndHookMethod("com.wx.audiotool.MainActivity", loadPackageParam.classLoader,
                    "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }
        if (FILTER_PKGNAME.equals(pkgname)) {
            HookHide.hookMethod(loadPackageParam);
            HookCreate.hookMethod(loadPackageParam);
            HookView.hookMethod(loadPackageParam);
        }
    }

}
