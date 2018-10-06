package com.web.data;

import org.litepal.crud.DataSupport;

public class InternetMusic extends DataSupport{

    private int id;
    private String musicName;
    private String singerName;
    private String hash;
    private String url="";
    private String path;
    private int hasDownload;
    private int fullSize;

    public InternetMusic(){}
    public InternetMusic(String musicName, String singerName, String hash){
        this.musicName=musicName;
        this.singerName=singerName;
        this.hash=hash;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setFullSize(int fullSize) {
        this.fullSize = fullSize;
    }

    public void setHasDownload(int hasDownload) {
        this.hasDownload = hasDownload;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public String getMusicName() {
        return musicName;
    }

    public int getFullSize() {
        return fullSize;
    }

    public int getHasDownload() {
        return hasDownload;
    }

    public String getHash() {
        return hash;
    }

    public String getSingerName() {
        return singerName;
    }

    public String getPath() {
        return path;
    }
}
