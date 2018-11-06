package com.web.data;

public class PlayerConfig {
    //***播放模式
    private PlayType playType=PlayType.ALL_LOOP;
    private MusicOrigin musicOrigin=MusicOrigin.LOCAL;
    private Music music;//**播放器中缓存的音乐
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
        this.music = music;
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
