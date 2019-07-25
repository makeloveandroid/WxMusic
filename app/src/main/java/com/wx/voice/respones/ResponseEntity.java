package com.wx.voice.respones;

public class ResponseEntity<T> {
    public int type;
    public String errorMsg;

    public ResponseEntity(T data) {
        this.type = 0;
        this.data = data;
    }

    public ResponseEntity(int type, String errorMsg) {
        this.type = type;
        this.errorMsg = errorMsg;
    }

    public T data;

}