package com.web.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.web.subWeb.GetInfo;

import java.util.function.Consumer;

public class InternetMusicInfo extends Music{
    private String hash;
    private String imgAddress;

    public InternetMusicInfo(@NonNull String hash){
        super("","","");
        this.hash=hash;
    }

    public String getHash() {
        return hash;
    }

    @Nullable
    public String getImgAddress() {
        return imgAddress.replace("{size}","80");
    }

    public void setImgAddress(String imgAddress) {
        this.imgAddress = imgAddress;
    }

}
