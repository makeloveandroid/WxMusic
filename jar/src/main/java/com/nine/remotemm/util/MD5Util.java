package com.nine.remotemm.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String getFileMD5(String path) {
        String md5 = null;
        try {
            // 获取生成工具
            MessageDigest md = MessageDigest.getInstance("MD5");
            File file = new File(path);
            // 对每个字节数据组生成特征
            if (file.exists()) {
                // 汇总成最后一个值
                FileInputStream input = new FileInputStream(file);

                byte[] buffer = new byte[1024 * 10];
                int len = 0;
                while ((len = input.read(buffer)) != -1) {
                    md.update(buffer, 0, len);
                }
                byte[] bytes = md.digest();// 16
                // 1byte =8bit
                // 128bit
                // 32固定长定
                StringBuffer sb = new StringBuffer();
                for (byte item : bytes) {
                    // 去掉高位
                    String hex = Integer.toHexString(item & 0xff);
                    if (hex.length() == 2) {
                        sb.append(hex);
                    } else
                    // 补0
                    {
                        sb.append('0').append(hex);

                    }
                }
                md5 = sb.toString();
                input.close();
            }
        } catch (Exception e) {
            L.e(e);
        }
        return md5;
    }

    /**
     * 针字符串的生成特征值
     *
     * @param pwd
     * @return
     */
    public static String getStringMD5(String pwd) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] digestBs = digest.digest(pwd.getBytes());
            for (int i = 0; i < digestBs.length; i++) {
                // 1010 1000
                // 1111 1111 1111 1111 1111 1111 1010 1000
                // & 0000 0000 0000 0000 0000 0000 1111 1111
                // 0000 0000 0000 0000 0000 0000 1010 1000
                int b = digestBs[i] & 0xFF;
                String hexString = Integer.toHexString(b);
                if (hexString != null && hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
                // System.out.println(hexString);
            }
            // System.out.println(sb.toString());
            // System.out.println(sb.toString().length());
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            L.e(e);
        }
        return null;
    }
}
