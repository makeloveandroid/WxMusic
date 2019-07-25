package com.wx.voice.core;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by Administrator on 2016/5/22.
 */
public class MMCore {
//    static {
//        System.loadLibrary("voiccore");
//    }

    private static String BASE_JAR_NAME = "mm.dex";
    private static String CLASS_NAME = "com.nine.remotemm.JarObject";

    private static MMCore mmCore = null;
    private Object coreObj;
    //private JarInterface jarInterface;

    public static MMCore getInstance(Activity activity) {
        if (mmCore == null) {
            synchronized (MMCore.class) {
                if (mmCore == null) {
                    mmCore = new MMCore();
                    //加载So
                    mmCore.loadJar(activity);
                }
            }
        }
        return mmCore;
    }

    public synchronized void deInt(Application application) {
        if (coreObj != null) {

        }

    }


    private static void addSo(Activity activity, String path) {
        try {
            FileInputStream inPut = new FileInputStream(path);
            File soFile = new File(activity.getFilesDir(), "libvoiccore.so");
            if (soFile.exists()) {
                soFile.delete();
            }
            soFile.createNewFile();
            FileOutputStream soOut = new FileOutputStream(soFile);
            byte[] intValue = new byte[4];
            inPut.read(intValue);
            int soLength = byte2int(intValue);
            byte[] b = new byte[1024];
            int i = -1;
            while ((i = inPut.read(b)) != -1) {
                if (i <= soLength) {
                    soOut.write(b, 0, i);
                    soLength = soLength - i;
                } else {
                    if (soLength > 0) {
                        soOut.write(b, 0, soLength);
                    }
                    break;
                }
            }
            inPut.close();
            soOut.close();
            try {
                if (soFile.exists()) {
                    System.load(soFile.getAbsolutePath());
                }
            } catch (Exception e) {
                Log.d("wyz", "加载so出错:" + e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static int byte2int(byte[] res) {
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00)
                | ((res[2] << 24) >>> 8) | (res[3] << 24);
        return targets;
    }

    public synchronized void reload(Activity activity) {
        loadJar(activity);
    }

    private synchronized void loadJar(Activity activity) {
        String jarPath = getJarPath(activity);
//        addSo(activity, jarPath);
        if (jarPath != null) {
            if (new File(jarPath).exists()) {
                try {
                    List<Throwable> suppressedExceptions = new ArrayList<Throwable>();
                    File file = new File(activity.getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
                    file.mkdirs();
                    Object dex = null;
                    coreObj = load(activity.getApplicationContext(), jarPath, file, suppressedExceptions);

                } catch (Exception e) {

                } finally {
                    deleteFile(jarPath);
                }

            } else {
                Log.d("wyz", "加载失败1");
            }
        }
    }

    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }

    private static Object getDexElements(Object paramObject)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        return getField(paramObject, paramObject.getClass(), "dexElements");
    }

    private static Object getField(Object obj, Class<?> cl, String field)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    private static Object getPathList(Object baseDexClassLoader)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    private static void setField(Object obj, Class<?> cl, String field,
                                 Object value) throws NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        localField.set(obj, value);
    }

    private static void inject(BaseDexClassLoader loader, Context ctx) {
        PathClassLoader pathLoader = (PathClassLoader) ctx.getClassLoader();
        try {
            Object dexElements = combineArray(
                    getDexElements(getPathList(pathLoader)),
                    getDexElements(getPathList(loader)));
            Object pathList = getPathList(pathLoader);
            setField(pathList, pathList.getClass(), "dexElements", dexElements);
        } catch (Exception e) {
            Log.i("multidex", "inject dexclassloader error:" + Log.getStackTraceString(e));
        }
    }


    private String getJarPath(Activity activity) {
        String path = "/sdcard/docker/Android/data/com.tencent.mm/cache/mm.xml";
        if (new File(path).exists()) {
            try {
                //这里到时候解密 拷贝
                return copyBaseJarToFolder(activity.getApplication(), path);
            } catch (IOException e) {
            }
        } else {
            return null;
        }
        return null;
    }

    public static String copyBaseJarToFolder(Application application, String inPath) throws IOException {
        String outPath = application.getCacheDir() + File.separator + System.currentTimeMillis() + ".xml";
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(inPath);
            out = new FileOutputStream(outPath);
            byte[] buffer = new byte[1024];
            int i = -1;
            while ((i = in.read(buffer)) > 0) {
                out.write(buffer, 0, i);
                out.flush();
            }
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outPath;
    }

    public void deleteDirectory(String path) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
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

    public void deleteFile(String path) {
        File file = new File(path);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }
    private native Object load(Context context, String jarPath, File file, List list);
}
