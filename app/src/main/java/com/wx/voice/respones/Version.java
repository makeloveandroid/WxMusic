package com.wx.voice.respones;

public class Version {
    private int versioncode;
    private String filename;
    private String md5;

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "Version{" +
                "versioncode=" + versioncode +
                ", filename='" + filename + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
