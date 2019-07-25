package com.nine.remotemm.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2015/12/10.
 */
public class PhoneUtil {
    public static void createShortcut(Context context, Intent intent, Bitmap icon, String text, boolean duplicate) {
        Intent shortcutIntent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutIntent.putExtra("duplicate", duplicate);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, text);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shortcutIntent);
    }

    public static void deleteShortcut(Context context, Intent intent, String text) {
        Intent shortcutIntent = new Intent(
                "com.android.launcher.action.UNINSTALL_SHORTCUT");
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, text);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shortcutIntent);
    }

    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }

    public static String getUUID(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if (imei == null)
            imei = "000000000000000";
        String simSerialNumber = tm.getSimSerialNumber();
        if (simSerialNumber == null)
            simSerialNumber = "000000000000";
        String androidId = android.provider.Settings.Secure.getString(
                context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) imei.hashCode() << 32) | simSerialNumber.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    public static void showInstallDialog(Context context, String path) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(path));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static int getDpi(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) dm.densityDpi;
    }

    public static int getAndroidVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static String getIMEI(Context context) {

        String imei = null;
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null)
            imei = telephonyManager.getDeviceId();

        return imei;
    }

    @SuppressWarnings("deprecation")
    public static long getSDCardSize() {
        long size = 0;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                // long totalBlocks = stat.getBlockCount();
                long availableBlocks = stat.getAvailableBlocks();
                // KB
                size = (blockSize * availableBlocks) / 1024;
            } catch (Exception e) {
                // this can occur if the SD card is removed
                size = 0;
            }
        }
        return size;
    }

    @SuppressWarnings("deprecation")
    public static long getRomSize() {

        long size = 0;
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        size = (blockSize * availableBlocks) / 1024;
        return size;
    }

    public static String getWifiMac(Context context) {
        try {
            WifiManager wifi = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();

        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> getInstalledPackageName(Context context) {
        PackageManager p = context.getPackageManager();
        List<PackageInfo> packages = p.getInstalledPackages(0);
        List<String> arrayList = new ArrayList<String>();
        PackageInfo packageInfo;
        for (int i = 0; i < packages.size(); i++) {
            packageInfo = packages.get(i);
            arrayList.add(packageInfo.packageName);
        }
        return arrayList;
    }

    public static int isSystemApp(Context context) {
        int appState = -1;
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                    && (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                appState = 0; // 系统应用
            } else if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                    && (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                appState = 1; // 经过升级的系统应用
            } else {
                appState = 2; // 用户应用
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        return appState;
    }

    public static String getPhoneBrand() {
        return android.os.Build.BRAND;
    }

    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    public static String getIMSI(Context context) {
        String imsi = null;
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null)
            imsi = telephonyManager.getSubscriberId();

        return imsi;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    public static String getPackageVersionName(Context context, String packageName) {
        String versionName = "0";
        PackageInfo pkg = null;
        try {
            pkg = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = pkg.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
