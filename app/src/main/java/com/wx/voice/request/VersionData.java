package com.wx.voice.request;

public class VersionData {
    public long version;
    public String versionName;

    public VersionData(long version, String versionName) {
        this.version = version;
        this.versionName = versionName;
    }

    @Override
    public String toString() {
        return "VersionData{" +
                "version=" + version +
                ", versionName='" + versionName + '\'' +
                '}';
    }
}
