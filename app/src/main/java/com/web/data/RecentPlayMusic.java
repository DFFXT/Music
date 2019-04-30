package com.web.data;


import org.litepal.crud.DataSupport;

/**
 * 用来保存网络播放记录
 */
public class RecentPlayMusic extends DataSupport {
    private String songId;
    private String musicName;
    private String artist;
    private int duration;
    private String path;
    private String lrcLink;
    private String imageLink;

    private String albumId;
    private String albumName;

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLrcLink() {
        return lrcLink;
    }

    public void setLrcLink(String lrcLink) {
        this.lrcLink = lrcLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void saveOrUpdate() {
        super.saveOrUpdate("songId=?",songId);
    }
}
