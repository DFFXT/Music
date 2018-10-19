package com.web.subWeb;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ListView;
import android.widget.Toast;

import com.web.config.DownloadViewAdapter;
import com.web.data.InternetMusic;
import com.web.service.FileDownloadService;
import com.web.web.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InlinedApi")
public class MusicDownLoad extends Activity implements FileDownloadService.DownloadListener {
	private ListView listView;
	private List<InternetMusic> dataList=new ArrayList<>();
	private DownloadViewAdapter adapter;

	private ServiceConnection serviceConnection;
	private FileDownloadService.Connect connect;

	protected void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.music_download);
		findId();//--获取组件
		dataList= DataSupport.findAll(InternetMusic.class);
		setAdapter();
		connect();
	}
	public void findId(){//--获取组件
		listView=findViewById(R.id.list);
	}
	private void connect(){

		Intent intent=new Intent(this,FileDownloadService.class);
		startService(intent);
		bindService(intent,serviceConnection=new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				connect=(FileDownloadService.Connect)service;
				connect.setDownloadListener(MusicDownLoad.this);
				if(adapter!=null){
					String hash=connect.getDownloading();
					adapter.setConnect(connect);
					if(hash!=null){
						adapter.setDownloadHash(hash);
					}
				}
			}
			@Override
			public void onServiceDisconnected(ComponentName name) { }
		},BIND_AUTO_CREATE);
	}
	private void setAdapter(){//--设置适配器
		try{
			adapter=new DownloadViewAdapter(MusicDownLoad.this, dataList,connect);
			listView.setAdapter(adapter);
		}catch(Exception e){
			e.printStackTrace();
		}
	}





	public void show(String str){
		Toast.makeText(MusicDownLoad.this, str, Toast.LENGTH_LONG).show();
	}


	private int preLength;
	@Override
	public void progressChange(String hash, int progress, int max) {
		if(progress-preLength<1024*100){
			return;
		}
		preLength=progress;
		for(InternetMusic music:dataList){
			if(music.getHash().equals(hash)){
				music.setHasDownload(progress);
				runOnUiThread(() -> adapter.notifyDataSetChanged());
				break;
			}
		}
	}

	@Override
	public void complete(String hash) {
		for(InternetMusic music:dataList){
			if(music.getHash().equals(hash)){
				dataList.remove(music);
				runOnUiThread(() -> adapter.notifyDataSetChanged());
				break;
			}
		}
	}

	@Override
	public void statusChange(final String hash, final boolean isDownload) {
        if(!hash.equals(adapter.getDownloadHash())){
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(!isDownload){
						adapter.setDownloadHash("");
					}else {
						adapter.setDownloadHash(hash);
					}
				}
			});

        }
	}




	public void onDestroy(){
		super.onDestroy();
		if(serviceConnection!=null){
			unbindService(serviceConnection);
			if(connect.getDownloading()==null){
				stopService(new Intent(this,FileDownloadService.class));
			}
		}
	}
}
