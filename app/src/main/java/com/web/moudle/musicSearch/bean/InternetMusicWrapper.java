package com.web.moudle.musicSearch.bean;

import android.support.annotation.IntDef;

import com.web.data.InternetMusic;

import java.util.ArrayList;
import java.util.List;

public class InternetMusicWrapper {
    private List<InternetMusic> list=new ArrayList<>();
    public final static int OK=1;
    public final static int ERROR=3;
    @IntDef({OK,ERROR})
    @interface CODE{}
    private @CODE int code=OK;

    public List<InternetMusic> getList() {
        return list;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
