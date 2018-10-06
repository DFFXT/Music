package com.web.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.web.config.GetFiles;
import com.web.config.Shortcut;
import com.web.data.InternetMusic;
import com.web.data.InternetMusicInfo;
import com.web.data.Music;
import com.web.subWeb.GetInfo;

import org.litepal.crud.DataSupport;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloadService extends Service {
	public final static String ACTION_DOWNLOAD="com.web.web.File.download";
	private GetFiles gfFiles=new GetFiles();
	InternetMusic task;
	private boolean download=false;
	private Connect connect;
	private DownloadListener downloadListener;
	private MyHandler handler=new MyHandler(this);
	@Override
	public IBinder onBind(Intent intent) {
		if(connect==null)connect=new Connect();
		return connect;
	}
	public class Connect extends Binder{
		public void setDownloadListener(DownloadListener downloadListener){
			FileDownloadService.this.downloadListener=downloadListener;
		}
		public void start(String hash){
			if(!download){
				addTask(hash);
			}
		}
		public void pause(String hash){

			if(download&&task!=null&&task.getHash().equals(hash)){
				download=false;
			}
		}
		public void delete(String hash){
			if(download&&task.getHash().equals(hash)){
				download=false;
				if(task!=null){
					Shortcut.fileDelete(task.getPath());
					task.delete();
					task=null;
				}
			}else {
				DataSupport.deleteAll(InternetMusic.class,"hash=?",hash);
			}
		}

		/**
		 * 获取正在下载的hash
		 * @return hash
		 */
		public @Nullable String getDownloading(){
			if(download&&task!=null){
				return task.getHash();
			}
			return null;
		}

	}
	//--[download:开始下载,pause:暂停下载,cancel:取消下载]
	public int onStartCommand(Intent intent,int flag,int startId){
		String action=intent.getAction();
		if(action!=null){
			switch (action){
				case ACTION_DOWNLOAD:{
					addTask(intent.getStringExtra("hash"));
				}break;
			}
		}
		return START_NOT_STICKY;
	}

	/**
	 * 开始一个task
	 */
	private void addTask(final String hash){
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(!download){
					task=DataSupport.where("hash=?",hash).findFirst(InternetMusic.class);
					if(task==null) return;
					if(task.getHasDownload()==0)
						startTask(task);
					else {
						downloadGO(task);
					}
				}
			}
		}).start();

	}
	private InternetMusic getFirst(){
		return DataSupport.findFirst(InternetMusic.class);
	}

	/**
	 * 继续下载
	 */
	public void nextDownload(){
		new Thread(() -> {
            task=getFirst();
            if(task==null){//**所以都下载完成
				stopSelf();
            	return;
			}
            if(task.getHasDownload()==0){
                startTask(task);
            }else {
                downloadGO(task);
            }
        }).start();
	}


	public void startTask(final InternetMusic music){
		if(music==null)return;
		download=true;//--更新进行
		GetInfo getInfo=new GetInfo();//**获取歌曲详细信息
		InternetMusicInfo info=getInfo.getMusicInfo(music.getHash());
		info.setMusicName(music.getMusicName());
		info.setSinger(music.getSingerName());
		music.setUrl(info.getPath());
		music.save();
		if(!Shortcut.fileExsist(info.getLyricsPath())){
			//**下载歌词
			gfFiles.write(info.getLyricsPath(),getInfo.getKrc(music.getHash()),false);
		}
		if(!Shortcut.fileExsist(info.getIconPath())){
			if(info.getImgAddress()!=null){
				//**下载图片
				gfFiles.NetDataToLocal(info.getImgAddress(), GetFiles.singerPath+music.getSingerName()+".png");//---下载图片
			}
		}
		downloadGO(music);
	}

	private void downloadGO(InternetMusic music){//--需要新线程  保存至本地
		download=true;
		byte[] byte1= new byte[1024*8];
		try {
			int hasDownload=music.getHasDownload();
			if(hasDownload==0){//--清除同名文件【重新下载】
				Shortcut.fileDelete(music.getPath());
			}
			if(music.getUrl().equals(""))return ;
			URL Url=new URL(music.getUrl());
			HttpURLConnection connection= (HttpURLConnection) Url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Range", "bytes="+hasDownload+"-"+music.getFullSize());
			InputStream stream = connection.getInputStream();
			RandomAccessFile file=new RandomAccessFile(music.getPath(), "rw");
			int tmp;
			int sdsfdsf=byte1.length;
			int offset= (int) file.length();
			if(downloadListener!=null)
				downloadListener.statusChange(music.getHash(),true);
			while((tmp=stream.read(byte1,0,sdsfdsf))!=-1){
				file.seek(offset);
				file.write(byte1);
				offset+=tmp;
				music.setHasDownload(music.getHasDownload()+tmp);
				music.save();//**时刻保存进度
				if(downloadListener!=null){
					downloadListener.progressChange(music.getHash(),music.getHasDownload(),music.getFullSize());
				}
				if(!download){//**暂停
					downloadListener.statusChange(music.getHash(),false);
					file.close();
					stream.close();
					connection.disconnect();
					return ;
				}
			}
			file.close();
			stream.close();
			download=true;

			if(downloadListener!=null)
				downloadListener.complete(music.getHash());

			Music record=new Music(music.getMusicName(),music.getSingerName(),music.getPath());
			record.saveOrUpdate();
			complete(music.getPath());//**下载完成 --- 广播
			music.delete();//**成功删除记录
			handler.sendEmptyMessage(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载完成告知播放器
	 * @param path path
	 */
	private void complete(String path){
		Intent intent=new Intent(this,MusicPlay.class);
		intent.setAction(MusicPlay.ACTION_DOWNLOAD_COMPLETE);
		intent.putExtra("path",path);
		startService(intent);
	}



	public interface DownloadListener{
		void progressChange(String hash,int progress,int max);
		void complete(String hash);
		void statusChange(String hash,boolean isDownload);
	}



	public void show(String str){
		Toast.makeText(FileDownloadService.this, str, Toast.LENGTH_LONG).show();
	}



	/**
	 * 添加一个下载任务
	 * @param context context
	 * @param hash hash
	 */
	public static void addTask(Context context,String hash){
		Intent intent=new Intent(context,FileDownloadService.class);
		intent.setAction(FileDownloadService.ACTION_DOWNLOAD);
		intent.putExtra("hash",hash);
		context.startService(intent);
	}


	private static class MyHandler extends Handler{
		private WeakReference<FileDownloadService> service;
		MyHandler(FileDownloadService service){
			this.service=new WeakReference<>(service);
		}
		public void handleMessage(Message msg){
			switch (msg.what){
				case 1:{
					service.get().nextDownload();
				}break;
			}
		}
	}


}
