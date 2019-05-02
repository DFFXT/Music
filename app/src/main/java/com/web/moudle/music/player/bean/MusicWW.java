package com.web.moudle.music.player.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class MusicWW {
    @JSONField(name = "songId")
    private long songId;
    private String name;
    private String artist;
    private String album;

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
