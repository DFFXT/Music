package com.web.moudle.music.player;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.web.common.base.BaseObserver;
import com.web.common.base.BaseSingleObserver;
import com.web.common.base.ChineseComparator;
import com.web.common.base.MyApplication;
import com.web.common.base.SingleSchedulerTransform;
import com.web.common.constant.Constant;
import com.web.common.tool.MToast;
import com.web.common.tool.Ticker;
import com.web.common.util.IOUtil;
import com.web.common.util.MediaQuery;
import com.web.common.util.ResUtil;
import com.web.config.Shortcut;
import com.web.data.InternetMusicForPlay;
import com.web.data.Music;
import com.web.data.MusicList;
import com.web.data.RecentPlayMusic;
import com.web.moudle.music.player.other.PlayInterface;
import com.web.moudle.music.player.other.PlayInterfaceManager;
import com.web.moudle.music.player.other.PlayerConfig;
import com.web.moudle.lockScreen.receiver.LockScreenReceiver;
import com.web.moudle.lyrics.EqualizerActivity;
import com.web.moudle.lyrics.bean.SoundInfo;
import com.web.moudle.musicEntry.bean.MusicDetailInfo;
import com.web.moudle.musicEntry.model.MusicDetailModel;
import com.web.moudle.musicEntry.ui.MusicDetailActivity;
import com.web.moudle.net.proxy.InternetProxy;
import com.web.moudle.notification.MyNotification;
import com.web.moudle.preference.SP;
import com.web.moudle.setting.lockscreen.LockScreenSettingActivity;
import com.web.moudle.setting.lyrics.LyricsSettingActivity;
import com.web.web.R;

import org.litepal.crud.DataSupport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import androidx.annotation.UiThread;
import androidx.lifecycle.LifecycleOwner;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlinx.coroutines.Dispatchers;

public class MusicPlay extends Service {
    public final static String BIND = "not media bind";
    public final static String ACTION_PLAY_INTERNET_MUSIC = "com.web.web.MusicPlay.playInternetMusic";
    public final static String ACTION_PLAY_LOCAL_MUSIC = "com.web.web.MusicPlay.playLocalMusic";
    public final static String ACTION_NEXT = "com.web.web.MusicPlay.icon_next_black";
    public final static String ACTION_PRE = "com.web.web.MusicPlay.icon_pre_black";
    public final static String ACTION_STATUS_CHANGE = "com.web.web.MusicPlay.statusChange";
    public final static String ACTION_DOWNLOAD_COMPLETE = "com.web.web.MusicPlay.downloadComplete";
    public final static String ACTION_ClEAR_ALL_MUSIC = "clearAllMusic";
    public final static String ACTION_LOCKSCREEN = "ACTION_OpenLockScreen";
    public final static String ACTION_FLOAT_WINDOW_CHANGE = "ACTION_FLOAT_WINDOW_CHANGE";
    public final static String ACTION_SCAN = "ACTION_SCAN";

    public final static String COMMAND_SEND_SINGLE_DATA = "translateSingleData";

    private MediaPlayer player = new MediaPlayer();
    private MyNotification notification;
    private FloatLyricsManager floatLyricsManager;
    private RandomSystem randomSystem=new RandomSystem();


    private List<MusicList<Music>> musicList = new ArrayList<>();//**音乐列表
    private List<Music> waitMusic = new ArrayList<>();//***等待播放的音乐
    private int waitIndex = 0;
    //@Nullable
    //private PlayInterface play;//**界面接口
    private PlayInterfaceManager play = new PlayInterfaceManager();
    private int groupIndex = -1, childIndex = -1;
    private Connect connect;
    private LockScreenReceiver lockScreenReceiver;
    private Equalizer equalizer;

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
            if (connect == null){
                connect = new Connect();
                EqualizerActivity.saveDefaultSoundEffect(equalizer);
                List<SoundInfo> soundInfos=EqualizerActivity.getCurrentSoundEffect();
                for(int i=0;i<soundInfos.size();i++){
                    equalizer.setBandLevel((short) i,(short) (soundInfos.get(i).getValue()+soundInfos.get(i).getMin()));
                }

            }
            return connect;
        }
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        notification = new MyNotification(this);
        ticker = new Ticker(500,0, Dispatchers.getMain(), () -> {
            play.currentTime(groupIndex, childIndex, player.getCurrentPosition());
            return null;
        });
        //**设置均衡器
        equalizer=new Equalizer(0,player.getAudioSessionId());
        equalizer.setEnabled(true);


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

    /**
     * 开启歌词浮窗
     */
    private void lyricsFloatWindowChange(){
        if(LyricsSettingActivity.lyricsOverlap()){
            if(floatLyricsManager==null){
                floatLyricsManager=new FloatLyricsManager(getApplicationContext(),connect);
            }
            floatLyricsManager.refresh();
        }else{
            if(floatLyricsManager!=null){
                floatLyricsManager.close();
            }
        }
    }

    public class Connect extends Binder {
        Connect() {

            player.setLooping(false);
            player.setOnBufferingUpdateListener((p, percent) -> play.bufferingUpdate(percent));
            player.setOnPreparedListener(mp -> {
                config.setPrepared(true);
                musicLoad();
                if(floatLyricsManager==null){
                    lyricsFloatWindowChange();
                }

            });
            player.setOnCompletionListener(mp -> connect.next());
        }

        public int getMediaPlayId(){
            return player.getAudioSessionId();
        }

        public Equalizer getEqualizer(){
            return equalizer;
        }

        public void addObserver(LifecycleOwner owner, PlayInterface play) {
            MusicPlay.this.play.addObserver(owner, play);
        }
        public void removeObserver(LifecycleOwner owner){
            MusicPlay.this.play.removeObserver(owner);
        }

        /**
         * 在已经初始化或调用
         * @param group g
         */
        public void selectList(int group,int child) {
            if(groupIndex==group){//**不用置换
                return;
            }
            childIndex=child;
            groupIndex = group;
        }
        public void getList(){
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
            //**没有任何音乐
            if(child<0)return;
            waitMusic.clear();
            if(config.getMusicOrigin()!= PlayerConfig.MusicOrigin.LOCAL){
                randomSystem.reset(1);
                randomSystem.addIntRange(0,musicList.get(group).size());
                config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
            }
            if (groupIndex != group || child != childIndex) {
                groupIndex = group;
                childIndex = child;
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
            switch (config.getPlayType()) {
                //**列表循环
                case ALL_LOOP: {
                    if (config.getMusicOrigin() == PlayerConfig.MusicOrigin.WAIT) {//***播放准备音乐
                        loadNextWait(-1);
                    } else{
                        config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
                        connect.play(groupIndex, nextIndex());
                    }
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
                            loadNextWait(-1);
                        }
                    } else if (childIndex < musicList.get(groupIndex).size() - 1) {
                        connect.next();
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
                case RANDOM:{
                    if(groupIndex<0) groupIndex=0;
                    int index=randomSystem.getRandomNumber().intValue();
                    if(config.getMusicOrigin()== PlayerConfig.MusicOrigin.WAIT){
                        loadNextWait(index);
                    }else{
                        connect.play(groupIndex,index);
                    }

                }
                break;
            }
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

        public void addToWait(Music music){
            if(music instanceof InternetMusicForPlay){//**添加的是网络音乐
                for(Music m:waitMusic){
                    if(music.getSong_id().equals(m.getSong_id())){//**已经存在
                        return;
                    }
                }
            }
            else {
                for (Music m : waitMusic) {
                    if (m.getPath().equals(music.getPath())) return;
                }
            }
            if(config.getMusicOrigin()!=PlayerConfig.MusicOrigin.WAIT){
                waitIndex=0;
                waitMusic.clear();
                randomSystem.reset(2);
                config.setMusicOrigin(PlayerConfig.MusicOrigin.WAIT);
                waitMusic.add(music);
                randomSystem.addNumber(0);
                loadNextWait(0);
            }else{
                waitMusic.add(music);
                randomSystem.addNumber(waitMusic.size()-1);
            }
        }
        public void addListToWait(List<Music> ml){
            for(Music m:ml){
                addToWait(m);
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
                loadNextWait(-1);
            }
        }

        public void seekTo(int millis) {
            if (canPlay()) {
                player.seekTo(millis);
                play.currentTime(groupIndex, childIndex, millis);
            }
        }

        public void getPlayerInfo(LifecycleOwner owner) {
            disPatchMusicInfo(owner);
        }
        public void dispatchLoad(){
            play.load(groupIndex,childIndex,config.getMusic(),getDuration());
        }

        public PlayerConfig getConfig() {
            return config;
        }

        public void changePlayType(){
            switch (config.getPlayType()) {
                case ALL_LOOP: {
                    config.setPlayType(PlayerConfig.PlayType.ONE_LOOP);
                }
                break;
                case ONE_LOOP: {
                    config.setPlayType(PlayerConfig.PlayType.ALL_ONCE);
                }
                break;
                case ALL_ONCE: {
                    config.setPlayType(PlayerConfig.PlayType.ONE_ONCE);
                }
                break;
                case ONE_ONCE: {
                    config.setPlayType(PlayerConfig.PlayType.RANDOM);
                    randomSystem.reset(3);
                    randomSystem.addIntRange(0,waitMusic.size());

                }
                break;
                case RANDOM:{
                    config.setPlayType(PlayerConfig.PlayType.ALL_LOOP);
                }
                break;
            }
            play.playTypeChanged(config.getPlayType());
        }

        private MusicDetailModel model;

        /**
         * 播放在线音乐
         *
         * @param music music
         */
        private void playInternet(final InternetMusicForPlay music) {

            if(!TextUtils.isEmpty(music.getPath())){
                config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
                loadMusic(music);
            }
            //**下载歌词和图片
            Single.create(emitter -> {
                config.setMusicOrigin(PlayerConfig.MusicOrigin.INTERNET);
                //**判断是否是从歌单传入的数据，歌单数据没有路径，只有id
                if(TextUtils.isEmpty(music.getPath())){
                    if(model==null)model=new MusicDetailModel();
                    model.getMusicDetail(music.getSong_id())
                            .subscribe(new BaseObserver<MusicDetailInfo>() {
                                @Override
                                public void onNext(MusicDetailInfo musicDetailInfo) {
                                    MusicDetailActivity.map(musicDetailInfo,music);
                                }
                            });
                    AndroidSchedulers.mainThread().scheduleDirect(()->loadMusic(music));
                }


                RecentPlayMusic recentPlayMusic=new RecentPlayMusic();
                recentPlayMusic.setSongId(music.getSong_id());
                recentPlayMusic.setMusicName(music.getMusicName());
                recentPlayMusic.setArtist(music.getSinger());
                recentPlayMusic.setDuration(music.getDuration());
                recentPlayMusic.setAlbumName(music.getAlbum());
                recentPlayMusic.setAlbumId(music.getAlbum_id());

                //**可以尝试设置为本地路径
                recentPlayMusic.setImageLink(music.getImgAddress());
                recentPlayMusic.setLrcLink(music.getLrcLink());

                recentPlayMusic.setPath(music.getPath());
                recentPlayMusic.saveOrUpdate();


                if (!Shortcut.fileExsist(music.getLyricsPath())) {
                    IOUtil.onlineDataToLocal(music.getLrcLink(), music.getLyricsPath());
                }
                if (!Shortcut.fileExsist(music.getIconPath())) {
                    IOUtil.onlineDataToLocal(music.getImgAddress(), music.getIconPath());
                    config.setBitmapPath(music.getSinger());
                }
                emitter.onSuccess(1);
            }).compose(new SingleSchedulerTransform<>())
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
        public void play(Music music) {
            if(music instanceof InternetMusicForPlay){
                playInternet((InternetMusicForPlay) music);
            }else{
                config.setMusic(music);
                config.setMusicOrigin(PlayerConfig.MusicOrigin.STORAGE);
                loadMusic(music);
            }

        }

        public void playWait(int index) {
            loadNextWait(index);
        }

        public void delete(boolean deleteFile, int groupIndex, int... childIndex) {
            MusicPlay.this.deleteMusic(deleteFile, groupIndex, childIndex);
        }

        public void delete(boolean deleteFile, int groupIndex, List<Integer> childList) {
            int[] list = new int[childList.size()];
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

    /**
     * 获取下一首
     * @return 如果有音乐则返回下标，如果没有则返回-1
     */
    private int nextIndex() {
        if(musicList.get(groupIndex).size()==0)return -1;
        int index;
        if (childIndex + 1 >= musicList.get(groupIndex).size()) index = 0;
        else index = childIndex + 1;
        return index;
    }

    /**
     * 获取上一曲
     * @return 没有音乐则返回-1 有则返回下标
     */
    private int preIndex() {
        if(musicList.get(groupIndex).size()==0)return -1;
        if (childIndex - 1 < 0) return Math.max(musicList.get(groupIndex).size() - 1, 0);
        else return childIndex - 1;
    }

    /**
     * 加载等待歌曲
     * index<0 下一曲 index>0 指定index
     */
    private void loadNextWait(int index) {
        if(index<0){
            waitIndex++;
        }else{
            waitIndex=index;
        }
        if (waitMusic.size() <= waitIndex) waitIndex = 0;
        Music music=waitMusic.get(waitIndex);
        if(music instanceof InternetMusicForPlay){
            connect.playInternet((InternetMusicForPlay) music);
        }else{
            checkMusicIndexAndLoad(waitMusic.get(waitIndex));
        }
    }

    /**
     * 找到当前waitMusic在group里面的index
     * @param waitMusic wait
     */
    private void checkMusicIndexAndLoad(Music waitMusic){
        childIndex=-1;
        for(int i=0;i<musicList.get(groupIndex).size();i++){
            if(waitMusic.getPath().equals(musicList.get(groupIndex).get(i).getPath())){
                childIndex=i;
                break;
            }
        }
        loadMusic(waitMusic);
    }


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
            player.prepareAsync();
        } catch (IOException e) {//***播放异常，如果是文件不存在则删除记录
            if (e instanceof FileNotFoundException) {
                MToast.showToast(this, ResUtil.getString(R.string.fileNotFound));
            }
            if (config.getMusicOrigin() == PlayerConfig.MusicOrigin.LOCAL) {
                deleteMusic(false, groupIndex, childIndex);
                connect.next();
            } else if (config.getMusicOrigin() == PlayerConfig.MusicOrigin.WAIT) {
                loadNextWait(-1);
            } else if (config.getMusicOrigin() == PlayerConfig.MusicOrigin.STORAGE) {
                MToast.showToast(this, ResUtil.getString(R.string.cannotPlay));
            }
        }
        play.musicOriginChanged(config.getMusicOrigin());
    }

    private void musicPlay() {

        //player.setPlaybackParams(player.getPlaybackParams().setSpeed(2));
        Music music = config.getMusic();
        if (music == null) return;
        play.play();
        player.start();
        notification.setName(music.getMusicName())
                .setSinger(music.getSinger())
                .setPlayStatus(true)
                .setBitMap(config.getBitmap())
                .notifyChange();
        ticker.start();
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
        notification.setPlayStatus(false)
                .notifyChange();
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
                sort();
                musicListChange();
            }
            break;
            case ACTION_ClEAR_ALL_MUSIC: {
                DataSupport.deleteAll(Music.class);
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
                config.setMusicOrigin(PlayerConfig.MusicOrigin.INTERNET);
                connect.playInternet((InternetMusicForPlay) intent.getSerializableExtra(MusicPlay.COMMAND_SEND_SINGLE_DATA));
            }
            break;
            case ACTION_PLAY_LOCAL_MUSIC: {
                config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);

                loadMusic((Music) intent.getSerializableExtra(MusicPlay.COMMAND_SEND_SINGLE_DATA));
            }
            break;
            case ACTION_FLOAT_WINDOW_CHANGE:{
                lyricsFloatWindowChange();
            }break;
            case ACTION_SCAN:{
                scanMusicMedia();
            }
        }
        return START_NOT_STICKY;
		/*
		 * return START_NOT_STICKY;
		 “非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。
		 否则就会出现重新启动失败
		 */
    }


    private void musicListChange() {
        childIndex= musicList.get(groupIndex).indexOf(config.getMusic());
        waitMusic.clear();
        waitMusic.addAll(musicList.get(groupIndex).getAll());
        randomSystem.reset(4);
        randomSystem.addIntRange(0,musicList.get(groupIndex).size());
        play.musicListChange(groupIndex,childIndex, musicList);
    }

    //**分发信息
    private void disPatchMusicInfo(LifecycleOwner owner) {
        play.load(owner,groupIndex, childIndex, config.getMusic(), getDuration());
        if (!player.isPlaying()) {
            play.pause(owner);
        }
        if (groupIndex != -1 && childIndex != -1) {
            play.currentTime(owner,groupIndex, childIndex, player.getCurrentPosition());
        }

        play.musicOriginChanged(owner,config.getMusicOrigin());
        play.playTypeChanged(owner,config.getPlayType());
        if(config.getMusicOrigin()!= PlayerConfig.MusicOrigin.WAIT){
            waitMusic.clear();
        }
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
            sort();
            if(musicList.get(0).size()==0&&MediaQuery.needScan()){//**没有扫描媒体库
                scanMusicMedia();
            }else{
                musicListChange();
                gettingMusicLock.unlock();
            }
            return null;
        });

    }
    private void sort(){
        for (MusicList<Music> ml : musicList) {
            Collections.sort(ml.getMusicList(), (m1, m2) -> ChineseComparator.INSTANCE.compare(m1.getMusicName(),m2.getMusicName()));
        }
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
                musicListChange();
            }
            scanningMusicLock.unlock();
            return null;
        });

    }


    public static void scan(Context context){
        Intent intent=new Intent(context,MusicPlay.class);
        intent.setAction(ACTION_SCAN);
        context.startService(intent);
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

    public static void floatWindowChange(Context ctx){
        Intent intent = new Intent(ctx, MusicPlay.class);
        intent.setAction(MusicPlay.ACTION_FLOAT_WINDOW_CHANGE);
        ctx.startService(intent);
    }

    public static void musicDownloadComplete(Context ctx,String path){
        Intent intent = new Intent(ctx, MusicPlay.class);
        intent.setAction(MusicPlay.ACTION_DOWNLOAD_COMPLETE);
        intent.putExtra("path", path);
        ctx.startService(intent);
    }
}

