package com.web.data;

import com.alibaba.fastjson.annotation.JSONField;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

import androidx.annotation.IntDef;

public class InternetMusic extends DataSupport implements Serializable {

    private int id;
    @JSONField(name = "title")
    private String musicName;
    @JSONField(name = "author")
    private String singerName;
    @JSONField(name = "song_id")
    private String hash;
    @JSONField(name = "album_id")
    private String albumId;
    @JSONField(name = "sqhash")
    private String sqHash;
    @JSONField(name = "320hash")
    private String _320Hash;
    private String url="";
    //******本地保存位置
    private String path;
    @JSONField(name = "duration")
    private int duration;
    private int hasDownload;
    @JSONField(name = "filesize")
    private int fullSize;
    @JSONField(name = "320filesize")
    private int _320FileSize;
    @JSONField(name = "sqfilesize")
    private int sqFileSize;
    @JSONField(name = "lrclink")
    private String lrcLink;

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

    public void setMusicName(String musicName) {//**baidu music title有标签
        this.musicName = musicName;
        this.musicName=this.musicName.replace("<em>","");
        this.musicName=this.musicName.replace("</em>","");
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
        this.singerName=this.singerName.replace("<em>","");
        this.singerName=this.singerName.replace("</em>","");
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

    public String getLrcLink() {
        return lrcLink;
    }

    public void setLrcLink(String lrcLink) {
        this.lrcLink = lrcLink;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
}
