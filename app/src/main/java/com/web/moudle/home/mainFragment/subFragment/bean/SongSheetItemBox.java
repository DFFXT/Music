package com.web.moudle.home.mainFragment.subFragment.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class SongSheetItemBox {
    @JSONField(name = "diyInfo")
    private List<SongSheetItem> songSheetItems;
    private int havemore;
    private int error_code;
    private int nums;
    private String tag;

    public List<SongSheetItem> getSongSheetItems() {
        return songSheetItems==null? new ArrayList<>():songSheetItems;
    }

    public void setSongSheetItems(List<SongSheetItem> songSheetItems) {
        this.songSheetItems = songSheetItems;
    }

    public int getHavemore() {
        return havemore;
    }

    public void setHavemore(int havemore) {
        this.havemore = havemore;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public int getNums() {
        return nums;
    }

    public void setNums(int nums) {
        this.nums = nums;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
