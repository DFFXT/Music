package com.web.data;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class InternetMusicForPlay extends Music implements Serializable {
    private String imgAddress;
    private String lrcLink;

    public InternetMusicForPlay(String musicName,String singer,String path){
        super(musicName,singer,path);
    }

    @Nullable
    public String getImgAddress() {
        return imgAddress.replace("{size}","240");
    }

    public void setImgAddress(String imgAddress) {
        this.imgAddress = imgAddress;
    }

    @Nullable
    public String getLrcLink() {
        return lrcLink;
    }

    public void setLrcLink(String lrcLink) {
        this.lrcLink = lrcLink;
    }

    public static RecentPlayMusic toRecentPlayMusic(InternetMusicForPlay music){
        RecentPlayMusic recentPlayMusic=new RecentPlayMusic();
        recentPlayMusic.setSongId(music.getSong_id());
        recentPlayMusic.setMusicName(music.getMusicName());
        recentPlayMusic.setArtist(music.getSinger());
        recentPlayMusic.setDuration(music.getDuration());
        recentPlayMusic.setAlbumName(music.getAlbum());
        recentPlayMusic.setAlbumId(music.getAlbum_id());

        //**可以尝试设置为本地路径
        recentPlayMusic.setImageLink(music.getImgAddress());
        recentPlayMusic.setLrcLink(music.getLrcLink());

        recentPlayMusic.setPath(music.getPath());
        return recentPlayMusic;
    }
}
