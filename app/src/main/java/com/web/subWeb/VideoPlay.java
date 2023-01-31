package com.web.subWeb;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import com.music.m.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlay extends Activity {
	private String ip=null,path=null;
	private MediaPlayer player=new MediaPlayer();
	VideoView videoView;
	ImageView statu;
	protected void onCreate(Bundle b){
		super.onCreate(b);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.vedio);
		getIntentData();
		init();
		if (this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT){
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
	void init(){
		videoView=(VideoView)findViewById(R.id.vedio_sview); 
		videoView.setMediaController(new MediaController(this));
		String url="http://"+ip+":8080/"+path.replace("D:\\","D/").replace("\\", "/");
		
			videoView.setVideoPath(url);
	}
	void getIntentData(){
		Intent intent=getIntent();
		if(intent.getAction().equals("onlineVideo")){
			ip=intent.getStringExtra("ip");
			path=intent.getStringExtra("path");
		}
	}
	public void show(String str){
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}

	public boolean onKeyDown(int keyEvent,KeyEvent event){
		if(keyEvent==KeyEvent.KEYCODE_BACK){
			player.release();
			finish();
		}
		return false;
	}
}

