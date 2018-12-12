package com.web.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.web.subWeb.GetInfo;

import java.util.function.Consumer;

public class InternetMusicInfo extends Music{
    private String imgAddress;
    private String lrcLink;
    public InternetMusicInfo(@NonNull String hash){
        super("","","");
    }

    @Nullable
    public String getImgAddress() {
        return imgAddress.replace("{size}","80");
    }

    public void setImgAddress(String imgAddress) {
        this.imgAddress = imgAddress;
    }

    public String getLrcLink() {
        return lrcLink;
    }

    public void setLrcLink(String lrcLink) {
        this.lrcLink = lrcLink;
    }
}
