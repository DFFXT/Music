package com.web.data;

import com.web.common.base.MyApplication;
import com.web.common.constant.Constant;
import com.web.common.tool.MToast;
import com.web.config.GetFiles;
import com.web.config.Shortcut;
import com.web.moudle.music.player.MediaQuery;
import com.web.moudle.notification.MyNotification;
import com.web.web.R;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.Serializable;

import androidx.annotation.Nullable;

public class Music extends DataSupport implements Cloneable,Serializable {
    private int id;
    private String musicName;
    private String singer;
    private String path;
    private int groupId;
    private int duration;
    private int song_id;
    private int album_id;
    private String album;
    private String suffix;//** "mp3"无.
    private long size;
    @Deprecated
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
        return Constant.LocalConfig.krcPath+ File.separator+singer+" - "+musicName+".lrc";
    }
    public String getIconPath(){
        return Constant.LocalConfig.singerIconPath+ File.separator+singer+".png";
    }
    public Music copy(){
        try {
            return (Music) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean exist(Music music){
        File file=new File(music.path);
        return file.isFile()&&file.exists();
    }

    public boolean rename(String musicName,String singer){
        if(!Constant.LocalConfig.validFileName(musicName)||!Constant.LocalConfig.validFileName(singer)){
            MToast.showToast(MyApplication.context, R.string.invalidFilePath);
            return false;
        }
        if(!exist(this)){
            MToast.showToast(MyApplication.context, R.string.fileNotFound);
            return false;
        }
        File file=new File(path);
        File newFile=new File(Shortcut.createPath(musicName,singer,suffix));
        Music newMusic=this.copy();
        newMusic.setMusicName(musicName);
        newMusic.setSinger(singer);
        newMusic.setPath(newFile.getAbsolutePath());
        if(newFile.exists()){
            MToast.showToast(MyApplication.context, R.string.fileExist);
            return false;
        }

        boolean ok=file.renameTo(newFile);
        if(ok){
            MediaQuery.updateMusic(this,newMusic);
            setMusicName(musicName);
            setSinger(singer);
            setPath(newMusic.path);
            update(id);
        }else{
            MToast.showToast(MyApplication.context, R.string.renameFailed);
        }
        return ok;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }


    /**
     * 路径相同就是同一个music
     * @param obj music
     * @return bool
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Music){
            return path.equals(((Music) obj).path);
        }
        return false;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
