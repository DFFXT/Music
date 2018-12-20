package com.web.data;

import com.web.config.GetFiles;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.Serializable;

public class Music extends DataSupport implements Cloneable,Serializable {
    private int id;
    private String musicName;
    private String singer;
    private String path;
    private int groupId;
    private int duration;
    private int song_id;
    private int album_id;
    public Music(){}
    public Music(String musicName,String singer,String path){
        this.musicName=musicName;
        this.path=path;
        this.singer=singer;
    }

    public int getId() {
        return id;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getPath() {
        return path;
    }

    public String getSinger() {
        return singer;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }


    public String getLyricsPath(){
        return GetFiles.cachePath+"lyrics"+ File.separator+singer+" - "+musicName+".lrc";
    }
    public String getIconPath(){
        return GetFiles.cachePath+"singer"+ File.separator+singer+".png";
    }
    public Music copy(){
        try {
            return (Music) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除音乐,歌词
     * @param music music
     * @return Music
     */
    public static Music deleteMusic(Music music){
        File file=new File(music.getPath());
        file.delete();
        file=new File(music.getLyricsPath());
        file.delete();
        music.delete();
        return music;
    }

    public boolean saveOrUpdate(){
        return super.saveOrUpdate("path=?",path);
    }


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSong_id() {
        return song_id;
    }

    public void setSong_id(int song_id) {
        this.song_id = song_id;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }
}
