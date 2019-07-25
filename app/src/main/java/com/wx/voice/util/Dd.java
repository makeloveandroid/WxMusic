
package com.wx.voice.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

//数据库加密类
public class Dd {
    private static String CLASS_NAME = "com.nine.remotemm.JarObject";
    private final Context context;

    public String CORE = "AABBCCDDEE112233";
    /**
     * 加密解密的key
     */
    private Key mKey;
    /**
     * 解密的密码
     */
    private Cipher mDecryptCipher;
    /**
     * 加密的密码
     */
    private Cipher mEncryptCipher;

    public Dd(Context context) throws Exception {
        this.context = context;
        initKey(CORE);
        initCipher();
    }

    /**
     * 创建一个加密解密的key
     *
     * @param keyRule
     */
    public void initKey(String keyRule) {
        byte[] keyByte = keyRule.getBytes();
        // 创建一个空的八位数组,默认情况下为0
        byte[] byteTemp = new byte[8];
        // 将用户指定的规则转换成八位数组
        init();
        byte[] keyBye = CORE.getBytes();
        for (int i = 0; i < byteTemp.length && i < keyBye.length; i++) {
            byteTemp[i] = keyBye[i];
        }
        mKey = new SecretKeySpec(byteTemp, "DES");
    }

    /***
     * 初始化加载密码
     *
     * @throws Exception
     */
    private void initCipher() throws Exception {
        mEncryptCipher = Cipher.getInstance("DES");
        mEncryptCipher.init(Cipher.ENCRYPT_MODE, mKey);

        mDecryptCipher = Cipher.getInstance("DES");
        mDecryptCipher.init(Cipher.DECRYPT_MODE, mKey);
    }

    /**
     * 加密文件
     *
     * @param in
     * @param savePath 加密后保存的位置
     */
    public void doEncryptFile(InputStream in, String savePath) {
        if (in == null) {
            System.out.println("inputstream is null");
            return;
        }
        try {
            CipherInputStream cin = new CipherInputStream(in, mEncryptCipher);
            OutputStream os = new FileOutputStream(savePath);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = cin.read(bytes)) > 0) {
                os.write(bytes, 0, len);
                os.flush();
            }
            os.close();
            cin.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密文件
     *
     * @param filePath 需要加密的文件路径
     * @param savePath 加密后保存的位置
     * @throws FileNotFoundException
     */
    public void doEncryptFile(String filePath, String savePath) throws FileNotFoundException {
        doEncryptFile(new FileInputStream(filePath), savePath);
    }

    /**
     * 解密文件
     *
     * @param in
     */
    public void dd(InputStream in, OutputStream out) {
        if (in == null) {
            return;
        }
        try {
            byte[] intValue = new byte[4];
            in.read(intValue);
            int soLength = byte2int(intValue);
            in.skip(soLength);

            //在读取第二个so
            in.read(intValue);
            soLength = byte2int(intValue);
            in.skip(soLength);


            CipherInputStream cin = new CipherInputStream(in, mDecryptCipher);
            byte[] b = new byte[1024];
            int i = -1;
            while ((i = cin.read(b)) != -1) {
                out.write(b, 0, i);
            }
            out.close();
            //--------------------------防止反编译
            boolean isStart = false;
            ObjectInputStream in2 = null;
            String path = "";
            try {
                if (isStart) {
                    in2 = new ObjectInputStream(
                            new FileInputStream(path));
                    HashMap map = (HashMap) in2.readObject();
                    PackageManager packageManager = context.getPackageManager();
                    String signatrue = "";
                    try {
                        PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
                        String hashCode = packageInfo.signatures[0].hashCode() + "";
                    } catch (Exception e) {
                        return;
                    }
                }
            } catch (Exception e) {
            } finally {
                try {
                    if (in != null)
                        in.close();

                } catch (IOException e) {
                }
            }
            //--------------------------防止反编译
            cin.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int byte2int(byte[] res) {
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00)
                | ((res[2] << 24) >>> 8) | (res[3] << 24);
        return targets;
    }


    /**
     * 解密文件
     *
     * @param filePath 文件路径
     * @throws Exception
     */
    public String dd(String filePath, String name) throws Exception {
        String unpackDir = context.getFilesDir().getAbsolutePath() + "/" + System.currentTimeMillis(); // 解压路径
        new File(unpackDir).mkdirs();
        //--------------------------防止反编译
        boolean isStart = false;
        ObjectInputStream in = null;
        String path = "";
        try {
            if (isStart) {
                in = new ObjectInputStream(
                        new FileInputStream(path));
                HashMap map = (HashMap) in.readObject();
                PackageManager packageManager = context.getPackageManager();
                String signatrue = "";
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
                    String hashCode = packageInfo.signatures[0].hashCode() + "";
                } catch (Exception e) {
                    return null;
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
            }
        }
        //--------------------------防止反编译
        String ss = unpackDir + "/" + name; // 解密jar的路径
        dd(new FileInputStream(filePath), new FileOutputStream(ss));
        return ss;
    }

    public native void init();
}