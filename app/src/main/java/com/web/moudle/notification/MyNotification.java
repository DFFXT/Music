package com.web.moudle.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.web.common.base.BaseCustomNotification;
import com.web.common.util.ResUtil;
import com.web.moudle.lyrics.LyricsActivity;
import com.web.moudle.music.page.local.MusicActivity;
import com.web.moudle.music.player.MusicPlay;
import com.web.moudle.setting.lyrics.LyricsSettingActivity;
import com.web.web.R;

import org.jetbrains.annotations.NotNull;

/*
 * 音乐通知栏管理
 */

public class MyNotification extends BaseCustomNotification {
	public Context context;
	private Bitmap bitmap=null;
	private String name=null;
	private String singer=null;
	private boolean isPlay=false;
	public MyNotification(Context context) {
		super(context,0, MyNotification.class.getName(), ResUtil.getString(R.string.musicControl),R.layout.music_navigator_control);
		this.context=context;
		init();
	}
	private void init() {
		//--设置点击事件
		//--PendingIntent 的flag必须不一样
		PendingIntent pNext=PendingIntent.getService(context, 1, makeIntent(MusicPlay.ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
		getView().setOnClickPendingIntent(R.id.next, pNext);

		PendingIntent pPre=PendingIntent.getService(context, 2, makeIntent(MusicPlay.ACTION_PRE), PendingIntent.FLAG_UPDATE_CURRENT);
		getView().setOnClickPendingIntent(R.id.pre, pPre);

		PendingIntent pPause=PendingIntent.getService(context, 3,makeIntent(MusicPlay.ACTION_STATUS_CHANGE), PendingIntent.FLAG_UPDATE_CURRENT);
		getView().setOnClickPendingIntent(R.id.pause, pPause);

		//--进入歌词界面
		Intent enterIntent=new Intent(context, LyricsActivity.class).
				addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent enter=PendingIntent.getActivity(context, 0, enterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		getView().setOnClickPendingIntent(R.id.music_icon, enter);



		//**今日歌词设置页面
		Intent settingIntent=new Intent(context, LyricsSettingActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent=PendingIntent.getActivity(context,0,settingIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		getView().setOnClickPendingIntent(R.id.tv_lyricsSetting,pendingIntent);


		//**进入本地列表
		Intent localIntent=new Intent(context, MusicActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent localIntentP=PendingIntent.getActivity(context,0,localIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		getView().setOnClickPendingIntent(R.id.rootView,localIntentP);

	}

	public MyNotification setName(String name){
		this.name=name;
		return this;
	}
	public MyNotification setSinger(String singer){
		this.singer=singer;
		return this;
	}
	public MyNotification setBitMap(Bitmap bitmap){
		this.bitmap=bitmap;
		return this;
	}
	public MyNotification setPlayStatus(boolean isPlay){
		this.isPlay=isPlay;
		return this;
	}

	private Intent makeIntent(String action){
		Intent intent=new Intent(context,MusicPlay.class);
		intent.setAction(action);
		return intent;
	}

	@Override
	public void update(@NotNull RemoteViews view) {
		if(context==null) return;
		view.setTextViewText(R.id.song, name);
		view.setTextViewText(R.id.singer, singer);
		if(bitmap!=null){//--添加图片
			view.setImageViewBitmap(R.id.music_icon, bitmap);
		}else{//--没有图片，添加默认图片
			view.setImageViewResource(R.id.music_icon, R.drawable.ic_launcher);
		}
		if(isPlay){
			setClear(false);
			view.setImageViewResource(R.id.pause, R.drawable.icon_play_white);
		}else{
			setClear(true);
			view.setImageViewResource(R.id.pause, R.drawable.icon_pause_white);
		}

	}
}
