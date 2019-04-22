package com.web.moudle.music.player.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.web.config.GetFiles;
import com.web.data.Music;

import java.io.File;
import java.io.FileInputStream;

public class PlayerConfig {
    //***播放模式
    private PlayType playType=PlayType.ALL_LOOP;
    private MusicOrigin musicOrigin=MusicOrigin.LOCAL;
    private Music music;//**播放器中缓存的音乐
    private Bitmap bitmap;//**音乐图片
    private boolean hasInit=false;
    private boolean prepared=false;

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
    public void setBitmapPath(String singerName){
        try {
            File file = new File(GetFiles.singerPath+singerName+".png");
            if(file.exists()&&file.isFile()){//---存在图片
                FileInputStream fis=new FileInputStream(file);
                bitmap= BitmapFactory.decodeStream(fis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPrepared() {
        return prepared;
    }

    public void setPrepared(boolean prepared) {
        this.prepared = prepared;
    }

    public enum PlayType{
        ONE_LOOP,
        ALL_LOOP,
        ALL_ONCE,
        ONE_ONCE,
        RANDOM,
    }
    public enum MusicOrigin{
        LOCAL,
        INTERNET,
        WAIT,
        STORAGE
    }
}
