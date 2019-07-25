package com.wx.voice.util;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2015/12/31 0031.
 */
public class FileUtil {


    public static void decoderBase64File(String base64Code, String targetPath)
            throws Exception {
        byte[] buffer = Base64.decode(base64Code, Base64.NO_WRAP);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();

    }


    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static void deleteFile(String sPath) {
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static void deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return;
        }
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                deleteFile(files[i].getAbsolutePath());
            } //删除子目录
            else {
                deleteDirectory(files[i].getAbsolutePath());
            }
        }
        //删除当前目录
        dirFile.delete();
    }

    /**
     * 复制文件
     */
    public static boolean copyFile(InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[1024];
            int i = -1;
            while ((i = in.read(buffer)) > 0) {
                out.write(buffer, 0, i);
                out.flush();
            }
            return true;
        } catch (Exception e) {
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                L.e(e);
            }
        }
        return false;
    }

    public static void mkDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void copyAssetsFile(Context context, String outDir, String assetsName) {
        try {
            InputStream open = context.getAssets().open(assetsName);
            File file = new File(outDir);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            copyFile(open, out);
        } catch (IOException e) {
            L.e(e);
        }
    }


    public static String getSdWxPath(Context context) {
        String s = context.getExternalCacheDir().getAbsolutePath();
        File file = new File(s);
        if (!file.exists()) {
            file.mkdirs();
        }
        return s;
    }

    public static String getLocalSavaMusicPath(Context context) {
        return getSdWxPath(context) + "/tmp.wav";
    }

    public static String getLocalSavaMusicOutPath(Context context) {
        return getSdWxPath(context) + "/new_tmp.wav";
    }

    public static String getLocalSavaMusicAmrPath(Context context) {
        return getSdWxPath(context) + "/tmp.amr";
    }

    public static String loadJsonFromAssets(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        String result = "";
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            result = new String(buffer, "utf-8");
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static boolean installApk(Context context, String filePath) {
        File file = new File(filePath);
        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
                i.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            }
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        }
        return false;
    }

}
