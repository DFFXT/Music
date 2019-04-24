package com.web.data;

import com.web.common.base.MyApplication;
import com.web.common.constant.Constant;
import com.web.common.tool.MToast;
import com.web.config.Shortcut;
import com.web.moudle.music.player.MediaQuery;
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
    private String song_id;
    private String album_id;
    private String album;
    private String suffix;//** "mp3"无.
    private long size;
    private boolean isLike;
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
        if(Constant.LocalConfig.isValidFileName(musicName) || Constant.LocalConfig.isValidFileName(singer)){
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

    public boolean saveOrUpdate(){
        return super.saveOrUpdate("path=?",path);
    }


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSong_id() {
        return song_id==null?"":song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
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
        if(suffix!=null){
            this.suffix = suffix;
        }else{
            this.suffix="mp3";
        }

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

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isLike() {
        return isLike;
    }
}
