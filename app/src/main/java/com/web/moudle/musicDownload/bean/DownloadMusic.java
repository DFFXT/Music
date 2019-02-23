package com.web.moudle.musicDownload.bean;

import com.web.data.InternetMusicDetail;

import androidx.annotation.IntDef;

public class DownloadMusic {
    public final static int DOWNLOAD_WAIT=1;
    public final static int DOWNLOAD_PAUSE=2;
    public final static int DOWNLOAD_DOWNLOADING=3;
    public final static int DOWNLOAD_DELETE=4;
    public final static int DOWNLOAD_COMPLETE=5;
    public final static int DOWNLOAD_DOWNLOADING_HEAD=6;
    public final static int DOWNLOAD_COMPLETE_HEAD=7;
    @IntDef({DOWNLOAD_WAIT,DOWNLOAD_PAUSE,DOWNLOAD_DOWNLOADING,DOWNLOAD_DELETE,DOWNLOAD_COMPLETE,DOWNLOAD_DOWNLOADING_HEAD,DOWNLOAD_COMPLETE_HEAD})
    @interface DownloadStatus{}
    private InternetMusicDetail internetMusic;
    private @DownloadStatus int status;

    public DownloadMusic(InternetMusicDetail internetMusic, @DownloadStatus int status) {
        this.internetMusic = internetMusic;
        this.status = status;
    }



    public InternetMusicDetail getInternetMusicDetail() {
        return internetMusic;
    }

    public void setInternetMusicDetail(InternetMusicDetail internetMusic) {
        this.internetMusic = internetMusic;
    }

    public @DownloadStatus int getStatus() {
        return status;
    }

    public void setStatus(@DownloadStatus int status) {
        this.status = status;
    }
}
