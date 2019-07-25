package com.wx.voice.entity;

import com.wx.voice.app.App;
import com.wx.voice.util.L;
import com.wx.voice.util.MD5Util;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

import java.io.File;

/**
 * Created by wenyingzhi on 2018/12/12.
 */
@Entity
public class DownFile {
    @Id(autoincrement = true)
    public long id;

    public String url;

    public String file;

    public long size;

    @Unique
    public String md5;

    @Generated(hash = 2132140395)
    public DownFile(long id, String url, String file, long size, String md5) {
        this.id = id;
        this.url = url;
        this.file = file;
        this.size = size;
        this.md5 = md5;
    }

    @Generated(hash = 1874366195)
    public DownFile() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public static DownFile make(String url) {
        DownFile downFile = new DownFile();
        downFile.url = url;
        downFile.md5 = MD5Util.getMd5(url);
        downFile.file = new File(App.getContext().getExternalCacheDir(), downFile.md5).getAbsolutePath();
        L.d("当前文件路径:" + downFile.file);
        return downFile;
    }


}
