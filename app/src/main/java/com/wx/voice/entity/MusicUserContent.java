package com.wx.voice.entity;




public class MusicUserContent {
    /**
     * unid : 103632979
     * count : 37
     * isneedfx : no
     * adate : 1519833600
     * label :
     * isnew : no
     * title : 温柔女声
     * click : 2310743
     * nickname : 狂少_一祥
     * id : 10
     * newimg : https://qiniu.henpi.vip/music_type/newimg/10.jpg
     * status : 0
     * info :
     * plcount : 157
     * fxdata : {"shareurl":"http://www.52sec.net/wap/event/luodiye/index.html?unid=103764192&typeid=235","title":"抱抱抱抱好吗","url":"https://qiniu.henpi.vip/henpi/music/20180608/0.13119600152844538443183235.mp3"}
     */

    private int count;
    private String info;
    private int plcount;
    private String newimg;
    private String nickname;
    private int id;
    private String title;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getPlcount() {
        return plcount;
    }

    public void setPlcount(int plcount) {
        this.plcount = plcount;
    }

    public String getNewimg() {
        return newimg;
    }

    public void setNewimg(String newimg) {
        this.newimg = newimg;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}