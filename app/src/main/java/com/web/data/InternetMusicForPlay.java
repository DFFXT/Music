package com.web.data;

import android.support.annotation.Nullable;

import java.io.Serializable;

public class InternetMusicForPlay extends Music implements Serializable {
    private String imgAddress;
    private String lrcLink;

    @Nullable
    public String getImgAddress() {
        return imgAddress.replace("{size}","80");
    }

    public void setImgAddress(String imgAddress) {
        this.imgAddress = imgAddress;
    }

    @Nullable
    public String getLrcLink() {
        return lrcLink;
    }

    public void setLrcLink(String lrcLink) {
        this.lrcLink = lrcLink;
    }
}
