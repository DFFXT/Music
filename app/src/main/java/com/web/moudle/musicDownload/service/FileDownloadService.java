package com.web.moudle.musicDownload.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.web.common.base.BaseSingleObserver;
import com.web.common.tool.MToast;
import com.web.common.util.ResUtil;
import com.web.config.GetFiles;
import com.web.config.Shortcut;
import com.web.data.InternetMusic;
import com.web.data.InternetMusicInfo;
import com.web.data.Music;
import com.web.moudle.musicDownload.bean.DownloadMusic;
import com.web.moudle.music.player.MusicPlay;
import com.web.subWeb.GetInfo;
import com.web.web.R;

import org.jetbrains.annotations.NotNull;
import org.litepal.crud.DataSupport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FileDownloadService extends Service {
	public final static String ACTION_DOWNLOAD="com.web.web.File.download";
	private GetFiles gfFiles=new GetFiles();
	private Connect connect;
	private @Nullable DownloadListener downloadListener;
	private List<DownloadMusic> downloadList=new ArrayList<>();//**下载列表
	private List<DownloadMusic> downloadingList=new ArrayList<>();
	private int maxDownloadingCount=3;
	@Override
	public IBinder onBind(Intent intent) {
		if(connect==null)connect=new Connect();
		return connect;
	}
	public class Connect extends Binder{
		public void setDownloadListener(DownloadListener downloadListener){
			FileDownloadService.this.downloadListener=downloadListener;
		}
		public boolean start(int id){
			if(getDownloadingNum()>=maxDownloadingCount)return false;
			for(DownloadMusic dm:downloadList){
				if(dm.getInternetMusic().getId()==id){
				    dm.setStatus(DownloadMusic.DOWNLOAD_WAIT);
					startTask(dm);
					return true;
				}
			}
			return false;
		}
		public boolean pause(int id){
			for(DownloadMusic dm:downloadList){
				if(dm.getInternetMusic().getId()==id){
					dm.setStatus(DownloadMusic.DOWNLOAD_PAUSE);
					return true;
				}
			}
			return false;
		}

		public void delete(int id){
			for(int i=0;i<downloadList.size();i++){
				DownloadMusic dm=downloadList.get(i);
				if(dm.getInternetMusic().getId()==id){
					dm.getInternetMusic().delete();
					if(dm.getStatus()!=DownloadMusic.DOWNLOAD_DOWNLOADING){
						FileDownloadService.this.delete(dm.getInternetMusic());
					}
					dm.setStatus(DownloadMusic.DOWNLOAD_DELETE);
					break;
				}
			}

			if (downloadListener != null) {
				downloadListener.listChanged(downloadList);
			}
		}
		public void getDownloadList(){
			if(downloadList.size()!=0){
				listChange(downloadList);
				return;
			}
			Single.create((SingleOnSubscribe<List<InternetMusic>>) emitter -> {
				List<InternetMusic> res=DataSupport.findAll(InternetMusic.class);
				emitter.onSuccess(res);
			}).subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new BaseSingleObserver<List<InternetMusic>>() {
						@Override
						public void onSuccess(List<InternetMusic> res) {
							for(InternetMusic m:res){
								downloadList.add(new DownloadMusic(m,DownloadMusic.DOWNLOAD_WAIT));
							}
							listChange(downloadList);
						}
					});
		}
		public List<DownloadMusic> getDowloadingMusic(){
			downloadingList.clear();
			for(DownloadMusic dm:downloadList){
				if(dm.getStatus()==DownloadMusic.DOWNLOAD_DOWNLOADING) {
					downloadingList.add(dm);
				}
			}
			return downloadingList;
		}
	}

	/**
	 * 删除本地相关信息
	 * @param internetMusic music
	 */
	private void delete(InternetMusic internetMusic){
		Music.deleteMusic(new Music(internetMusic.getMusicName(),
				internetMusic.getSingerName(),internetMusic.getPath()));
	}
	public int onStartCommand(Intent intent,int flag,int startId){
		String action=intent.getAction();
		if(action!=null){
			switch (action){
				case ACTION_DOWNLOAD:{
					InternetMusic music= (InternetMusic) intent.getSerializableExtra("music");
					addMusic(music);
				}break;
			}
		}
		return START_NOT_STICKY;
	}

	/**
	 * 从外部添加任务
	 * @param music music
	 */
	private void addMusic(InternetMusic music){
		for(int i=0;i<downloadList.size();i++){
			if(downloadList.get(i).getInternetMusic().getHash().equals(music.getHash())){
				return;
			}
		}
		music.save();
		DownloadMusic dm=new DownloadMusic(music,DownloadMusic.DOWNLOAD_WAIT);
		downloadList.add(dm);
		startTask(dm);
		listChange(downloadList);
	}



	private int getDownloadingNum(){
		int num=0;
		for(DownloadMusic dm:downloadList){
			if(dm.getStatus()==DownloadMusic.DOWNLOAD_DOWNLOADING) num++;
		}
		return num;
	}




	public void startTask(final DownloadMusic downloadMusic) {
		Single.create((SingleOnSubscribe<DownloadMusic>) emitter -> {
			if(getDownloadingNum()>=maxDownloadingCount){
				emitter.onError(new Throwable(""));
				return;
			}
			if(downloadMusic.getStatus()==DownloadMusic.DOWNLOAD_WAIT){
				downloadMusic.setStatus(DownloadMusic.DOWNLOAD_DOWNLOADING);
			}else {
				emitter.onError(new Throwable(""));
				return;
			}
			InternetMusic music=downloadMusic.getInternetMusic();
			if(music==null)return;
			GetInfo getInfo=new GetInfo();//**获取歌曲详细信息
			String hash;
			switch (music.getType()){
				case InternetMusic.TYPE_NORMAL:hash=music.getHash();break;
				case InternetMusic.TYPE_HIGH:hash=music.getSqHash();break;
				default:return;
			}
			InternetMusicInfo info=getInfo.getMusicInfo(hash);
			info.setMusicName(music.getMusicName());
			info.setSinger(music.getSingerName());
			music.setUrl(info.getPath());
			music.save();
			if(!Shortcut.fileExsist(info.getLyricsPath())){
				//**下载歌词
				gfFiles.write(info.getLyricsPath(),getInfo.getKrc(music.getHash()),false);
			}
			if(!Shortcut.fileExsist(info.getIconPath())){
				if(!TextUtils.isEmpty(info.getImgAddress())){//**下载图片
					gfFiles.NetDataToLocal(info.getImgAddress(), GetFiles.singerPath+music.getSingerName()+".png");//---下载图片
				}
			}
			downloadGO(downloadMusic);
			emitter.onSuccess(downloadMusic);
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new BaseSingleObserver<DownloadMusic>() {
					@Override
					public void onSuccess(DownloadMusic res) {
						for(DownloadMusic dm:downloadList){
							if(dm.getStatus()==DownloadMusic.DOWNLOAD_WAIT){
								startTask(dm);
								break;
							}
						}
					}

					@Override
					public void onError(@NotNull Throwable e) {
						super.onError(e);
						for(DownloadMusic dm:downloadList){
							if(dm.getStatus()==DownloadMusic.DOWNLOAD_WAIT){
								startTask(dm);
								break;
							}
						}
					}
				});
	}


	private void downloadGO(DownloadMusic downloadMusic){//--需要新线程  保存至本地
		byte[] byte1= new byte[1024*50];
		InternetMusic music=downloadMusic.getInternetMusic();
		if(music.getHasDownload()==music.getFullSize()){//**已经完成了的
			complete(downloadMusic);
			return;
		}
		try {
			int hasDownload=music.getHasDownload();
			if(hasDownload==0){//--清除同名文件【重新下载】
				Shortcut.fileDelete(music.getPath());
			}
			if(TextUtils.isEmpty(music.getUrl())){
				MToast.showToast(FileDownloadService.this, ResUtil.getString(R.string.download_urlError));
				return ;
			}
			URL Url=new URL(music.getUrl());
			HttpURLConnection connection= (HttpURLConnection) Url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Range", "bytes="+hasDownload+"-"+music.getFullSize());
			InputStream stream = connection.getInputStream();
			BufferedInputStream bis=new BufferedInputStream(stream);
			File f=new File(music.getPath());
			FileOutputStream fos=new FileOutputStream(f,true);
			BufferedOutputStream bos=new BufferedOutputStream(fos);
			int tmp;
			statusChange(downloadMusic);
			while((tmp=bis.read(byte1))!=-1){
				bos.write(byte1,0,tmp);
				music.setHasDownload(music.getHasDownload()+tmp);
				music.save();//**时刻保存进度
				progressChange(music);
				if(downloadMusic.getStatus()!=DownloadMusic.DOWNLOAD_DOWNLOADING){//**暂停
					bis.close();
					fos.close();
					bos.close();
					stream.close();
					connection.disconnect();
					if(downloadMusic.getStatus()==DownloadMusic.DOWNLOAD_DELETE){//**删除任务
						downloadList.remove(downloadMusic);
						listChange(downloadList);
						delete(music);
						return;
					}
					statusChange(downloadMusic);
					return ;
				}
			}
			bis.close();
			fos.close();
			stream.close();


			Music record=new Music(music.getMusicName(),music.getSingerName(),music.getPath());
			record.saveOrUpdate();
			complete(downloadMusic);//**下载完成 --- 广播

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listChange(List<DownloadMusic> list){
		if(downloadListener!=null){
			downloadListener.listChanged(list);
		}
	}
	private void statusChange(DownloadMusic dm){
		if(downloadListener!=null){
			downloadListener.statusChange(dm.getInternetMusic().getId(),dm.getStatus()==DownloadMusic.DOWNLOAD_DOWNLOADING);
		}
	}
	private void progressChange(InternetMusic music){
		if(downloadListener!=null){
			downloadListener.progressChange(music.getId(),music.getHasDownload(),music.getFullSize());
		}
	}

	/**
	 * 下载完成告知播放器
	 */
	private void complete(DownloadMusic dm){
		downloadList.remove(dm);
		dm.getInternetMusic().delete();
		listChange(downloadList);
		Intent intent=new Intent(this,MusicPlay.class);
		intent.setAction(MusicPlay.ACTION_DOWNLOAD_COMPLETE);
		intent.putExtra("path",dm.getInternetMusic().getPath());
		startService(intent);

		//***添加进媒体库
		Intent intent2 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(new File(dm.getInternetMusic().getPath()));
		intent2.setData(uri);
		sendBroadcast(intent2);
	}




	public interface DownloadListener{
		void progressChange(int id,int progress,int max);
		void complete(InternetMusic music);
		void statusChange(int id,boolean isDownload);
		void listChanged(List<DownloadMusic> downloadMusicList);
	}



	public void show(String str){
		Toast.makeText(FileDownloadService.this, str, Toast.LENGTH_LONG).show();
	}



	/**
	 * 添加一个下载任务
	 * @param context context
	 * @param music music
	 */
	public static void addTask(Context context,InternetMusic music){
		Intent intent=new Intent(context,FileDownloadService.class);
		intent.setAction(FileDownloadService.ACTION_DOWNLOAD);
		intent.putExtra("music",music);
		context.startService(intent);
	}





}
