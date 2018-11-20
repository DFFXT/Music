package com.web.subWeb;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.web.moudle.musicDownload.adpter.DownloadViewAdapter;
import com.web.moudle.musicDownload.service.FileDownloadService;
import com.web.config.GetFiles;
import com.web.web.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

public class ProhibitWebSite extends Activity {
	ListView listView;
	List<Map<String,String>> dataList=new ArrayList<Map<String, String>>();
	DownloadViewAdapter adapter;
	DecimalFormat formatKb=new DecimalFormat("0");
	DecimalFormat formatMB=new DecimalFormat("0.00");
	GetFiles gfFiles=new GetFiles();
	protected void onCreate(Bundle b){
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.music_download);
		findId();//--获取组件
		register();
		send();//--发送信息
	}
	public void findId(){//--获取组件
		listView=(ListView)findViewById(R.id.list);
	}
	public void setAdapter(){//--设置适配器
		try{
			//adapter=new DownloadViewAdapter(ProhibitWebSite.this, dataList, R.layout.download_listview,"file");
			//listView.setAdapter(adapter);
		}catch(Exception e){
			gfFiles.write(GetFiles.rootPath+"log.txt", e+"", true);
		}
	}
	public void register(){//--注册接收器
		IntentFilter filter=new IntentFilter();
		filter.addAction("FileDownload");
		registerReceiver(receiver, filter);
	}
	public void send(){//--向服务发送信息
		Intent intent=new Intent(ProhibitWebSite.this,FileDownloadService.class);
		intent.setAction("downloadInfo");
		startService(intent);
	}
	BroadcastReceiver receiver=new BroadcastReceiver() {//--接收器
		public void onReceive(Context arg0, Intent intent) {
			String type = intent.getStringExtra("type_download");
			if(type.equals("list")){//--列表
				newList(intent);
			}else if(type.equals("progress")){//--进度
				progress(intent);
			}
		}
	};
	@SuppressWarnings("unchecked")
	public void newList(Intent intent){//--传值
		dataList=(List<Map<String,String>>)intent.getSerializableExtra("task");
		setAdapter();//--接收返回并显示
	}
	public void progress(Intent intent){//--更新进度
		if(dataList.isEmpty())return;//----必须存在，进度和显示有时差，否则在进行任务删除时会强退
		int downloadIndex=intent.getIntExtra("downloadIndex", 0);
		Map<String, String> map=new HashMap<String, String>();
			map=dataList.get(downloadIndex);
			map.put("hasDownload", intent.getStringExtra("hasDownload"));
		dataList.remove(downloadIndex);
		dataList.add(downloadIndex,map);
		adapter.notifyDataSetChanged();
	}
	public void show(String str){
		Toast.makeText(ProhibitWebSite.this, str, Toast.LENGTH_LONG).show();
	}
	
	
}
