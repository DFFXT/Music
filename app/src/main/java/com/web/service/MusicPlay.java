package com.web.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.web.adpter.PlayInterface;
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
import com.web.moudle.musicDownload.service.FileDownloadService;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MusicPlay extends Service {
	public static String ACTION_NEXT="com.web.web.MusicPlay.next";
	public static String ACTION_PRE="com.web.web.MusicPlay.pre";
	public static String ACTION_STATUS_CHANGE="com.web.web.MusicPlay.statusChange";
	public static String ACTION_DOWNLOAD_COMPLETE="com.web.web.MusicPlay.downloadComplete";
	private MediaPlayer player=new MediaPlayer();
	private MyNotification notification=new MyNotification(this);


	private List<MusicList<Music>> musicList=new ArrayList<>();//**音乐列表
	private List<Music> waitMusic=new ArrayList<>();//***等待播放的音乐
	private int waitIndex=0;
	private PlayInterface play;//**界面接口
	private int groupIndex=-1,childIndex=-1;
	private Connect connect;//***连接

	private ThreadPoolExecutor executor;
	private PlayerConfig config=new PlayerConfig();
	@Override
	public IBinder onBind(Intent arg0) {
		if(connect==null)connect=new Connect();
		return connect;
	}
	public class Connect extends Binder {
		Connect(){
			executor=new ThreadPoolExecutor(1,1,5, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>());
			player.setOnPreparedListener(mp -> {
                mp.start();
                Music music=null;
                switch (config.getMusicOrigin()){
                    case LOCAL:{
                        music=musicList.get(groupIndex).get(childIndex);
                        config.setMusic(music);
                    }break;
                    case INTERNET:{
                        music=config.getMusic();
                    }break;
					case WAIT:{
						music=waitMusic.get(waitIndex);
					}break;
                    case STORAGE:{
                        music=config.getMusic();
                    }break;
                }

                play.load(music.getMusicName(),music.getSinger(),player.getDuration());
                setTimeListener();
                notification.setName(music.getMusicName());
                notification.setSinger(music.getSinger());
                notification.setPlayStatus(true);
                notification.setBitMap(getBitmap(music.getSinger()));
                notification.show();
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
                        player.start();
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
							play.pause();
						}
                    }break;
                    //**单曲不循环
                    case ONE_ONCE:{
						play.pause();
                    }
                }

            });

		}



		/***
		 * 每隔一秒获取当前时间
		 */
		private void setTimeListener(){
			executor.execute(() -> {
                while(player.isPlaying()){
                    if(play!=null){
                        play.currentTime(groupIndex,childIndex,player.getCurrentPosition());
                        Shortcut.sleep(500);
                    }
                }
            });
		}

		public void setPlayInterface(PlayInterface play){
			MusicPlay.this.play=play;
		}
		public void getList(){
			if(musicList.size()==0)
				getMusicList();
			else play.musicListChange(musicList);
		}
		public void scanLocalMusic(){
			MusicPlay.this.scanMusic();
		}
		/**
		 * 控制音乐
		 * @param group group
		 * @param child child
		 */
		public void play(int group,int child){

			if(groupIndex!=group||child!=childIndex){
				groupIndex=group;
				childIndex=child;
				waitMusic.clear();
				config.setMusicOrigin(PlayerConfig.MusicOrigin.LOCAL);
				loadMusic(musicList.get(groupIndex).get(childIndex));
			}else {
				if(player.isPlaying()){//**播放--->暂停
					play.pause();
					player.pause();
				}else{//**暂停--->播放
					Music music=musicList.get(group).get(child);
					play.play(music.getMusicName(),music.getSinger(),player.getDuration());
					player.start();
					setTimeListener();//**需要重新开线程监听时间变化
				}
				notification.setPlayStatus(player.isPlaying());
				notification.show();
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
			switch (config.getMusicOrigin()){
				case LOCAL://**本地
				case INTERNET:{//**网络
					if(player.isPlaying()){
						player.pause();
						play.pause();
					}else {
						player.start();
						play.play(config.getMusic().getMusicName(),
								config.getMusic().getSinger(),player.getDuration());
						setTimeListener();
					}
				}break;
				case STORAGE:
				case WAIT:{
					if(player.isPlaying()){
						player.pause();
						play.pause();
					}else{
						player.start();
						play.play(config.getMusic().getMusicName(),
								config.getMusic().getSinger(),player.getDuration());
                        setTimeListener();
					}
					notification.setPlayStatus(player.isPlaying());
					notification.show();
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
				play.load(music.getMusicName(),music.getSinger(),player.getDuration());
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
			}
		}
		play.musicOriginChanged(config.getMusicOrigin());
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

	public void onDestroy() {//--移除notification
		stopForeground(true);
	}
	//--[next:对歌曲进行了点击;init:初始化界面;task:下载请求;delFile:删除某个歌曲;getNewList:重新扫描歌曲;seekTo:进度跳转]
	@Override
	public int onStartCommand(Intent intent,int flags,int startId){
		String action= intent.getAction();
		if (action==null)return START_NOT_STICKY;
		switch (action){
			case "com.web.web.MusicPlay.next":connect.next();break;
			case "com.web.web.MusicPlay.pre":connect.pre();break;
			case "com.web.web.MusicPlay.statusChange":connect.changePlayerPlayingStatus();break;
			case "com.web.web.MusicPlay.downloadComplete":{
				Music music=DataSupport.where("path=?",
						intent.getStringExtra("path")).findFirst(Music.class);
				musicList.get(0).add(music);
				play.musicListChange(musicList);
			}break;
		}
		return START_NOT_STICKY;
		/*
		 * return START_NOT_STICKY;
		 “非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。
		 否则就会出现重新启动失败
		 */
	}


	public void show(String str){
		Toast.makeText(MusicPlay.this, str,Toast.LENGTH_LONG).show();
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


	/**
	 * 获取本地列表
	 */
	private void getMusicList(){
		new Thread(() -> {
			LitePal.getDatabase();
            List<MusicGroup> musicGroups =DataSupport.findAll(MusicGroup.class);
            musicList.clear();
            //**获取默认列表的歌曲
            MusicList<Music> defGroup=new MusicList<>("默认");
            List<Music> defList=DataSupport.findAll(Music.class);
            defGroup.addAll(defList);
            musicList.add(defGroup);
            //**获取自定义列表的歌曲
            for (MusicGroup musicGroup : musicGroups) {
                MusicList<Music> list=new MusicList<>(musicGroup.getGroupName());
                List<Music> musics=DataSupport.where("groupId=?", musicGroup.getId()+"").find(Music.class);
                list.addAll(musics);
                if(list.size()!=0)
                	musicList.add(list);
            }
            play.musicListChange(musicList);
        }).start();
	}
	private boolean isScanFile=false;
	/**
	 * 扫描音乐
	 */
	private void scanMusic(){
		if(isScanFile)return;
		new Thread(() -> {
            isScanFile=true;
            List<ScanMusicType> types=DataSupport.findAll(ScanMusicType.class);
            if(types.size()==0){//**如果扫描设置不存在，就采用默认扫描设置
                String suffix[]=new String[]{"mp3","ogg","wav"};
                for(String str:suffix){
                    ScanMusicType scanMusicType=new ScanMusicType();
                    scanMusicType.setScanSuffix(str);
                    scanMusicType.setMinFileSize(1024*50);
                    scanMusicType.save();
                    types.add(scanMusicType);
                }

            }
            else{
            	for(int i=0;i<types.size();i++){
            		if(!types.get(i).isScanable()){
            			types.remove(i);
            			i--;
					}
				}
			}
            List<String> suffixs=new ArrayList<>();
            List<Integer> len=new ArrayList<>();
            for(ScanMusicType t:types){
                suffixs.add(t.getScanSuffix());
                len.add(t.getMinFileSize());
            }
            GetFiles getFiles=new GetFiles();
            getFiles.getfiles(suffixs,len);
            for(int i=0;i<getFiles.name.size();i++){
                String[] str=Shortcut.getName(getFiles.name.get(i));
                Music music=new Music(deprecateSuffix(str[0]),str[1],getFiles.path.get(i));
                //**更新或者插入一条记录
                music.saveOrUpdate();
            }
            //***扫描结束
            getMusicList();
            isScanFile=false;
        }).start();
	}



	private String deprecateSuffix(String str){
		int index=str.lastIndexOf('.');
		if(index<0)return str;
		return str.substring(0,index);
	}

}

