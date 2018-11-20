package com.web.moudle.musicDownload.bean;

import android.support.annotation.IntDef;

import com.web.data.InternetMusic;

public class DownloadMusic {
    public final static int DOWNLOAD_WAIT=1;
    public final static int DOWNLOAD_PAUSE=2;
    public final static int DOWNLOAD_DOWNLOADING=3;
    public final static int DOWNLOAD_DELETE=4;
    @IntDef({DOWNLOAD_WAIT,DOWNLOAD_PAUSE,DOWNLOAD_DOWNLOADING,DOWNLOAD_DELETE})
    @interface DownloadStatus{}
    private InternetMusic internetMusic;
    private @DownloadStatus int status;

    public DownloadMusic(InternetMusic internetMusic,@DownloadStatus int status) {
        this.internetMusic = internetMusic;
        this.status = status;
    }



    public InternetMusic getInternetMusic() {
        return internetMusic;
    }

    public void setInternetMusic(InternetMusic internetMusic) {
        this.internetMusic = internetMusic;
    }

    public @DownloadStatus int getStatus() {
        return status;
    }

    public void setStatus(@DownloadStatus int status) {
        this.status = status;
    }
}
