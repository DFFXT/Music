package com.web.moudle.musicDownload.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.web.common.base.BaseActivity;
import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.config.Shortcut;
import com.web.data.InternetMusic;
import com.web.misc.TopBarLayout;
import com.web.moudle.musicDownload.adpter.DownloadViewAdapter;
import com.web.moudle.musicDownload.bean.DownloadMusic;
import com.web.moudle.musicDownload.service.FileDownloadService;
import com.web.web.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InlinedApi")
public class MusicDownLoadActivity extends BaseActivity implements FileDownloadService.DownloadListener {
	private RecyclerView rv_download;
	private List<DownloadMusic> dataList=new ArrayList<>();
	private DownloadViewAdapter adapter;

	private ServiceConnection serviceConnection;
	private FileDownloadService.Connect connect;
	private boolean active=false;


	@Override
	public int getLayoutId() {
		return R.layout.music_download;
	}

	@Override
	public void initView() {
        rv_download=findViewById(R.id.list);
        rv_download.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
		connect();
		TopBarLayout topBarLayout=findViewById(R.id.topBar_musicDownload);
		topBarLayout.setMainTitle(ResUtil.getString(R.string.downloadManager));
		ViewUtil.transparentStatusBar(getWindow());
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
                if(status==DownloadMusic.DOWNLOAD_DOWNLOADING){
                    connect.pause(id);

                }
                else {
                    connect.start(id);
                }
            }else if(view.getId()==R.id.close){
                new AlertDialog.Builder(MusicDownLoadActivity.this)
                        .setTitle(ResUtil.getString(R.string.delete))
                        .setMessage("\n\n")
                        .setNegativeButton(ResUtil.getString(R.string.no),null)
                        .setPositiveButton(ResUtil.getString(R.string.yes),(dialog,witch)->{
                            connect.delete(id);
                        }).create().show();
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
				List<DownloadMusic> list=connect.getDownloadingMusic();
				if(list.size()==0){
				    active=false;
				    break;
                }
				Shortcut.sleep(500);
				runOnUiThread(()-> adapter.notifyDataSetChanged());
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
			if(connect!=null)
				connect.removeDownloadListener(this);
		}
	}
}
