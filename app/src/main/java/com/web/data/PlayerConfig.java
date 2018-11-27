package com.web.data;

import android.graphics.Bitmap;

public class PlayerConfig {
    //***播放模式
    private PlayType playType=PlayType.ALL_LOOP;
    private MusicOrigin musicOrigin=MusicOrigin.LOCAL;
    private Music music;//**播放器中缓存的音乐
    private Bitmap bitmap;//**音乐图片
    private boolean hasInit=false;


    public void setPlayType(PlayType playType) {
        this.playType = playType;
    }

    public PlayType getPlayType() {
        return playType;
    }

    public void setMusicOrigin(MusicOrigin musicOrigin) {
        this.musicOrigin = musicOrigin;
    }

    public MusicOrigin getMusicOrigin() {
        return musicOrigin;
    }

    public void setMusic(Music music) {
        if(this.music!=music){
            this.music = music;
            bitmap=null;
        }

    }

    public Music getMusic() {
        return music;
    }

    public boolean isHasInit() {
        return hasInit;
    }

    public void setHasInit(boolean hasInit) {
        this.hasInit = hasInit;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    public enum PlayType{
        ONE_LOOP,
        ALL_LOOP,
        ALL_ONCE,
        ONE_ONCE
    }
    public enum MusicOrigin{
        LOCAL,
        INTERNET,
        WAIT,
        STORAGE
    }
}
