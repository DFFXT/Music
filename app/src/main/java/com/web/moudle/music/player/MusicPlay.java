package com.web.moudle.music.player;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.web.adpter.PlayInterface;
import com.web.common.base.MyApplication;
import com.web.common.constant.Constant;
import com.web.common.tool.MToast;
import com.web.common.util.ResUtil;
import com.web.config.GetFiles;
import com.web.config.MyNotification;
import com.web.config.Shortcut;
import com.web.data.InternetMusic;
import com.web.data.InternetMusicInfo;
import com.web.data.Music;
import com.web.data.MusicGroup;
import com.web.data.MusicList;
import com.web.data.PlayerConfig;
import com.web.data.ScanMusicType;
import com.web.moudle.lockScreen.receiver.LockScreenReceiver;
import com.web.moudle.musicDownload.service.FileDownloadService;
import com.web.moudle.preference.SP;
import com.web.web.R;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MusicPlay extends MediaBrowserServiceCompat {
	public final static String BIND="not media bind";
	public final static String ACTION_NEXT="com.web.web.MusicPlay.icon_next_black";
	public final static String ACTION_PRE="com.web.web.MusicPlay.icon_pre_black";
	public final static String ACTION_STATUS_CHANGE="com.web.web.MusicPlay.statusChange";
	public final static String ACTION_DOWNLOAD_COMPLETE="com.web.web.MusicPlay.downloadComplete";
	public final static String ACTION_ClEAR_ALL_MUSIC="clearAllMusic";
	public final static String ACTION_LOCKSCREEN="ACTION_OpenLockScreen";

	public final static String COMMAND_GET_CURRENT_POSITION="getCurrentPosition";
	public final static String COMMAND_GET_STATUS="getStatus";
	public final static String COMMAND_SEND_SINGLE_DATA="sendCurrentPosition";
	public final static int COMMAND_RESULT_CODE_CURRENT_POSITION=1;//**result code 标识为当前播放时间
	public final static int COMMAND_RESULT_CODE_STATUS=2;//**result code 标识为播放状态

	private MediaPlayer player=new MediaPlayer();
	private MyNotification notification=new MyNotification(this);


	private List<MusicList<Music>> musicList=new ArrayList<>();//**音乐列表
	private List<Music> waitMusic=new ArrayList<>();//***等待播放的音乐
	private int waitIndex=0;
	private PlayInterface play;//**界面接口
	private int groupIndex=-1,childIndex=-1;
	private Connect connect;//***连接
	private LockScreenReceiver lockScreenReceiver;
    private MediaSessionCompat sessionCompat;

	private ThreadPoolExecutor executor;
	private PlayerConfig config=new PlayerConfig();
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
                super.onPlay();
                connect.changePlayerPlayingStatus();
            }

			@Override
			public void onPause() {
				super.onPause();
                musicPause();
			}

			@Override
			public void onSkipToNext() {
				super.onSkipToNext();
                connect.next();
			}

			@Override
			public void onSkipToPrevious() {
				super.onSkipToPrevious();
                connect.pre();
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

        if(!SP.INSTANCE.getBoolean(Constant.spName,Constant.SpKey.noLockScreen)){
			lockScreen();
		}
	}
	public void onDestroy() {//--移除notification
		unLockScreen();
		stopForeground(true);
	}
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
			player.setOnPreparedListener(mp -> musicLoad());
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


		public void setPlayInterface(PlayInterface play){
			MusicPlay.this.play=play;
		}
		public void getList(){
			if(musicList.size()==0)
				new Thread(MusicPlay.this::getMusicList).start();
			else play.musicListChange(musicList);
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
			if(canPlay()){
				Music music=config.getMusic();
				play.load(groupIndex,childIndex,music,player.getDuration());
				if(!player.isPlaying()){
					play.pause();
				}
			}
			if(groupIndex!=-1&&childIndex!=-1){
				play.currentTime(groupIndex,childIndex,player.getCurrentPosition());
			}

			play.musicOriginChanged(config.getMusicOrigin());
			play.playTypeChanged(config.getPlayType());

		}
		public PlayerConfig getConfig(){
			return config;
		}
		@Nullable
		public Music getPlayingMusic(){
			return config.getMusic();
		}
		public void download(InternetMusic music){
			String originMusicName=music.getMusicName();
			for(int i=1;i<10;i++){//***音乐查重
				Music localMusic=DataSupport.where("path=?",Shortcut.createPath(music)).findFirst(Music.class);
				if(localMusic!=null){
					File file=new File(Shortcut.createPath(music));
					if(file.exists()&&file.isFile()){//**存在记录，有文件
						if(music.getFullSize()==file.length()){
							//**文件完全相同
							return;
						}
						else {//**文件不同
							music.setMusicName(originMusicName+"("+i+")");
						}
					}
					else {//**存在记录，文件丢失
						music.setMusicName(localMusic.getPath());
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
		public void playInternet(final InternetMusicInfo music){
			config.setMusicOrigin(PlayerConfig.MusicOrigin.INTERNET);
			loadMusic(music);
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
		public boolean isPlay(){
			return player.isPlaying();
		}

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

	/**
	 * 加载音乐
	 * @param music music
	 */
	private void loadMusic(Music music){
		config.setHasInit(true);
		config.setMusic(music);
		player.reset();
		try {
			player.setDataSource(music.getPath());
			player.prepareAsync();
		} catch (IOException e) {//***播放异常，如果是文件不存在则删除记录
			if(config.getMusicOrigin()== PlayerConfig.MusicOrigin.LOCAL){
				deleteMusic(groupIndex,childIndex,Shortcut.fileExsist(music.getPath()));
				connect.next();
			}
			else if(config.getMusicOrigin()== PlayerConfig.MusicOrigin.WAIT){
				connect.next();
			}else if(config.getMusicOrigin()==PlayerConfig.MusicOrigin.STORAGE){
				MToast.showToast(this,ResUtil.getString(R.string.cannotPlay));
			}
		}
		play.musicOriginChanged(config.getMusicOrigin());
		MediaMetadataCompat data=new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,music.getMusicName())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,music.getSinger())
				.putString(MediaMetadataCompat.METADATA_KEY_COMPOSER,music.getLyricsPath())
                .build();
		sessionCompat.setMetadata(data);
	}
	public void musicPlay(){
		player.start();
		Music music=config.getMusic();
		play.play();

		notification.setName(music.getMusicName());
		notification.setSinger(music.getSinger());
		notification.setPlayStatus(true);
		notification.setBitMap(getBitmap(music.getSinger()));
		notification.show();
		executor.execute(() -> {
			while(player.isPlaying()){
				if(play!=null){
					play.currentTime(groupIndex,childIndex,player.getCurrentPosition());
					sendDuring(player.getCurrentPosition());
					Shortcut.sleep(500);
				}
			}
		});
        sendState(PlaybackStateCompat.STATE_PLAYING);
	}
	public void musicLoad(){
		player.start();
		Music music=config.getMusic();
		notification.setName(music.getMusicName());
		notification.setSinger(music.getSinger());
		notification.setPlayStatus(true);
		notification.setBitMap(getBitmap(music.getSinger()));
		notification.show();
		play.load(groupIndex,childIndex,music,player.getDuration());
		executor.execute(() -> {
			while(player.isPlaying()){
				if(play!=null){
					play.currentTime(groupIndex,childIndex,player.getCurrentPosition());
					sendDuring(player.getCurrentPosition());
					Shortcut.sleep(500);
				}
			}
		});

        sendState(PlaybackStateCompat.STATE_PLAYING);
	}
	private void musicPause(){
		player.pause();
		play.pause();
		notification.setPlayStatus(false);
		notification.show();
        sendState(PlaybackStateCompat.STATE_PAUSED);
	}
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
	 * @param child child
	 * @param deleteFile 是否删除源文件
	 */
	private void deleteMusic(int group,int child,boolean deleteFile){
		if(!canPlay())return;
		Music music=musicList.get(group).get(child);
		if (deleteFile){
			Shortcut.fileDelete(music.getPath());
		}
		music.delete();
		musicList.get(group).remove(child);
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
				play.musicListChange(musicList);
			}break;
			case ACTION_ClEAR_ALL_MUSIC:{
				DataSupport.deleteAll(Music.class);
				DataSupport.deleteAll(MusicGroup.class);
				SP.INSTANCE.putValue(Constant.spName,Constant.SpKey.clearAll,true);
				new Thread(this::getMusicList).start();
                reset();
            }break;
            case ACTION_LOCKSCREEN:{
                boolean noLock=SP.INSTANCE.getBoolean(Constant.spName,Constant.SpKey.noLockScreen);
                if(noLock){
					unLockScreen();
				}else {
					lockScreen();
				}
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

	private void reset(){
		play.load(-1,-1,null,0);
		play.currentTime(0,0,0);
		play.musicOriginChanged(PlayerConfig.MusicOrigin.LOCAL);
		play.pause();
		config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
		config.setMusic(null);
		waitMusic.clear();
		waitIndex=0;
	}

	/**
	 * 获取本地列表
	 */
	@WorkerThread
	private void getMusicList(){
		List<MusicGroup> musicGroups =DataSupport.findAll(MusicGroup.class);
		musicList.clear();
		//**获取默认列表的歌曲
		List<Music> defList=DataSupport.findAll(Music.class);
		/*if(defList.size()==0&&!SP.INSTANCE.getBoolean(Constant.spName,Constant.SpKey.clearAll)){
			scanMusicMedia();
		}*/
		MusicList<Music> defGroup=new MusicList<>("默认");
		defGroup.addAll(defList);
		musicList.add(defGroup);
		//**获取自定义列表的歌曲
		for (MusicGroup musicGroup : musicGroups) {
			MusicList<Music> list=new MusicList<>(musicGroup.getGroupName());
			List<Music> musics=DataSupport.where("groupId=?", musicGroup.getGroupId()+"").find(Music.class);
			list.addAll(musics);
			if(list.size()!=0)
				musicList.add(list);
		}
		play.musicListChange(musicList);
	}
	@WorkerThread
	private void scanMusicMedia(){
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
				if(path.endsWith(type.getScanSuffix())&&size>=type.getMinFileSize()){
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
	}

}

