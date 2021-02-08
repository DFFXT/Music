package com.web.data;

/**
 * 黑名单音乐
 */
public class IgnoreMusic extends Music{

    public IgnoreMusic(String musicName, String singer, String path) {
        super(musicName, singer, path);
        setPath(path);
    }

    @Override
    public synchronized int delete() {
        IgnoreMusic.deleteAll(IgnoreMusic.class,"path=\""+getPath()+"\"");
        return super.delete();
    }

    public static IgnoreMusic createIgnoreMusic(Music music){
        IgnoreMusic ig = new IgnoreMusic(music.getMusicName(),music.getSinger(),music.getPath());
        ig.setDuration(music.getDuration());
        return ig;
    }
    public static boolean isIgnoreMusic(Music music){
        return find(music.getPath()) != null;
    }
    public static IgnoreMusic find(String path){
        return IgnoreMusic.where("path=\""+path+"\"").findFirst(IgnoreMusic.class);
    }
}
