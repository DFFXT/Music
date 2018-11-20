package com.web.data;

import android.support.annotation.IntDef;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class InternetMusic extends DataSupport implements Serializable {

    private int id;
    private String musicName;
    private String singerName;
    private String hash;
    private String sqHash;
    private String _320Hash;
    private String url="";
    //******本地保存位置
    private String path;
    private int duration;
    private int hasDownload;
    private int fullSize;
    private int _320FileSize;
    private int sqFileSize;

    public final static int TYPE_NORMAL=1;
    public final static int TYPE_HIGH=2;
    @IntDef({TYPE_NORMAL,TYPE_HIGH})
    @interface MusicQuality{ }
    private @MusicQuality int type=TYPE_NORMAL;

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSqHash() {
        return sqHash;
    }

    public void setSqHash(String sqHash) {
        this.sqHash = sqHash;
    }

    public String get_320Hash() {
        return _320Hash;
    }

    public void set_320Hash(String _320Hash) {
        this._320Hash = _320Hash;
    }

    public int get_320FileSize() {
        return _320FileSize;
    }

    public void set_320FileSize(int _320FileSize) {
        this._320FileSize = _320FileSize;
    }

    public int getSqFileSize() {
        return sqFileSize;
    }

    public void setSqFileSize(int sqFileSize) {
        this.sqFileSize = sqFileSize;
    }

    public @MusicQuality int getType() {
        return type;
    }

    public void setType(@MusicQuality int type) {
        this.type = type;
    }
}
