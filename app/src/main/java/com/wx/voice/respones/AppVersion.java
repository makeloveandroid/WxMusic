package com.wx.voice.respones;

public class AppVersion {
    private int versioncode;
    private String fileName;
    private String md5;
    private boolean forcibly;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isForcibly() {
        return forcibly;
    }

    public void setForcibly(boolean forcibly) {
        this.forcibly = forcibly;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
