package com.web.moudle.home.mainFragment.subFragment.bean;

import java.util.List;

public class HomePageMusicInfo {
    private List<HomePageMusic> songlist;
    private int count;
    private int havemore;

    public List<HomePageMusic> getSonglist() {
        return songlist;
    }

    public void setSonglist(List<HomePageMusic> songlist) {
        this.songlist = songlist;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getHavemore() {
        return havemore;
    }

    public void setHavemore(int havemore) {
        this.havemore = havemore;
    }
}
