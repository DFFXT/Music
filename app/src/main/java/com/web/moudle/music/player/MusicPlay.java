package com.web.moudle.music.player;

import android.arch.lifecycle.LifecycleOwner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import com.web.common.base.BaseSingleObserver;
import com.web.common.base.MyApplication;
import com.web.common.constant.Constant;
import com.web.common.tool.MToast;
import com.web.common.util.IOUtil;
import com.web.common.util.ResUtil;
import com.web.config.GetFiles;
import com.web.config.Shortcut;
import com.web.data.InternetMusicDetail;
import com.web.data.InternetMusicForPlay;
import com.web.data.Music;
import com.web.data.MusicGroup;
import com.web.data.MusicList;
import com.web.data.PlayerConfig;
import com.web.data.ScanMusicType;
import com.web.moudle.lockScreen.receiver.LockScreenReceiver;
import com.web.moudle.music.player.bean.SongSheet;
import com.web.moudle.musicDownload.service.FileDownloadService;
import com.web.moudle.notification.MyNotification;
import com.web.moudle.preference.SP;
import com.web.moudle.setting.lockscreen.LockScreenSettingActivity;
import com.web.web.R;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MusicPlay extends MediaBrowserServiceCompat {
	public final static String BIND="not media bind";
	public final static String ACTION_PLAY_INTERNET_MUSIC="com.web.web.MusicPlay.playInternetMusic";
	public final static String ACTION_NEXT="com.web.web.MusicPlay.icon_next_black";
	public final static String ACTION_PRE="com.web.web.MusicPlay.icon_pre_black";
	public final static String ACTION_STATUS_CHANGE="com.web.web.MusicPlay.statusChange";
	public final static String ACTION_DOWNLOAD_COMPLETE="com.web.web.MusicPlay.downloadComplete";
	public final static String ACTION_ClEAR_ALL_MUSIC="clearAllMusic";
	public final static String ACTION_LOCKSCREEN="ACTION_OpenLockScreen";

	public final static String COMMAND_GET_CURRENT_POSITION="getCurrentPosition";
	public final static String COMMAND_GET_STATUS="getStatus";
	public final static String COMMAND_SEND_SINGLE_DATA="translateSingleData";
	public final static int COMMAND_RESULT_CODE_CURRENT_POSITION=1;//**result code 标识为当前播放时间
	public final static int COMMAND_RESULT_CODE_STATUS=2;//**result code 标识为播放状态

	private MediaPlayer player=new MediaPlayer();
	private MyNotification notification=new MyNotification(this);


	private List<MusicList<Music>> musicList=new ArrayList<>();//**音乐列表
	private List<Music> waitMusic=new ArrayList<>();//***等待播放的音乐
	private int waitIndex=0;
	//@Nullable
	//private PlayInterface play;//**界面接口
	private PlayInterfaceManager play=new PlayInterfaceManager();
	private int groupIndex=0,childIndex=-1;
	private Connect connect;
	private LockScreenReceiver lockScreenReceiver;
    private MediaSessionCompat sessionCompat;

	private ThreadPoolExecutor executor;
	private PlayerConfig config=new PlayerConfig();
	//********耳塞插拔广播接收
	private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if(!config.isHasInit())return;
            if(intent.getIntExtra("state",0)==0){
                musicPause();
            }else {
            	musicPlay();
			}
        }
    };
	//*******来电监听器
	private PhoneStateListener phoneStateListener=new PhoneStateListener(){
		@Override
		public void onCallStateChanged(int state, String phoneNumber) {
			if(!config.isHasInit())return;
			switch (state){
				case TelephonyManager.CALL_STATE_RINGING:{
					musicPause();
				}break;
				case TelephonyManager.CALL_STATE_IDLE:{
					musicPlay();
				}break;
			}
		}
	};
	@Override
	public IBinder onBind(Intent arg0) {
		if(BIND.equals(arg0.getAction())){
			if(connect==null)connect=new Connect();
			return connect;
		}
		return super.onBind(arg0);
	}

	@Nullable
	@Override
	public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
		return new BrowserRoot("root",null);
	}

	@Override
	public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
		result.sendResult(new ArrayList<>());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		executor=new ThreadPoolExecutor(1,1,1, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        sessionCompat=new MediaSessionCompat(this,"2");
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

			private long preEventTime=0;
            private long preHookTime=0;
			@Override
			public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
				if(System.currentTimeMillis()-preEventTime<50)return false;
				preEventTime=System.currentTimeMillis();
                KeyEvent event=mediaButtonEvent.getParcelableExtra("android.intent.extra.KEY_EVENT");
				switch (event.getKeyCode()){
					case KeyEvent.KEYCODE_MEDIA_NEXT:{
						onSkipToNext();
					}break;
					case KeyEvent.KEYCODE_MEDIA_PREVIOUS:{
						onSkipToPrevious();
					}break;
					case KeyEvent.KEYCODE_HEADSETHOOK:{
						if(System.currentTimeMillis()-preHookTime<100)return false;
						else if(System.currentTimeMillis()-preHookTime<800){
							onSkipToNext();
						}else {
							connect.changePlayerPlayingStatus();
						}
						preHookTime=System.currentTimeMillis();
					}break;
				}
				return true;
			}

			@Override
			public void onCommand(String command, Bundle extras, ResultReceiver cb) {
				Bundle bundle=new Bundle();
            	switch (command){
					case COMMAND_GET_CURRENT_POSITION:{
						if(!config.isHasInit())return;
						bundle.putInt(COMMAND_SEND_SINGLE_DATA,player.getCurrentPosition());
						cb.send(COMMAND_RESULT_CODE_CURRENT_POSITION,bundle);

					}break;
					case COMMAND_GET_STATUS:{
						bundle.putBoolean(COMMAND_SEND_SINGLE_DATA,player.isPlaying());
						cb.send(COMMAND_RESULT_CODE_STATUS,bundle);
					}break;
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

        if(!SP.INSTANCE.getBoolean(Constant.spName,Constant.SpKey.noLockScreen,false)){
			lockScreen();
		}
		//**耳塞插拔注册广播
		IntentFilter filter=new IntentFilter();
        filter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(headsetReceiver,filter);

        //**来电注册监听
		TelephonyManager manager=getSystemService(TelephonyManager.class);
		manager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);

	}
	public void onDestroy() {//--移除notification、取消注册、监听
		unLockScreen();
		unregisterReceiver(headsetReceiver);
		getSystemService(TelephonyManager.class).listen(phoneStateListener,PhoneStateListener.LISTEN_NONE);
		stopForeground(true);
	}

    /**
     * 激活锁屏
     */
	private void lockScreen(){
		IntentFilter filter=new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(lockScreenReceiver=new LockScreenReceiver(),filter);
	}
	private void unLockScreen(){
		if(lockScreenReceiver!=null){
			unregisterReceiver(lockScreenReceiver);
			lockScreenReceiver=null;
		}
	}

	public class Connect extends Binder {
		Connect(){
			player.setLooping(false);
			player.setOnPreparedListener(mp -> {
				config.setPrepared(true);
				musicLoad();
			});
			player.setOnCompletionListener(mp -> {
                switch (config.getPlayType()){
                    //**列表循环
                    case ALL_LOOP:{
                    	if(config.getMusicOrigin()== PlayerConfig.MusicOrigin.WAIT){//***播放准备音乐
							loadNextWait();
						}
                    	else next();
					}break;
                    //**单曲循环
                    case ONE_LOOP:{
                        player.seekTo(0);
                        musicPlay();
                    }break;
                    //**列表不循环
                    case ALL_ONCE:{
						if(config.getMusicOrigin()== PlayerConfig.MusicOrigin.WAIT){//***播放准备音乐
							if(waitIndex<waitMusic.size()){
								loadNextWait();
							}
						}
                        else if(childIndex<musicList.get(groupIndex).size()-1){
							next();
                        }else{//***暂停
							musicPause();
						}
                    }break;
                    //**单曲不循环
                    case ONE_ONCE:{
						musicPause();
                    }break;
                }

            });

		}



		/***
		 * 每隔一秒获取当前时间
		 */


		public void addObserver(LifecycleOwner owner,PlayInterface play){
			MusicPlay.this.play.addObserver(owner,play);
		}
		public void getList(int group){
			groupIndex=group;
			if(musicList.size()==0)
				new Thread(MusicPlay.this::getMusicList).start();
			else {
				musicListChange();
			}
		}
		public int getCurrentPosition(){
		    return player.getCurrentPosition();
        }
		public void scanLocalMusic(){
			new Thread(()->{
				scanMusicMedia();
				getMusicList();
			}).start();
		}
		public void musicSelect(int group,int child){
            if(groupIndex!=group||child!=childIndex){
                play(group,child);
            }else if(player.isPlaying()) {
                musicPause();
            }else {
                musicPlay();
            }
        }
		/**
		 * 控制音乐
		 * @param group group
		 * @param child child
		 */
		private void play(int group,int child){
			if(groupIndex!=group||child!=childIndex){
				groupIndex=group;
				childIndex=child;
				waitMusic.clear();
				config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
				loadMusic(musicList.get(groupIndex).get(childIndex));
			}else {
				player.seekTo(0);
				musicPlay();
			}
		}

		/**
		 * 播放下一首
		 */
		public void next(){

			waitMusic.clear();
			config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
		    int nextChildIndex=1+childIndex;
		    if(!canPlay(groupIndex,nextChildIndex)){
		        if(groupIndex<0) groupIndex=0;
		        if(nextChildIndex<0) nextChildIndex=0;
		        if(nextChildIndex>=musicList.get(groupIndex).size()) nextChildIndex=0;
		        if(nextChildIndex<musicList.get(groupIndex).size()) {
		        	play(groupIndex,nextChildIndex);
				}

            }
			else if(config.getMusicOrigin()== PlayerConfig.MusicOrigin.LOCAL){
				play(groupIndex,childIndex+1);
			}
			//**在线播放下一首---------当做本地播放
			else if(config.getMusicOrigin()== PlayerConfig.MusicOrigin.INTERNET){
		    	config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
				play(groupIndex,childIndex+1);
			}
		}

		/**
		 * 播放前一曲
		 */
		public void pre(){
			waitMusic.clear();
			config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
		    int preChildIndex=childIndex-1;
			if(!canPlay(groupIndex,preChildIndex)){
			    if(groupIndex<0) groupIndex=0;
                if(preChildIndex<0){
                    preChildIndex=musicList.get(groupIndex).size()-1;
			        if (preChildIndex>=0) play(groupIndex,preChildIndex);
                }
            }
			else{
				play(groupIndex,childIndex-1);
			}
		}
		//***切换播放状态
		public void changePlayerPlayingStatus(){
            if(player.isPlaying()){
                musicPause();
            }else{
                if(config.getMusic()!=null){
                    musicPlay();
                }else {
                    next();
                }
            }


		}
		public List<Music> getWaitMusic(){
			return waitMusic;
		}
		public boolean addToWait(Music music){
			for(Music m:waitMusic){
				if(m.getPath().equals(music.getPath()))return false;
			}
			config.setMusicOrigin(PlayerConfig.MusicOrigin.WAIT);
			waitMusic.add(music);
			if(waitMusic.size()==1){//**添加了播放列表，第一个添加的直接抢占播放
				waitIndex=0;
				loadNextWait();
			}
			return true;
		}
		public void groupChange(){
			new Thread(MusicPlay.this::getMusicList).start();
		}
		public int getWaitIndex(){
			return  waitIndex;
		}
		public void removeWait(int index){
			if(index<0||index>waitMusic.size())return;
			if(index==0&&waitMusic.size()==1){//***移除最后一个
				next();
				return;
			}
			if(index<waitIndex){//**移除播放之前的
				waitIndex--;
				waitMusic.remove(index);
			}else if(index>waitIndex&&index<waitMusic.size()){//**移除之后播放的
				waitMusic.remove(index);
			}
			else if(index==waitIndex){//**移除正在播放的
				waitIndex--;
				waitMusic.remove(index);
				loadNextWait();
			}
		}
		public void seekTo(int millis){
		    if(canPlay()) {
				player.seekTo(millis);
				play.currentTime(groupIndex,childIndex,millis);
			}
		}
		public void getPlayerInfo(){
			disPatchMusicInfo();
		}
		public PlayerConfig getConfig(){
			return config;
		}
		@Nullable
		public Music getPlayingMusic(){
			return config.getMusic();
		}
		public void download(InternetMusicDetail music){
			String originMusicName=music.getSongName();
			for(int i=1;i<10;i++){//***音乐查重
				Music localMusic=DataSupport.where("path=?",Shortcut.createPath(music)).findFirst(Music.class);
				if(localMusic!=null){
					File file=new File(Shortcut.createPath(music));
					if(file.exists()&&file.isFile()){//**存在记录，有文件
						if(music.getSize()==file.length()){
							//**文件完全相同
							return;
						}
						else {//**文件不同
							music.setSongName(originMusicName+"("+i+")");
						}
					}
					else {//**存在记录，文件丢失
						music.setSongName(localMusic.getPath());
						music.setPath(localMusic.getPath());
						break;
					}
				}else {//**没有重复记录
					music.setPath(Shortcut.createPath(music));
				}
			}
			FileDownloadService.addTask(MusicPlay.this,music);
		}

		/**
		 * 播放在线音乐
		 * @param music music
		 */
		public void playInternet(final InternetMusicForPlay music){
			config.setMusicOrigin(PlayerConfig.MusicOrigin.INTERNET);
			loadMusic(music);
			//**下载歌词和图片
            Single.create(emitter -> {
                if(!Shortcut.fileExsist(music.getLyricsPath())){
                    IOUtil.onlineDataToLocal(music.getLrcLink(),music.getLyricsPath());
                }
                if(!Shortcut.fileExsist(music.getIconPath())){
                    IOUtil.onlineDataToLocal(music.getImgAddress(),music.getIconPath());
                    config.setBitmap(getBitmap(music.getSinger()));
                }
                emitter.onSuccess(1);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSingleObserver<Object>(){
                        @Override
                        public void onSuccess(Object obj) {
                        	if(config.isPrepared())//**必须在准备好了才能start和getDuration
                            	musicLoad();
                        }
					});
		}

		/**
		 * 播放本地音乐
		 * @param music music
		 */
		public void playLocal(Music music){
		    config.setMusic(music);
			config.setMusicOrigin(PlayerConfig.MusicOrigin.STORAGE);
			loadMusic(music);
		}
		public void playWait(int index){
			if(index>=0&&index<waitMusic.size()){
				waitIndex=index;
				loadMusic(waitMusic.get(index));
			}
		}

		public void delete(boolean deleteFile,int groupIndex,int...childIndex){
			MusicPlay.this.deleteMusic(deleteFile,groupIndex,childIndex);
		}
		public void delete(boolean deleteFile,int groupIndex,List<Integer> childList){
			int list[]=new int[childList.size()];
			for(int i=0;i<list.length;i++){
				list[i]=childList.get(i);
			}
			delete(deleteFile,groupIndex,list);
		}

		/**
		 * 取消连接
		public void cancel(){
			play=null;
		}*/
	}

	/**
	 * 判定当前歌曲是否能播放
	 * @return bool
	 */
	private boolean canPlay(int group,int child) {
		if(group>=0&&child>=0){
			if(group<musicList.size()){
                return child < musicList.get(group).size();
			}
		}
		return false;
	}
	private boolean canPlay(){
	    return canPlay(groupIndex,childIndex);
    }

	/**
	 * 加载等待歌曲
	 */
	public void loadNextWait(){
		waitIndex++;
		if(waitMusic.size()<=waitIndex) waitIndex=0;
		loadMusic(waitMusic.get(waitIndex));

	}


	private MediaMetadataCompat.Builder metaDataBuilder=new MediaMetadataCompat.Builder();
	/**
	 * 加载音乐
	 * @param music music
	 */
	private void loadMusic(Music music){
		config.setHasInit(true);
		config.setMusic(music);
		config.setBitmap(getBitmap(music.getSinger()));
		player.reset();
		try {
			config.setPrepared(false);
			player.setDataSource(music.getPath());
			player.prepareAsync();
		} catch (IOException e) {//***播放异常，如果是文件不存在则删除记录
			if(config.getMusicOrigin()== PlayerConfig.MusicOrigin.LOCAL){
				deleteMusic(false,groupIndex, childIndex);
				connect.next();
			}
			else if(config.getMusicOrigin()== PlayerConfig.MusicOrigin.WAIT){
				connect.next();
			}else if(config.getMusicOrigin()==PlayerConfig.MusicOrigin.STORAGE){
				MToast.showToast(this,ResUtil.getString(R.string.cannotPlay));
			}
		}
		play.musicOriginChanged(config.getMusicOrigin());

		metaDataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE,music.getMusicName())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,music.getSinger())
				.putString(MediaMetadataCompat.METADATA_KEY_COMPOSER,music.getLyricsPath());

		sessionCompat.setMetadata(metaDataBuilder.build());
	}
	public void musicPlay(){

		//player.setPlaybackParams(player.getPlaybackParams().setSpeed(2));
		Music music=config.getMusic();
		if(music==null)return;
		play.play();
		player.start();
		notification.setName(music.getMusicName());
		notification.setSinger(music.getSinger());
		notification.setPlayStatus(true);
		notification.setBitMap(config.getBitmap());
		notification.show();
		executor.execute(() -> {
			while(player.isPlaying()){
				play.currentTime(groupIndex,childIndex,player.getCurrentPosition());
				sendDuring(player.getCurrentPosition());
				Shortcut.sleep(500);
			}
		});
        sendState(PlaybackStateCompat.STATE_PLAYING);
	}
	public void musicLoad(){
		play.load(groupIndex,childIndex,config.getMusic(),getDuration());
		musicPlay();
	}

	private int getDuration(){
		if(!config.isPrepared())return 0;
		return player.getDuration();
	}
	private void musicPause(){
		player.pause();
        play.pause();
		notification.setPlayStatus(false);
		notification.show();
        sendState(PlaybackStateCompat.STATE_PAUSED);
	}

	/**
	 * 发送播放器状态
	 * @param state state
	 */
	private void sendState(int state){
        PlaybackStateCompat stateCompat=new PlaybackStateCompat.Builder()
                .setState(state,1,1)
				.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
                .build();
        sessionCompat.setPlaybackState(stateCompat);

    }
    private Bundle bundle=new Bundle();
	public final static String MUSIC_DURING="md";
	private void sendDuring(int during){
		bundle.putInt(MUSIC_DURING,during);
		PlaybackStateCompat stateCompat=new PlaybackStateCompat.Builder()
				.setExtras(bundle)
				.setActions(PlaybackStateCompat.ACTION_SEEK_TO)
				.build();
		sessionCompat.setPlaybackState(stateCompat);
	}

	/**
	 * 删除音乐记录
	 * @param group group
	 * @param childList child
	 * @param deleteFile 是否删除源文件
	 */
	private void deleteMusic(boolean deleteFile,int group,int...childList){
		List<Music> deleteList=new ArrayList<>();
		for(int child:childList){
			if(!canPlay(group,child))return;
			Music music=musicList.get(group).get(child);
			deleteList.add(music);
			if(music==config.getMusic()){
				reset();
			}
			if (deleteFile){//******删除源文件并更新媒体库
				Music.deleteMusic(music);
				getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,"_data=?",new String[]{music.getPath()});
			}


			if(group==0||deleteFile){//**影响group 0
				List<SongSheet> list=SongSheetManager.INSTANCE.getSongSheetList().getSongList();
				for(SongSheet songSheet:list){
					songSheet.remove(music.getId());
				}
			}else {
				SongSheetManager.INSTANCE.getSongSheetList().getSongList().get(group).remove(music.getId());
				SongSheetManager.INSTANCE.getSongSheetList().save();
			}
			music.delete();
		}
		for(Music m:deleteList){
			musicList.get(group).remove(m);
		}


		new Thread(MusicPlay.this::getMusicList).start();
	}


	//--[icon_next_black:对歌曲进行了点击;init:初始化界面;task:下载请求;delFile:删除某个歌曲;getNewList:重新扫描歌曲;seekTo:进度跳转]
	@Override
	public int onStartCommand(Intent intent,int flags,int startId){
		String action= intent.getAction();
		if (action==null)return START_NOT_STICKY;
		switch (action){
			case ACTION_NEXT:connect.next();break;
			case ACTION_PRE:connect.pre();break;
			case ACTION_STATUS_CHANGE:connect.changePlayerPlayingStatus();break;
			case ACTION_DOWNLOAD_COMPLETE:{
				Music music=DataSupport.where("path=?",
						intent.getStringExtra("path")).findFirst(Music.class);
				musicList.get(0).add(music);
				musicListChange();
			}break;
			case ACTION_ClEAR_ALL_MUSIC:{
				DataSupport.deleteAll(Music.class);
				DataSupport.deleteAll(MusicGroup.class);
				SP.INSTANCE.putValue(Constant.spName,Constant.SpKey.clearAll,true);
				new Thread(this::getMusicList).start();
                reset();
            }break;
            case ACTION_LOCKSCREEN:{
                boolean noLock=LockScreenSettingActivity.Companion.getNoLockScreen();
                if(noLock){
					unLockScreen();
				}else {
					lockScreen();
				}
			}break;
			case ACTION_PLAY_INTERNET_MUSIC:{
				connect.playInternet((InternetMusicForPlay) intent.getSerializableExtra(MusicPlay.COMMAND_SEND_SINGLE_DATA));
			}break;
		}
		return START_NOT_STICKY;
		/*
		 * return START_NOT_STICKY;
		 “非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。
		 否则就会出现重新启动失败
		 */
	}



	public Bitmap getBitmap(String singerName){//--加载本地音频,图片
		Bitmap bitmap=null;
		try {
			File file = new File(GetFiles.singerPath+singerName+".png");
			if(file.exists()&&file.isFile()){//---存在图片
				FileInputStream fis=new FileInputStream(file);
				bitmap=BitmapFactory.decodeStream(fis);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	private void musicListChange(){
		play.musicListChange(groupIndex,musicList);
	}
	//**分发信息
	private void disPatchMusicInfo(){
		Music music=config.getMusic();
		play.load(groupIndex,childIndex,music,getDuration());
		if(!player.isPlaying()){
			play.pause();
		}
		if(groupIndex!=-1&&childIndex!=-1){
			play.currentTime(groupIndex,childIndex,player.getCurrentPosition());
		}

		play.musicOriginChanged(config.getMusicOrigin());
		play.playTypeChanged(config.getPlayType());
	}
	private void reset(){
		player.reset();
		play.load(-1,-1,null,0);
		play.currentTime(0,0,0);
		play.musicOriginChanged(PlayerConfig.MusicOrigin.LOCAL);
		play.pause();
		config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
		config.setMusic(null);
		waitMusic.clear();
		waitIndex=0;
	}

	private Lock gettingMusicLock =new ReentrantLock();
	private Lock scanningMusicLock =new ReentrantLock();
	/**
	 * 获取本地列表
	 */
	@WorkerThread
	private void getMusicList(){
		if(!gettingMusicLock.tryLock()){
			return;
		}
		musicList.clear();
		//**获取默认列表的歌曲
		List<Music> defList=DataSupport.findAll(Music.class);

		MusicList<Music> defGroup=new MusicList<>("默认");
		defGroup.addAll(defList);
		musicList.add(defGroup);
		//**获取自定义列表的歌曲

		List<SongSheet> sheetList=SongSheetManager.INSTANCE.getSongSheetList().getSongList();
		for(SongSheet songSheet:sheetList){
			MusicList<Music> group=new MusicList<>(songSheet.getName());
			songSheet.each((id)->{
				for(Music m:defList){
					if(m.getId()==id){
						group.add(m);
					}
				}
				return null;
			});
			musicList.add(group);
		}
		String chinese="[\\u4e00-\\u9fa5+]";
		String code="[a-zA-Z]";
		for(MusicList<Music> ml:musicList){
			Collections.sort(ml.getMusicList(),(m1,m2)-> {
				String n1=m1.getMusicName().substring(0,1);
				String n2=m2.getMusicName().substring(0,1);
				boolean valid1=n1.matches(chinese+"|"+code);//**是否是中英文
				boolean valid2=n2.matches(chinese+"|"+code);
				if(valid1&&valid2){//***中英文
					String c1=n1;
					String c2=n2;
					if(n1.matches(chinese)){//**中文
						c1=PinyinHelper.toHanyuPinyinStringArray(n1.charAt(0))[0];
					}
					if(n2.matches(chinese)){
						c2=PinyinHelper.toHanyuPinyinStringArray(n2.charAt(0))[0];
					}
					return Collator.getInstance(Locale.CHINA).compare(c1,c2);
				}else if(valid1){
					return -1;
				}else if(valid2){
					return 1;
				}else {
					return n1.charAt(0)-n2.charAt(0);
				}

			});
		}
		musicListChange();

		gettingMusicLock.unlock();
	}
	@WorkerThread
	private void scanMusicMedia(){
		if(!scanningMusicLock.tryLock()){
			return;
		}
		SP.INSTANCE.putValue(Constant.spName,Constant.SpKey.noNeedScan,true);
		Cursor cursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
		if(cursor==null)return;
		List<ScanMusicType> types=DataSupport.findAll(ScanMusicType.class);
		if(types.size()==0){//**如果扫描设置不存在，就采用默认扫描设置
			String suffix[]=new String[]{".mp3",".ogg",".wav"};
			for(String str:suffix){
				ScanMusicType scanMusicType=new ScanMusicType(str,1024*50,true);
				scanMusicType.save();
				types.add(scanMusicType);
			}
		}
		String[] out=new String[2];
		while (cursor.moveToNext()){
			int index=cursor.getColumnIndex("_data");
			String path=cursor.getString(index);
			index=cursor.getColumnIndex("_size");
			int size=cursor.getInt(index);



			for(ScanMusicType type:types){
				if(!type.isScanable())continue;
				if(path!=null&&path.endsWith(type.getScanSuffix())&&size>=type.getMinFileSize()){
					int lastSeparatorChar = path.lastIndexOf(File.separatorChar);
					//**文件名包含后缀
					String fileName=path;
					if (lastSeparatorChar >=0){
						fileName = path.substring(lastSeparatorChar+1);
					}
					Shortcut.getName(out,fileName);
					//**去后缀
					int lastIndex=out[0].lastIndexOf('.');
					Music music=new Music(out[0].substring(0,lastIndex),out[1],path);
					index=cursor.getColumnIndex("duration");
					music.setDuration(cursor.getInt(index));
					index=cursor.getColumnIndex("_id");
					music.setSong_id(cursor.getInt(index));
					index=cursor.getColumnIndex("album_id");
					music.setAlbum_id(cursor.getInt(index));
					Music m=DataSupport.where("path=?",music.getPath()).findFirst(Music.class);
					if(m==null){
						music.save();
					}else {//***更新保持groupID不变
						music.setGroupId(m.getGroupId());
						music.setId(m.getId());
						music.update(m.getId());
					}
					break;
				}
			}
		}
		cursor.close();
		AndroidSchedulers.mainThread().scheduleDirect(()-> MToast.showToast(MyApplication.context,ResUtil.getString(R.string.scanOver)));
		scanningMusicLock.unlock();
	}


	/**
	 * 播放网络音乐
	 * @param ctx context
	 * @param music music
	 */
	public static void play(Context ctx,InternetMusicForPlay music){
		Intent intent=new Intent(ctx,MusicPlay.class);
		intent.putExtra(MusicPlay.COMMAND_SEND_SINGLE_DATA,music);
		intent.setAction(MusicPlay.ACTION_PLAY_INTERNET_MUSIC);
		ctx.startService(intent);
	}
}

