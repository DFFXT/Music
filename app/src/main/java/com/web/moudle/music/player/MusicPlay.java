package com.web.moudle.music.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.web.common.base.BaseSingleObserver;
import com.web.common.base.MyApplication;
import com.web.common.constant.Constant;
import com.web.common.tool.MToast;
import com.web.common.tool.Ticker;
import com.web.common.util.IOUtil;
import com.web.common.util.ResUtil;
import com.web.config.Shortcut;
import com.web.data.InternetMusicForPlay;
import com.web.data.Music;
import com.web.data.MusicGroup;
import com.web.data.MusicList;
import com.web.data.PlayerConfig;
import com.web.moudle.lockScreen.receiver.LockScreenReceiver;
import com.web.moudle.net.proxy.InternetProxy;
import com.web.moudle.notification.MyNotification;
import com.web.moudle.preference.SP;
import com.web.moudle.setting.lockscreen.LockScreenSettingActivity;
import com.web.web.R;

import org.litepal.crud.DataSupport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.lifecycle.LifecycleOwner;
import androidx.media.MediaBrowserServiceCompat;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kotlinx.coroutines.Dispatchers;

public class MusicPlay extends MediaBrowserServiceCompat {
    public final static String BIND = "not media bind";
    public final static String ACTION_PLAY_INTERNET_MUSIC = "com.web.web.MusicPlay.playInternetMusic";
    public final static String ACTION_PLAY_LOCAL_MUSIC = "com.web.web.MusicPlay.playLocalMusic";
    public final static String ACTION_NEXT = "com.web.web.MusicPlay.icon_next_black";
    public final static String ACTION_PRE = "com.web.web.MusicPlay.icon_pre_black";
    public final static String ACTION_STATUS_CHANGE = "com.web.web.MusicPlay.statusChange";
    public final static String ACTION_DOWNLOAD_COMPLETE = "com.web.web.MusicPlay.downloadComplete";
    public final static String ACTION_ClEAR_ALL_MUSIC = "clearAllMusic";
    public final static String ACTION_LOCKSCREEN = "ACTION_OpenLockScreen";

    public final static String COMMAND_GET_CURRENT_POSITION = "getCurrentPosition";
    public final static String COMMAND_GET_STATUS = "getStatus";
    public final static String COMMAND_SEND_SINGLE_DATA = "translateSingleData";
    public final static int COMMAND_RESULT_CODE_CURRENT_POSITION = 1;//**result code 标识为当前播放时间
    public final static int COMMAND_RESULT_CODE_STATUS = 2;//**result code 标识为播放状态

    private MediaPlayer player = new MediaPlayer();
    private MyNotification notification = new MyNotification(this);


    private List<MusicList<Music>> musicList = new ArrayList<>();//**音乐列表
    private List<Music> waitMusic = new ArrayList<>();//***等待播放的音乐
    private int waitIndex = 0;
    //@Nullable
    //private PlayInterface play;//**界面接口
    private PlayInterfaceManager play = new PlayInterfaceManager();
    private int groupIndex = 0, childIndex = -1;
    private Connect connect;
    private LockScreenReceiver lockScreenReceiver;
    private MediaSessionCompat sessionCompat;

    private PlayerConfig config = new PlayerConfig();
    private Ticker ticker;
    //********耳塞插拔广播接收
    private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!config.isHasInit()) return;
            if (intent.getIntExtra("state", 0) == 0) {
                musicPause();
            } else {
                musicPlay();
            }
        }
    };
    //*******来电监听器
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            if (!config.isHasInit()) return;
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: {
                    musicPause();
                }
                break;
                case TelephonyManager.CALL_STATE_IDLE: {
                    musicPlay();
                }
                break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        if (BIND.equals(arg0.getAction())) {
            if (connect == null) connect = new Connect();
            return connect;
        }
        return super.onBind(arg0);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(new ArrayList<>());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ticker = new Ticker(500, Dispatchers.getMain(), () -> {
            play.currentTime(groupIndex, childIndex, player.getCurrentPosition());
            sendDuring(player.getCurrentPosition());
            return null;
        });
        sessionCompat = new MediaSessionCompat(this, "2");
        sessionCompat.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                connect.changePlayerPlayingStatus();
            }

            @Override
            public void onPause() {
                musicPause();
            }

            @Override
            public void onSkipToNext() {
                connect.next();
            }

            @Override
            public void onSkipToPrevious() {
                connect.pre();
            }

            private long preEventTime = 0;
            private long preHookTime = 0;

            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                if (System.currentTimeMillis() - preEventTime < 50) return false;
                preEventTime = System.currentTimeMillis();
                KeyEvent event = mediaButtonEvent.getParcelableExtra("android.intent.extra.KEY_EVENT");
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_MEDIA_NEXT: {
                        onSkipToNext();
                    }
                    break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS: {
                        onSkipToPrevious();
                    }
                    break;
                    case KeyEvent.KEYCODE_HEADSETHOOK: {
                        if (System.currentTimeMillis() - preHookTime < 100) return false;
                        else if (System.currentTimeMillis() - preHookTime < 800) {
                            onSkipToNext();
                        } else {
                            connect.changePlayerPlayingStatus();
                        }
                        preHookTime = System.currentTimeMillis();
                    }
                    break;
                }
                return true;
            }

            @Override
            public void onCommand(String command, Bundle extras, ResultReceiver cb) {
                Bundle bundle = new Bundle();
                switch (command) {
                    case COMMAND_GET_CURRENT_POSITION: {
                        if (!config.isHasInit()) return;
                        bundle.putInt(COMMAND_SEND_SINGLE_DATA, player.getCurrentPosition());
                        cb.send(COMMAND_RESULT_CODE_CURRENT_POSITION, bundle);

                    }
                    break;
                    case COMMAND_GET_STATUS: {
                        bundle.putBoolean(COMMAND_SEND_SINGLE_DATA, player.isPlaying());
                        cb.send(COMMAND_RESULT_CODE_STATUS, bundle);
                    }
                    break;
                }
            }
        });


        sessionCompat.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        // setSessionToken
        setSessionToken(sessionCompat.getSessionToken());
        sessionCompat.setActive(true);

        if (!SP.INSTANCE.getBoolean(Constant.spName, Constant.SpKey.noLockScreen, false)) {
            lockScreen();
        }
        //**耳塞插拔注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(headsetReceiver, filter);

        //**来电注册监听
        TelephonyManager manager = getSystemService(TelephonyManager.class);
        manager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public void onDestroy() {//--移除notification、取消注册、监听
        unLockScreen();
        unregisterReceiver(headsetReceiver);
        getSystemService(TelephonyManager.class).listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        stopForeground(true);
    }

    /**
     * 激活锁屏
     */
    private void lockScreen() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenReceiver = new LockScreenReceiver(), filter);
    }

    private void unLockScreen() {
        if (lockScreenReceiver != null) {
            unregisterReceiver(lockScreenReceiver);
            lockScreenReceiver = null;
        }
    }

    public class Connect extends Binder {
        Connect() {
            player.setLooping(false);
            player.setOnBufferingUpdateListener((p, percent) -> play.bufferingUpdate(percent));
            player.setOnPreparedListener(mp -> {
                config.setPrepared(true);
                musicLoad();
            });
            player.setOnCompletionListener(mp -> {
                switch (config.getPlayType()) {
                    //**列表循环
                    case ALL_LOOP: {
                        if (config.getMusicOrigin() == PlayerConfig.MusicOrigin.WAIT) {//***播放准备音乐
                            loadNextWait();
                        } else next();
                    }
                    break;
                    //**单曲循环
                    case ONE_LOOP: {
                        player.seekTo(0);
                        musicPlay();
                    }
                    break;
                    //**列表不循环
                    case ALL_ONCE: {
                        if (config.getMusicOrigin() == PlayerConfig.MusicOrigin.WAIT) {//***播放准备音乐
                            if (waitIndex < waitMusic.size()) {
                                loadNextWait();
                            }
                        } else if (childIndex < musicList.get(groupIndex).size() - 1) {
                            next();
                        } else {//***暂停
                            musicPause();
                        }
                    }
                    break;
                    //**单曲不循环
                    case ONE_ONCE: {
                        musicPause();
                    }
                    break;
                }

            });

        }


        /***
         * 每隔一秒获取当前时间
         */


        public void addObserver(LifecycleOwner owner, PlayInterface play) {
            MusicPlay.this.play.addObserver(owner, play);
        }
        public void removeObserver(PlayInterface play){
            MusicPlay.this.play.removeObserver(play);
        }

        /**
         * 在已经初始化或调用
         * @param group
         */
        public void getList(int group) {
            groupIndex = group;
            if (musicList.size() == 0)
                getMusicList();
            else {
                musicListChange();
            }
        }

        public void scanLocalMusic() {
            scanMusicMedia();
        }

        public void musicSelect(int group, int child) {
            if (groupIndex != group || child != childIndex) {
                play(group, child);
            } else if (player.isPlaying()) {
                musicPause();
            } else {
                musicPlay();
            }
        }

        /**
         * 控制音乐
         *
         * @param group group
         * @param child child
         */
        private void play(int group, int child) {
            if (groupIndex != group || child != childIndex) {
                groupIndex = group;
                childIndex = child;
                waitMusic.clear();
                config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
                loadMusic(musicList.get(groupIndex).get(childIndex));
            } else {
                player.seekTo(0);
                musicPlay();
            }
        }

        /**
         * 播放下一首
         */
        public void next() {
            waitMusic.clear();
            //**在线播放下一首---------当做本地播放
            config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
            play(groupIndex, nextIndex());
        }

        /**
         * 播放前一曲
         */
        public void pre() {
            waitMusic.clear();
            config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
            play(groupIndex, preIndex());
        }

        //***切换播放状态
        public void changePlayerPlayingStatus() {
            if (player.isPlaying()) {
                musicPause();
            } else {
                if (config.getMusic() != null) {
                    musicPlay();
                } else {
                    next();
                }
            }


        }

        public List<Music> getWaitMusic() {
            return waitMusic;
        }

        public void addToWait(Music music) {
            for (Music m : waitMusic) {
                if (m.getPath().equals(music.getPath())) return;
            }
            config.setMusicOrigin(PlayerConfig.MusicOrigin.WAIT);
            waitMusic.add(music);
            if (waitMusic.size() == 1) {//**添加了播放列表，第一个添加的直接抢占播放
                waitIndex = 0;
                loadNextWait();
            }
        }

        public void groupChange() {
            getMusicList();
        }

        public int getWaitIndex() {
            return waitIndex;
        }

        public void removeWait(int index) {
            if (index < 0 || index > waitMusic.size()) return;
            if (index == 0 && waitMusic.size() == 1) {//***移除最后一个
                next();
                return;
            }
            if (index < waitIndex) {//**移除播放之前的
                waitIndex--;
                waitMusic.remove(index);
            } else if (index > waitIndex && index < waitMusic.size()) {//**移除之后播放的
                waitMusic.remove(index);
            } else if (index == waitIndex) {//**移除正在播放的
                waitIndex--;
                waitMusic.remove(index);
                loadNextWait();
            }
        }

        public void seekTo(int millis) {
            if (canPlay()) {
                player.seekTo(millis);
                play.currentTime(groupIndex, childIndex, millis);
            }
        }

        public void getPlayerInfo() {
            disPatchMusicInfo();
        }

        public PlayerConfig getConfig() {
            return config;
        }

        @Nullable
        public Music getPlayingMusic() {
            return config.getMusic();
        }

        /**
         * 播放在线音乐
         *
         * @param music music
         */
        public void playInternet(final InternetMusicForPlay music) {
            config.setMusicOrigin(PlayerConfig.MusicOrigin.INTERNET);
            loadMusic(music);
            //**下载歌词和图片
            Single.create(emitter -> {
                if (!Shortcut.fileExsist(music.getLyricsPath())) {
                    IOUtil.onlineDataToLocal(music.getLrcLink(), music.getLyricsPath());
                }
                if (!Shortcut.fileExsist(music.getIconPath())) {
                    IOUtil.onlineDataToLocal(music.getImgAddress(), music.getIconPath());
                    config.setBitmapPath(music.getSinger());
                }
                emitter.onSuccess(1);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSingleObserver<Object>() {
                        @Override
                        public void onSuccess(Object obj) {
                            if (config.isPrepared())//**必须在准备好了才能start和getDuration
                                musicLoad();
                        }
                    });
        }

        /**
         * 播放本地音乐，就是从文件管理器里面跳转过来的音乐
         *
         * @param music music
         */
        public void playLocal(Music music) {
            config.setMusic(music);
            config.setMusicOrigin(PlayerConfig.MusicOrigin.STORAGE);
            loadMusic(music);
        }

        public void playWait(int index) {
            if (index >= 0 && index < waitMusic.size()) {
                waitIndex = index;
                loadMusic(waitMusic.get(index));
            }
        }

        public void delete(boolean deleteFile, int groupIndex, int... childIndex) {
            MusicPlay.this.deleteMusic(deleteFile, groupIndex, childIndex);
        }

        public void delete(boolean deleteFile, int groupIndex, List<Integer> childList) {
            int list[] = new int[childList.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = childList.get(i);
            }
            delete(deleteFile, groupIndex, list);
        }
    }

    /**
     * 判定当前歌曲是否能播放
     *
     * @return bool
     */
    private boolean canPlay(int group, int child) {
        if (config.getMusicOrigin() == PlayerConfig.MusicOrigin.INTERNET) {//**来源为网络时可播放
            return true;
        }
        if (group >= 0 && child >= 0) {
            if (group < musicList.size()) {
                return child < musicList.get(group).size();
            }
        }
        return false;
    }

    private boolean canPlay() {
        return canPlay(groupIndex, childIndex);
    }

    private int nextIndex() {
        int index;
        if (childIndex + 1 >= musicList.get(groupIndex).size()) index = 0;
        else index = childIndex + 1;
        return index;
    }

    private int preIndex() {
        if (childIndex - 1 < 0) return Math.max(musicList.get(groupIndex).size() - 1, 0);
        else return childIndex - 1;
    }

    /**
     * 加载等待歌曲
     */
    private void loadNextWait() {
        waitIndex++;
        if (waitMusic.size() <= waitIndex) waitIndex = 0;
        loadMusic(waitMusic.get(waitIndex));

    }


    private MediaMetadataCompat.Builder metaDataBuilder = new MediaMetadataCompat.Builder();

    /**
     * 加载音乐
     *
     * @param music music
     */
    private void loadMusic(Music music) {
        config.setHasInit(true);
        config.setMusic(music);
        config.setBitmapPath(music.getSinger());
        player.reset();
        try {
            config.setPrepared(false);
            player.setDataSource(InternetProxy.proxyUrl(music.getPath()));
            //player.setDataSource(music.getPath());
            player.prepareAsync();
        } catch (IOException e) {//***播放异常，如果是文件不存在则删除记录
            if (e instanceof FileNotFoundException) {
                MToast.showToast(this, ResUtil.getString(R.string.fileNotFound));
            }
            if (config.getMusicOrigin() == PlayerConfig.MusicOrigin.LOCAL) {
                deleteMusic(false, groupIndex, childIndex);
                connect.next();
            } else if (config.getMusicOrigin() == PlayerConfig.MusicOrigin.WAIT) {
                connect.next();
            } else if (config.getMusicOrigin() == PlayerConfig.MusicOrigin.STORAGE) {
                MToast.showToast(this, ResUtil.getString(R.string.cannotPlay));
            }
        }
        play.musicOriginChanged(config.getMusicOrigin());

        metaDataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.getMusicName())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.getSinger())
                .putString(MediaMetadataCompat.METADATA_KEY_COMPOSER, music.getLyricsPath());

        sessionCompat.setMetadata(metaDataBuilder.build());
    }

    private void musicPlay() {

        //player.setPlaybackParams(player.getPlaybackParams().setSpeed(2));
        Music music = config.getMusic();
        if (music == null) return;
        play.play();
        player.start();
        notification.setName(music.getMusicName());
        notification.setSinger(music.getSinger());
        notification.setPlayStatus(true);
        notification.setBitMap(config.getBitmap());
        notification.show();
        ticker.start();
        try {
            sendState(PlaybackStateCompat.STATE_PLAYING);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void musicLoad() {
        play.load(groupIndex, childIndex, config.getMusic(), getDuration());
        musicPlay();
    }

    private int getDuration() {
        if (!config.isPrepared()) return 0;
        return player.getDuration();
    }

    private void musicPause() {
        ticker.stop();
        player.pause();
        play.pause();
        notification.setPlayStatus(false);
        notification.show();
        sendState(PlaybackStateCompat.STATE_PAUSED);
    }

    /**
     * 发送播放器状态
     *
     * @param state state
     */
    private void sendState(int state) {
        PlaybackStateCompat stateCompat = new PlaybackStateCompat.Builder()
                .setState(state, 1, 1)
                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
                .build();
        sessionCompat.setPlaybackState(stateCompat);

    }

    private Bundle bundle = new Bundle();
    public final static String MUSIC_DURING = "md";

    private void sendDuring(int during) {
        bundle.putInt(MUSIC_DURING, during);
        PlaybackStateCompat stateCompat = new PlaybackStateCompat.Builder()
                .setExtras(bundle)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build();
        try {
            sessionCompat.setPlaybackState(stateCompat);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除音乐记录
     *
     * @param group      group
     * @param childList  child
     * @param deleteFile 是否删除源文件
     */
    private void deleteMusic(boolean deleteFile, int group, int... childList) {
        List<Music> deleteList = new ArrayList<>();
        for (int child : childList) {
            Music music = musicList.get(group).get(child);
            deleteList.add(music);
            if (music == config.getMusic()) reset();
            MediaQuery.deleteMusic(this, music, group, deleteFile);
        }
        for (Music m : deleteList) {
            musicList.get(group).remove(m);
        }
        getMusicList();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action == null) return START_NOT_STICKY;
        switch (action) {
            case ACTION_NEXT:
                connect.next();
                break;
            case ACTION_PRE:
                connect.pre();
                break;
            case ACTION_STATUS_CHANGE:
                connect.changePlayerPlayingStatus();
                break;
            case ACTION_DOWNLOAD_COMPLETE: {
                Music music = DataSupport.where("path=?",
                        intent.getStringExtra("path")).findFirst(Music.class);
                musicList.get(0).add(music);
                musicListChange();
            }
            break;
            case ACTION_ClEAR_ALL_MUSIC: {
                DataSupport.deleteAll(Music.class);
                DataSupport.deleteAll(MusicGroup.class);
                SP.INSTANCE.putValue(Constant.spName, Constant.SpKey.clearAll, true);
                getMusicList();
                reset();
            }
            break;
            case ACTION_LOCKSCREEN: {
                boolean noLock = LockScreenSettingActivity.Companion.getNoLockScreen();
                if (noLock) {
                    unLockScreen();
                } else {
                    lockScreen();
                }
            }
            break;
            case ACTION_PLAY_INTERNET_MUSIC: {
                connect.playInternet((InternetMusicForPlay) intent.getSerializableExtra(MusicPlay.COMMAND_SEND_SINGLE_DATA));
            }
            break;
            case ACTION_PLAY_LOCAL_MUSIC: {
                config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
                loadMusic((Music) intent.getSerializableExtra(MusicPlay.COMMAND_SEND_SINGLE_DATA));
            }
            break;
        }
        return START_NOT_STICKY;
		/*
		 * return START_NOT_STICKY;
		 “非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。
		 否则就会出现重新启动失败
		 */
    }


    private void musicListChange() {
        play.musicListChange(groupIndex, musicList);
    }

    //**分发信息
    private void disPatchMusicInfo() {
        Music music = config.getMusic();
        play.load(groupIndex, childIndex, music, getDuration());
        if (!player.isPlaying()) {
            play.pause();
        }
        if (groupIndex != -1 && childIndex != -1) {
            play.currentTime(groupIndex, childIndex, player.getCurrentPosition());
        }

        play.musicOriginChanged(config.getMusicOrigin());
        play.playTypeChanged(config.getPlayType());
    }

    private void reset() {
        player.reset();
        play.load(-1, -1, null, 0);
        play.currentTime(0, 0, 0);
        play.musicOriginChanged(PlayerConfig.MusicOrigin.LOCAL);
        play.pause();
        config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
        config.setMusic(null);
        waitMusic.clear();
        waitIndex = 0;
    }

    private Lock gettingMusicLock = new ReentrantLock();
    private Lock scanningMusicLock = new ReentrantLock();

    /**
     * 获取本地列表
     */
    @UiThread
    private void getMusicList() {
        if (!gettingMusicLock.tryLock()) return;
        MediaQuery.getLocalList((res) -> {
            musicList.clear();
            musicList.addAll(res);
            if(musicList.get(0).size()==0){//**没有扫描媒体库
                scanMusicMedia();
            }else{
                musicListChange();
                gettingMusicLock.unlock();
            }
            return null;
        });

    }

    @UiThread
    private void scanMusicMedia() {
        if (!scanningMusicLock.tryLock()) return;
        MediaQuery.scanMedia(this, (isOk) -> {
            if (isOk) {
                getMusicList();
                MToast.showToast(MyApplication.context, ResUtil.getString(R.string.scanOver));
            }else{
                MToast.showToast(MyApplication.context, ResUtil.getString(R.string.scanOver_noMusic));
                play.musicListChange(0,null);
            }
            scanningMusicLock.unlock();
            return null;
        });

    }


    /**
     * 播放网络音乐
     *
     * @param ctx   context
     * @param music music
     */
    public static void play(Context ctx, InternetMusicForPlay music) {
        Intent intent = new Intent(ctx, MusicPlay.class);
        intent.putExtra(MusicPlay.COMMAND_SEND_SINGLE_DATA, music);
        intent.setAction(MusicPlay.ACTION_PLAY_INTERNET_MUSIC);
        ctx.startService(intent);
    }

    public static void play(Context ctx, Music music) {
        Intent intent = new Intent(ctx, MusicPlay.class);
        intent.putExtra(MusicPlay.COMMAND_SEND_SINGLE_DATA, music);
        intent.setAction(MusicPlay.ACTION_PLAY_LOCAL_MUSIC);
        ctx.startService(intent);
    }
}

