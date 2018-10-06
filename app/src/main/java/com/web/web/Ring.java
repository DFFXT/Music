package com.web.web;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;


public class Ring extends Activity{
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.ring);
		AssetManager aManager=Ring.this.getAssets();
		try {
			AssetFileDescriptor assetFileDescriptor=aManager.openFd("1.mp3");
			final MediaPlayer player =new MediaPlayer();
			player.setAudioStreamType(AudioManager.STREAM_RING);
			player.setVolume(1f, 1f);
			player.setDataSource(assetFileDescriptor.getFileDescriptor());
			player.prepare();
			player.start();
			new AlertDialog.Builder(Ring.this)
			.setTitle("选择")
			.setMessage("吉时已到")
			.setNegativeButton("退出", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO 自动生成的方法存根
					player.reset();
				}
			}).create().show();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}
