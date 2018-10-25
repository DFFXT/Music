package com.web.moudle.musicDownload.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.web.config.DownloadViewAdapter;
import com.web.config.Shortcut;
import com.web.data.InternetMusic;
import com.web.moudle.musicDownload.bean.DownloadMusic;
import com.web.moudle.musicDownload.service.FileDownloadService;
import com.web.web.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InlinedApi")
public class MusicDownLoadActivity extends Activity implements FileDownloadService.DownloadListener {
	private RecyclerView rv_download;
	private List<DownloadMusic> dataList=new ArrayList<>();
	private DownloadViewAdapter adapter;

	private ServiceConnection serviceConnection;
	private FileDownloadService.Connect connect;
	private boolean active=false;

	protected void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.music_download);
		findId();//--获取组件
		connect();
	}
	public void findId(){//--获取组件
		rv_download=findViewById(R.id.list);
		rv_download.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
	}
	private void connect(){
		Intent intent=new Intent(this,FileDownloadService.class);
		startService(intent);
		bindService(intent,serviceConnection=new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				connect=(FileDownloadService.Connect)service;
				connect.setDownloadListener(MusicDownLoadActivity.this);
				connect.getDownloadList();
			}
			@Override
			public void onServiceDisconnected(ComponentName name) { }
		},BIND_AUTO_CREATE);
	}
	private void setAdapter(){//--设置适配器
        adapter=new DownloadViewAdapter(MusicDownLoadActivity.this, dataList);
        adapter.setListener((view,position)->{
            int id=dataList.get(position).getInternetMusic().getId();
            int status=dataList.get(position).getStatus();
            if(view.getId()==R.id.downloadStatu){
                if(status==DownloadMusic.DOWNLOAD_DOWNLODINF){
                    connect.pause(id);
                }
                else {
                    connect.start(id);
                }
            }else if(view.getId()==R.id.close){
                connect.delete(id);
            }
        });
        rv_download.setAdapter(adapter);
	}





	public void show(String str){
		Toast.makeText(MusicDownLoadActivity.this, str, Toast.LENGTH_LONG).show();
	}
	private void loop(){
	    if(active)return;
	    active=true;
		new Thread(()->{
			while (true){
				List<DownloadMusic> list=connect.getDowloadingMusic();
				if(list.size()==0){
				    active=false;
				    break;
                }
				Shortcut.sleep(500);
				runOnUiThread(()->{
					adapter.notifyDataSetChanged();
				});
			}
		}).start();
	}


	@Override
	public void progressChange(int id, int progress, int max) {
		loop();
	}

	@Override
	public void complete(InternetMusic music) {
	}

	@Override
	public void statusChange(int id,boolean isDownload) {
		int index=getIndex(id);
		if(id<0)return;
		runOnUiThread(()-> adapter.notifyItemChanged(index));
	}

	@Override
	public void listChanged(List<DownloadMusic> downloadMusicList) {
		dataList=downloadMusicList;
		runOnUiThread(()->{
			if(adapter==null){
				setAdapter();
			}else {
				adapter.notifyDataSetChanged();
			}
		});

	}
	private int getIndex(int id){
		for(int i=0;i<dataList.size();i++){
			if(id==dataList.get(i).getInternetMusic().getId()){
				return i;
			}
		}
		return -1;
	}

	public void onDestroy(){
		super.onDestroy();
		if(serviceConnection!=null){
			unbindService(serviceConnection);
		}
	}
}
