package com.web.moudle.musicDownload.bean;

import android.support.annotation.IntDef;

import com.web.data.InternetMusic;
import com.web.data.InternetMusicDetail;

public class DownloadMusic {
    public final static int DOWNLOAD_WAIT=1;
    public final static int DOWNLOAD_PAUSE=2;
    public final static int DOWNLOAD_DOWNLOADING=3;
    public final static int DOWNLOAD_DELETE=4;
    @IntDef({DOWNLOAD_WAIT,DOWNLOAD_PAUSE,DOWNLOAD_DOWNLOADING,DOWNLOAD_DELETE})
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
