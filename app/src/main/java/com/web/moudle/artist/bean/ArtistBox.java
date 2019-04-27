package com.web.moudle.artist.bean;

import java.util.List;

public class ArtistBox {
    private List<ArtistInfo> artist;
    private int havemore;
    private int nums;
    private String noFirstChar;

    public List<ArtistInfo> getArtist() {
        return artist;
    }

    public void setArtist(List<ArtistInfo> artist) {
        this.artist = artist;
    }

    public int getHavemore() {
        return havemore;
    }

    public void setHavemore(int havemore) {
        this.havemore = havemore;
    }

    public int getNums() {
        return nums;
    }

    public void setNums(int nums) {
        this.nums = nums;
    }

    public String getNoFirstChar() {
        return noFirstChar;
    }

    public void setNoFirstChar(String noFirstChar) {
        this.noFirstChar = noFirstChar;
    }
}
