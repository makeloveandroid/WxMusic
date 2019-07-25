package com.wx.voice.core;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;


import com.wx.voice.base.Constant;
import com.wx.voice.manager.RxTransformerHelper;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.request.VersionData;
import com.wx.voice.manager.RetrofitManager;
import com.wx.voice.respones.ResponseEntity;
import com.wx.voice.respones.Version;
import com.wx.voice.util.FileUtil;
import com.wx.voice.util.L;
import com.wx.voice.util.MD5Util;
import com.wx.voice.util.MyDialog;
import com.wx.voice.util.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.XposedHelpers;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2016/5/22.
 */
public class MMCore {

    private static MMCore mmCore = null;
    private static Object coreObj;

    public static MMCore getInstance(Activity activity) {
        if (mmCore == null) {
            synchronized (MMCore.class) {
                if (mmCore == null) {
                    mmCore = new MMCore();
                    //加载So
                    mmCore.loadJar(activity);
                    L.d("加载jar");
                }
            }
        }
        return mmCore;
    }


    public static MMCore getInstance() {
        return mmCore;
    }

    public static boolean isInit() {
        return coreObj != null;
    }

    public synchronized void deInt(Application application) {
        if (coreObj != null) {
            XposedHelpers.callMethod(coreObj, "deInit");
        }
    }


    public synchronized void addView(LinearLayout layout, String wxid) {
        if (coreObj != null) {
            XposedHelpers.callMethod(coreObj, "addView", layout, wxid);
        }
    }


    private static void addSo(Activity activity, String path) {
        try {
            FileInputStream inPut = new FileInputStream(path);
            File soFile = new File(activity.getCacheDir(), "libvoiccore.so");
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
                    // 拷贝出第二个so文件
                    addSo2(activity, path);
                    L.d("加载so成功:");

                }
            } catch (Exception e) {
                L.d("加载so出错:" + e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            L.d("加载so出错2:" + e);
        }

    }

    private static void addSo2(Activity activity, String path) {
        try {
            FileInputStream inPut = new FileInputStream(path);
            File soFile = new File(activity.getCacheDir(), "libvoice.so");
            if (soFile.exists()) {
                soFile.delete();
            }
            soFile.createNewFile();
            FileOutputStream soOut = new FileOutputStream(soFile);
            byte[] intValue = new byte[4];
            inPut.read(intValue);
            int soLength = byte2int(intValue);
            inPut.skip(soLength);

            //在读取第二个so
            inPut.read(intValue);
            soLength = byte2int(intValue);

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
        } catch (Exception e) {
            e.printStackTrace();
            L.d("加载so出错22:" + e);
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

    private synchronized void loadJar(final Activity activity) {
        downLoadDex(activity);
    }

    private void loadDex(final Activity activity, final String corePath, final Dialog dialog) {
        // 对下载文件进行解密
        Observable
                .create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                        String jarPath = copyBaseJarToFolder(activity.getApplication(), corePath);
                        // 这里考虑下放在线程中运行
                        if (jarPath != null && !TextUtils.isEmpty(jarPath)) {
                            // todo 注意下jni包,打包2个so文件会存在一定优化所以,2个so分开打,修改cmake
                            addSo(activity, jarPath);
                            if (new File(jarPath).exists()) {
                                List<Throwable> suppressedExceptions = new ArrayList<Throwable>();
                                File file = new File(activity.getApplicationContext().getFilesDir().getAbsolutePath() + "/cache/" + System.currentTimeMillis());

                                file.mkdirs();
                                L.d("开始加载了:");
                                // 完成加载对象
                                coreObj = load(activity.getApplicationContext(), jarPath, file, suppressedExceptions);

                                // 弹射
                                emitter.onNext(coreObj);
                                emitter.onComplete();
                                deleteFile(jarPath);
                            } else {
                                throw new RuntimeException("核心不存在,尝试重启微信!");
                            }
                        } else {
                            throw new RuntimeException("核心文件不存在,尝试重启微信!");
                        }

                    }
                })
                .compose(RxTransformerHelper.<Object>schedulerTransf())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object coreObj) {
                        Method init = null;
                        try {
                            init = coreObj.getClass().getMethod("init", Activity.class);
                            init.invoke(coreObj, activity);

                        } catch (Exception e) {
                            throw new RuntimeException("出错:" + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.getInstance().show(activity, "初始化失败:" + e.getMessage());
                        dialog.dismiss();

                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                    }
                });
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


    /**
     * 开始进行加载dex
     *
     * @param activity
     * @return
     */
    private void downLoadDex(final Activity activity) {
        PackageManager packageManager = activity.getApplication().getPackageManager();
        try {
            final PackageInfo packageInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
            final String outDir = FileUtil.getSdWxPath(activity);
            VersionData versionData = new VersionData(packageInfo.versionCode, packageInfo.versionName);
            final BaseRequest<VersionData> versionDataBaseRequest = new BaseRequest<>(activity.getApplicationContext(), "updata", "VERSION", "updata");
            versionDataBaseRequest.data = versionData;
            final Dialog dialog = showLoadDialog(activity);

            L.d("当前微信版本:" + versionData + " 执行网络");

            if (Constant.CONFIG.DEBUG) {
                // debug模式从资源中拷贝
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String path = outDir + packageInfo.versionName + ".xml";
                        new File(path).delete();
                        copyDebugCore(path, activity);
                        loadDex(activity, path, dialog);
                    }
                }).start();
                return;
            }

            // 非调试环境
            RetrofitManager
                    .getInstance()
                    .createApi()
                    .getVersion(versionDataBaseRequest)
                    .map(new Function<ResponseEntity<Version>, ResponseEntity<Version>>() {
                        @Override
                        public ResponseEntity<Version> apply(ResponseEntity<Version> versionResponseEntity) throws Exception {
                            if (versionResponseEntity.type == 0) {
                                // 判断文件是否存在
                                Version data = versionResponseEntity.data;
                                L.d("获取版本数据:" + data);
                                if (data != null && !TextUtils.isEmpty(data.getFilename()) && !TextUtils.isEmpty(data.getMd5())) {
                                    File file = new File(outDir, data.getMd5() + "_" + data.getFilename());
                                    L.d("当前文件数据:" + file.getAbsolutePath());
                                    if (file.exists()) {
                                        // 文件存在,比对md5是否相同
                                        String md5ByFile = MD5Util.getMd5ByFile(file);
                                        L.d("当前文件数据md5:" + md5ByFile);
                                        if (!TextUtils.equals(md5ByFile, data.getMd5())) {
                                            file.delete();
                                        }
                                    }
                                } else {
                                    throw new RuntimeException("初始化数据失败!");
                                }
                            } else {
                                throw new RuntimeException(versionResponseEntity.errorMsg);
                            }
                            return versionResponseEntity;
                        }
                    })
                    .compose(RxTransformerHelper.<ResponseEntity<Version>>schedulerTransf())
                    .subscribe(new Observer<ResponseEntity<Version>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseEntity<Version> versionResponseEntity) {
                            // 判断文件是否存在
                            Version data = versionResponseEntity.data;
                            File file = new File(outDir, data.getMd5() + "_" + data.getFilename());
                            if (file.exists()) {
                                // 文件存在
                                loadDex(activity, file.getAbsolutePath(), dialog);
                            } else {
                                dialog.setTitle("初始化进行通讯中...");
                                down(data, file, dialog, activity);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            MyDialog.showAlert("提示", e.getMessage(), true, activity);
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 正式下载dex
     *
     * @param file
     * @param dialog
     * @param activity
     */
    private void down(Version data, final File file, final Dialog dialog, final Activity activity) {
        RetrofitManager.getInstance().createApi()
                .download(data.getFilename())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }
                })
                .map(new Function<InputStream, Boolean>() {
                    @Override
                    public Boolean apply(InputStream inputStream) throws Exception {
                        file.createNewFile();
                        boolean b = FileUtil.copyFile(inputStream, new FileOutputStream(file));
                        return b;
                    }
                })
                .compose(RxTransformerHelper.<Boolean>schedulerTransf())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean flag) {
                        if (flag) {
                            loadDex(activity, file.getAbsolutePath(), dialog);
                        } else {
                            ToastUtil.getInstance().show(activity, "文件出错!");
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.getInstance().show(activity, "文件出错3:" + e.getMessage());
                        dialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private Dialog showLoadDialog(Activity activity) {
        ProgressDialog dialog = new ProgressDialog(activity);
        // 设置进度条的形式为圆形转动的进度条 
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置是否可以通过点击Back键取消  
        dialog.setCancelable(false);
        // 设置在点击Dialog外是否取消Dialog进度条 
        dialog.setCanceledOnTouchOutside(false);
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的  
        dialog.setTitle("正在初始化应用");
        dialog.show();
        return dialog;
    }

    /**
     * 从资源中拷贝
     *
     * @param path
     */
    private void copyDebugCore(String path, Activity activity) {
        String inPath = "/sdcard/docker/Android/data/com.tencent.mm/cache/mm.xml";
        File file = new File(inPath);
        L.d("当前加载md5:" + MD5Util.getMd5ByFile(file));
        if (file.exists()) {
            try {
                File outFile = new File(path);
                if (outFile.exists()) {
                    outFile.delete();
                }
                outFile.createNewFile();
                FileUtil.copyFile(new FileInputStream(file), new FileOutputStream(outFile));
            } catch (Exception e) {
                e.printStackTrace();
                L.d("拷贝调试文件出错:" + e.getLocalizedMessage());
            }
        } else {
            L.d("调试文件不存在:" + file.getAbsolutePath());
        }
    }

    public static String copyBaseJarToFolder(Application application, String inPath) throws IOException {
        String outPath = application.getCacheDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + ".xml";
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
