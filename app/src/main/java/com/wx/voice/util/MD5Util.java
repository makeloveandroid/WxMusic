package com.wx.voice.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public MD5Util() {
    }

    public static final String MD5(byte[] var0) {
        char[] var1 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            MessageDigest var2 = MessageDigest.getInstance("MD5");
            var2.update(var0);
            byte[] var3 = var2.digest();
            int var4 = var3.length;
            char[] var5 = new char[var4 * 2];
            int var6 = 0;

            for (int var7 = 0; var7 < var4; ++var7) {
                byte var8 = var3[var7];
                var5[var6++] = var1[var8 >>> 4 & 15];
                var5[var6++] = var1[var8 & 15];
            }

            return new String(var5);
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    public static final String MD5(String var0) {
        char[] var1 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            byte[] var2 = var0.getBytes("utf-8");
            MessageDigest var3 = MessageDigest.getInstance("MD5");
            var3.update(var2);
            byte[] var4 = var3.digest();
            int var5 = var4.length;
            char[] var6 = new char[var5 * 2];
            int var7 = 0;

            for (int var8 = 0; var8 < var5; ++var8) {
                byte var9 = var4[var8];
                var6[var7++] = var1[var9 >>> 4 & 15];
                var6[var7++] = var1[var9 & 15];
            }

            return new String(var6);
        } catch (Exception var10) {
            var10.printStackTrace();
            return null;
        }
    }

    public static final String getMd5ByFile(File var0) {
        String var1 = null;
        FileInputStream var2 = null;

        try {
            var2 = new FileInputStream(var0);
            byte[] var16 = new byte[var2.available()];
            var2.read(var16);
            String var4 = MD5(var16);
            return var4;
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            try {
                var2.close();
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

        return var1;
    }

    public static String md5Digest(String var0) {
        try {
            MessageDigest var1 = MessageDigest.getInstance("MD5");
            var1.update(var0.getBytes());
            byte[] var2 = var1.digest();
            return byte2hex(var2);
        } catch (Exception var3) {
            System.out.println(var3.toString());
            return "";
        }
    }

    private static String byte2hex(byte[] var0) {
        String var1 = "";
        String var2 = "";

        for (int var3 = 0; var3 < var0.length; ++var3) {
            var2 = Integer.toHexString(var0[var3] & 255);
            if (var2.length() == 1) {
                var1 = var1 + "0" + var2;
            } else {
                var1 = var1 + var2;
            }
        }

        return var1.toLowerCase();
    }

    public static final String getMessageDigest(byte[] var0) {
        char[] var1 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            MessageDigest var2 = MessageDigest.getInstance("MD5");
            var2.update(var0);
            byte[] var3 = var2.digest();
            int var4 = var3.length;
            char[] var5 = new char[var4 * 2];
            int var6 = 0;

            for (int var7 = 0; var7 < var4; ++var7) {
                byte var8 = var3[var7];
                var5[var6++] = var1[var8 >>> 4 & 15];
                var5[var6++] = var1[var8 & 15];
            }

            return new String(var5);
        } catch (Exception var9) {
            return null;
        }
    }

    public static String getMd5(String var0) {
        try {
            MessageDigest var1 = MessageDigest.getInstance("MD5");
            var1.update(var0.getBytes());
            byte[] var2 = var1.digest();
            StringBuffer var4 = new StringBuffer("");

            for (int var5 = 0; var5 < var2.length; ++var5) {
                int var3 = var2[var5];
                if (var3 < 0) {
                    var3 += 256;
                }

                if (var3 < 16) {
                    var4.append("0");
                }

                var4.append(Integer.toHexString(var3));
            }

            return var4.toString();
        } catch (NoSuchAlgorithmException var6) {
            var6.printStackTrace();
            return null;
        }
    }
}
