package com.wx.voice.down;

public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}