package com.web.moudle.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.web.common.base.BaseSingleObserver;
import com.web.common.constant.Constant;
import com.web.common.tool.MToast;
import com.web.common.util.IOUtil;
import com.web.common.util.ResUtil;
import com.web.config.Shortcut;
import com.web.data.InternetMusic;
import com.web.data.InternetMusicDetail;
import com.web.data.Music;
import com.web.moudle.music.player.MediaQuery;
import com.web.moudle.music.player.MusicPlay;
import com.web.moudle.musicDownload.bean.DownloadMusic;
import com.web.moudle.net.proxy.InternetProxy;
import com.web.moudle.notification.DownloadNotification;
import com.web.web.R;

import org.jetbrains.annotations.NotNull;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.WorkerThread;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FileDownloadService extends Service {
    public final static String ACTION_DOWNLOAD = "com.web.web.File.download";
    public final static String ACTION_DELETE_ALL = "com.web.web.File.delete_all";
    public final static String INTENT_SINGLE_DATA = "com.web.web.File.data";
    private Connect connect;
    private ArrayList<DownloadListener> listeners = new ArrayList<>();
    private List<DownloadMusic> downloadList = new ArrayList<>();//**下载列表
    private List<DownloadMusic> downloadingList = new ArrayList<>();//**正在下载列表
    private List<DownloadMusic> completeList = new ArrayList<>();//**完成历史记录
    private int maxDownloadingCount = 3;


    private DownloadNotification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        notification = new DownloadNotification(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (connect == null) {
            connect = new Connect();
        }
        return connect;
    }

    public class Connect extends Binder {
        public void setDownloadListener(DownloadListener downloadListener) {
            if (!listeners.contains(downloadListener))
                FileDownloadService.this.listeners.add(downloadListener);
        }

        public void removeDownloadListener(DownloadListener downloadListener) {
            listeners.remove(downloadListener);
        }


        public boolean start(int id) {
            if (getDownloadingNum() >= maxDownloadingCount) return false;
            for (DownloadMusic dm : downloadList) {
                if (dm.getInternetMusicDetail().getId() == id) {
                    dm.setStatus(DownloadMusic.DOWNLOAD_WAIT);
                    startTask(dm);
                    return true;
                }
            }
            return false;
        }

        public boolean pause(int id) {
            for (DownloadMusic dm : downloadList) {
                if (dm.getInternetMusicDetail().getId() == id) {
                    dm.setStatus(DownloadMusic.DOWNLOAD_PAUSE);
                    return true;
                }
            }
            return false;
        }

        public void delete(int... id) {
            for (int anId : id) {
                delete(anId);
            }
            listChange();
        }

        public void delete(List<Integer> idList) {
            for (int id : idList) {
                delete(id);
            }
            listChange();
        }

        private void delete(int id) {
            for (int i = 0; i < downloadList.size(); i++) {
                DownloadMusic dm = downloadList.get(i);
                if (dm.getInternetMusicDetail().getId() == id) {
                    dm.getInternetMusicDetail().delete();
                    if (dm.getStatus() != DownloadMusic.DOWNLOAD_DOWNLOADING) {
                        FileDownloadService.this.delete(dm.getInternetMusicDetail());
                        downloadList.remove(i);
                    }
                    dm.setStatus(DownloadMusic.DOWNLOAD_DELETE);
                    return;
                }
            }
            for (int i = 0; i < completeList.size(); i++) {
                DownloadMusic dm = completeList.get(i);
                if (dm.getInternetMusicDetail().getId() == id) {
                    dm.getInternetMusicDetail().delete();
                    completeList.remove(i);
                    return;
                }
            }

        }

        public void getDownloadList() {
            getDownloadList(null);
        }

        public void getDownloadList(Runnable runnable) {
            if (downloadList.size() != 0) {
                listChange();
                return;
            }
            Single.create((SingleOnSubscribe<List<InternetMusicDetail>>) emitter -> {
                List<InternetMusicDetail> res = DataSupport.findAll(InternetMusicDetail.class);

                emitter.onSuccess(res);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSingleObserver<List<InternetMusicDetail>>() {
                        @Override
                        public void onSuccess(List<InternetMusicDetail> res) {
                            completeList.clear();
                            downloadList.clear();
                            for (InternetMusicDetail m : res) {
                                if (m.getHasDownload() == m.getSize()) {
                                    completeList.add(0, new DownloadMusic(m, DownloadMusic.DOWNLOAD_COMPLETE));
                                } else {
                                    downloadList.add(new DownloadMusic(m, DownloadMusic.DOWNLOAD_PAUSE));
                                }
                            }
                            listChange();
                            if (runnable != null) runnable.run();
                        }
                    });
        }

        public List<DownloadMusic> getDownloadingMusic() {
            downloadingList.clear();
            for (DownloadMusic dm : downloadList) {
                if (dm.getStatus() == DownloadMusic.DOWNLOAD_DOWNLOADING) {
                    downloadingList.add(dm);
                }
            }
            return downloadingList;
        }
    }

    /**
     * 删除本地相关信息
     *
     * @param internetMusic music
     */
    private void delete(InternetMusicDetail internetMusic) {
        MediaQuery.deleteCacheMusic(new Music(internetMusic.getSongName(),
                internetMusic.getArtistName(), internetMusic.getPath()));
    }

    public int onStartCommand(Intent intent, int flag, int startId) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_DOWNLOAD: {
                    InternetMusicDetail music = (InternetMusicDetail) intent.getSerializableExtra(INTENT_SINGLE_DATA);
                    //**此处需要请求下载列表，请求以前的下载
                    if (connect == null) {
                        connect = new Connect();
                        connect.getDownloadList(() -> addMusic(music));
                    } else {
                        addMusic(music);
                    }
                }
                break;
                case ACTION_DELETE_ALL: {//**删除下载历史记录
                    Runnable runnable=()->{
                        for(int i=0;i<completeList.size();i++){
                            InternetMusicDetail detail=completeList.get(i).getInternetMusicDetail();
                            detail.delete();
                        }
                        completeList.clear();
                        listChange();
                    };
                    if (connect == null) {
                        connect = new Connect();
                        connect.getDownloadList(runnable);
                    } else {
                        runnable.run();
                    }
                }
                break;
            }
        }
        return START_NOT_STICKY;
    }

    /**
     * 从外部添加任务
     *
     * @param music music
     */
    private void addMusic(InternetMusicDetail music) {
        for (int i = 0; i < downloadList.size(); i++) {
            if (downloadList.get(i).getInternetMusicDetail().getSongId().equals(music.getSongId())) {
                MToast.showToast(this, ResUtil.getString(R.string.alreadyInTask));
                return;
            }
        }
        music.save();
        DownloadMusic dm = new DownloadMusic(music, DownloadMusic.DOWNLOAD_WAIT);
        downloadList.add(dm);
        startTask(dm);
        listChange();
    }


    private int getDownloadingNum() {
        int num = 0;
        for (DownloadMusic dm : downloadList) {
            if (dm.getStatus() == DownloadMusic.DOWNLOAD_DOWNLOADING) num++;
        }
        return num;
    }


    public void startTask(final DownloadMusic downloadMusic) {
        Single.create((SingleOnSubscribe<DownloadMusic>) emitter -> {
            if (getDownloadingNum() >= maxDownloadingCount) {
                emitter.onError(new Throwable(""));
                return;
            }
            if (downloadMusic.getStatus() == DownloadMusic.DOWNLOAD_WAIT) {
                downloadingList.add(downloadMusic);
                downloadMusic.setStatus(DownloadMusic.DOWNLOAD_DOWNLOADING);
            } else {
                emitter.onError(new Throwable(""));
                return;
            }
            InternetMusicDetail music = downloadMusic.getInternetMusicDetail();
            if (music == null) return;

            music.save();
            String lyricPath = Shortcut.getLyricsPath(music.getSongName(), music.getArtistName());
            if (!Shortcut.fileExsist(lyricPath)) {
                IOUtil.onlineDataToLocal(music.getLrcLink(), lyricPath);
            }
            String iconPath = Shortcut.getIconPath(music.getArtistName());
            if (!Shortcut.fileExsist(iconPath)) {
                IOUtil.onlineDataToLocal(music.getSingerIconSmall(), iconPath);
            }

            downloadGO(downloadMusic);
            emitter.onSuccess(downloadMusic);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSingleObserver<DownloadMusic>() {
                    @Override
                    public void onSuccess(DownloadMusic res) {
                        for (DownloadMusic dm : downloadList) {
                            if (dm.getStatus() == DownloadMusic.DOWNLOAD_WAIT) {
                                startTask(dm);
                                break;
                            }
                        }
                        if (downloadingList.size() == 0) {
                            stopSelf();
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        super.onError(e);
                        onSuccess(null);
                    }
                });
    }


    @WorkerThread
    private void downloadGO(DownloadMusic downloadMusic) {//--需要新线程  保存至本地
        InternetMusicDetail music = downloadMusic.getInternetMusicDetail();
        if (music.getHasDownload() == music.getSize()) {//**已经完成了的
            downloadMusic.setStatus(DownloadMusic.DOWNLOAD_COMPLETE);
            complete(downloadMusic);
            return;
        }

        try {
            long hasDownload = music.getHasDownload();
            if (hasDownload == 0) {//--清除同名文件【重新下载】
                Shortcut.fileDelete(music.getPath());
            }
            if (TextUtils.isEmpty(music.getSongLink())) {
                MToast.showToast(FileDownloadService.this, ResUtil.getString(R.string.download_urlError));
                return;
            }
            notifyNotification();
            URL Url = new URL(InternetProxy.proxyUrl(music.getSongLink()));
            HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Range", "bytes=" + hasDownload + "-" + music.getSize());
            InputStream stream = connection.getInputStream();

            RandomAccessFile raf = new RandomAccessFile(new File(music.getPath()), "rw");
            statusChange(downloadMusic);
            IOUtil.StreamStop streamStop = new IOUtil.StreamStop();

            IOUtil.streamAccess(stream, raf, hasDownload, progress -> {
                music.setHasDownload(hasDownload + progress);
                music.save();//**时刻保存进度
                progressChange(music);
                if (downloadMusic.getStatus() != DownloadMusic.DOWNLOAD_DOWNLOADING) {//**暂停
                    streamStop.setStop(true);
                    if (downloadMusic.getStatus() == DownloadMusic.DOWNLOAD_DELETE) {//**删除任务
                        downloadList.remove(downloadMusic);
                        listChange();
                        delete(music);
                    }
                }
                return null;
            }, 1000, (complete, length) -> {
                if (complete) {//**下载完成 --- 广播
                    Music record = new Music(music.getSongName(), music.getArtistName(), music.getPath());
                    record.setAlbum(music.getAlbumName());
                    record.setAlbum_id(music.getAlbumId());
                    record.setSong_id(music.getSongId());
                    record.setSuffix(music.getFormat());
                    record.setDuration(music.getDuration() * 1000);//**将时间转换为ms
                    record.setAlbum(music.getAlbumName());
                    record.setSize(music.getSize());
                    record.saveOrUpdate();
                    music.setHasDownload(music.getSize());
                    downloadMusic.setStatus(DownloadMusic.DOWNLOAD_COMPLETE);
                    music.save();
                    complete(downloadMusic);
                }
                //**stop
                connection.disconnect();
                statusChange(downloadMusic);
                return null;
            }, streamStop);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void listChange() {
        for (DownloadListener listener : listeners) {
            listener.listChanged(downloadList, completeList);
        }
        notifyNotification();
    }

    private void notifyNotification() {
        int num = getDownloadingNum();
        if (num > 0) {//**正在下载的为0 则取消显示
            notification.notifyChange(getDownloadingNum());
        } else {
            notification.cancel();
        }
    }

    private void statusChange(DownloadMusic dm) {
        notifyNotification();
        for (DownloadListener listener : listeners) {
            listener.statusChange(dm.getInternetMusicDetail().getId(), dm.getStatus() == DownloadMusic.DOWNLOAD_DOWNLOADING);
        }
    }

    private void progressChange(InternetMusicDetail music) {
        for (DownloadListener listener : listeners) {
            listener.progressChange(music.getId(), music.getHasDownload(), music.getSize());
        }
    }

    /**
     * 下载完成告知播放器
     */
    private void complete(DownloadMusic dm) {
        notifyNotification();
        downloadList.remove(dm);
        completeList.add(0, dm);
        listChange();
        Intent intent = new Intent(this, MusicPlay.class);
        intent.setAction(MusicPlay.ACTION_DOWNLOAD_COMPLETE);
        intent.putExtra("path", dm.getInternetMusicDetail().getPath());
        startService(intent);

        //***添加进媒体库
        Intent intent2 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(dm.getInternetMusicDetail().getPath()));
        intent2.setData(uri);
        sendBroadcast(intent2);
    }


    public interface DownloadListener {
        void progressChange(int id, long progress, long max);

        void complete(InternetMusic music);

        void statusChange(int id, boolean isDownload);

        void listChanged(List<DownloadMusic> downloadMusicList, List<DownloadMusic> completeList);
    }


    /**
     * 添加一个下载任务
     * 以名称、文件大小来判断歌曲是否相同
     *
     * @param context context
     * @param music   music
     */
    public static void addTask(Context context, InternetMusicDetail music) {
        String originMusicName = music.getSongName();
        for (int i = 1; i < 10; i++) {//***音乐查重
            Music localMusic = DataSupport.where("path=?", Shortcut.createPath(music)).findFirst(Music.class);
            if (localMusic != null) {
                File file = new File(Shortcut.createPath(music));
                if (file.exists() && file.isFile()) {//**存在记录，有文件
                    if (music.getSize() == file.length()) {
                        //**文件完全相同
                        MToast.showToast(context, ResUtil.getString(R.string.alreadyInTask));
                        return;
                    } else {//**文件不同
                        music.setSongName(originMusicName + "(" + i + ")");
                    }
                } else {//**存在记录，文件丢失
                    music.setSongName(localMusic.getPath());
                    music.setPath(localMusic.getPath());
                    break;
                }
            } else {//**没有重复记录
                music.setPath(Shortcut.createPath(music));
            }
        }
        Intent intent = new Intent(context, FileDownloadService.class);
        intent.setAction(FileDownloadService.ACTION_DOWNLOAD);
        intent.putExtra(INTENT_SINGLE_DATA, music);
        context.startService(intent);
    }

    public static void clearAllRecord(Context context) {
        Intent intent = new Intent(context, FileDownloadService.class);
        intent.setAction(FileDownloadService.ACTION_DELETE_ALL);
        context.startService(intent);
    }
}
