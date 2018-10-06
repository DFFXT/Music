package com.web.config;

import com.web.service.MusicPlay;
import com.web.subWeb.MusicPlayer_main_restruct;
import com.web.web.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

/*
 * 音乐通知栏管理
 */

public class MyNotification{
	public Context context;
	private Bitmap bitmap=null;
	private String name=null;
	private String singer=null;
	private boolean isPlay=false;
	private RemoteViews remoteViews=null;
	public MyNotification(Context context) {
		this.context=context;
	}
	public MyNotification(Context context,Bitmap bitmap,String name,String singer,boolean isPlay) {
		this.context=context;
		this.bitmap=bitmap;
		this.name=name;
		this.singer=singer;
		this.isPlay=isPlay;
	}
	
	public void show(){//--通知栏
		if(context==null) return;
		Notification.Builder mBuilder = new Notification.Builder(context);
		remoteViews=new RemoteViews(context.getPackageName(),R.layout.music_navigator_control);
		mBuilder.setContent(remoteViews);
		mBuilder.setPriority(Notification.PRIORITY_MAX);
		mBuilder.setSmallIcon(R.drawable.defaultfile,1);//--必须
		remoteViews.setTextViewText(R.id.song, name);
		remoteViews.setTextViewText(R.id.singer, singer);
		if(bitmap!=null){//--添加图片
			remoteViews.setImageViewBitmap(R.id.music_icon, bitmap);
		}else{//--没有图片，添加默认图片
			remoteViews.setImageViewResource(R.id.music_icon, R.drawable.ic_launcher);
		}
		if(isPlay){
			remoteViews.setImageViewResource(R.id.pause, R.drawable.play);
		}else{
			remoteViews.setImageViewResource(R.id.pause, R.drawable.pause);
		}
		//--设置点击事件
		//--PendingIntent 的flag必须不一样
		PendingIntent pNext=PendingIntent.getService(context, 1, makeIntent(MusicPlay.ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.next, pNext);
		
		PendingIntent pPre=PendingIntent.getService(context, 2, makeIntent(MusicPlay.ACTION_PRE), PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.pre, pPre);
		
		PendingIntent pPause=PendingIntent.getService(context, 3,makeIntent(MusicPlay.ACTION_STATUS_CHANGE), PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.pause, pPause);
		
		//--进入播放器
		Intent enterIntent=new Intent(context, MusicPlayer_main_restruct.class).
				addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent enter=PendingIntent.getActivity(context, 0, enterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.op, enter);
		
		mBuilder.setAutoCancel(false);
		Notification notification=mBuilder.build();
		notification.flags=Notification.FLAG_NO_CLEAR;//---不会被清理
		//--服务启动notification，防止服务被关闭
		((Service) context).startForeground(11, notification);
	}
	public void setName(String name){
		this.name=name;
		//remoteViews.setTextViewText(R.id.song, name);
	}
	public void setSinger(String singer){
		this.singer=singer;
		//remoteViews.setTextViewText(R.id.singer, singer);
	}
	public void setBitMap(Bitmap bitmap){
		this.bitmap=bitmap;
		//remoteViews.setImageViewBitmap(R.id.music_icon, bitmap);
	}
	public void setPlayStatus(boolean isPlay){
		this.isPlay=isPlay;
	}

	private Intent makeIntent(String action){
		Intent intent=new Intent(context,MusicPlay.class);
		intent.setAction(action);
		return intent;
	}
}
