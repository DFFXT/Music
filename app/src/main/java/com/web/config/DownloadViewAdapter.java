package com.web.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.web.data.InternetMusic;
import com.web.service.FileDownloadService;
import com.web.web.R;

import java.text.DecimalFormat;
import java.util.List;


public class DownloadViewAdapter extends BaseAdapter{
	private List<InternetMusic> dataList;
	private DecimalFormat format=new DecimalFormat("0.00");
	private Context context;
	private String downloadHash="";
	private FileDownloadService.Connect connect;
	public DownloadViewAdapter(Context c, List<InternetMusic> data, FileDownloadService.Connect connect){
		dataList=data;
		context=c;
		this.connect=connect;
	}

	public void setConnect(FileDownloadService.Connect connect) {
		this.connect = connect;
	}

	private class View_item{
		RelativeLayout parent;
		TextView musicName;
		TextView hasDownload;
		TextView fullSize;
		ImageView close;
		ImageView statu;
	}
	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("SetTextI18n")
	@Override
	public View getView(final int position, View view, ViewGroup parent) {//--视图信息和内部点击事件
		final View_item item;
		final InternetMusic music=dataList.get(position);
		if(view==null){
			item=new View_item();
			view=LayoutInflater.from(context).inflate(R.layout.download_listview,parent, false);
			item.parent=view.findViewById(R.id.parent);
			item.musicName=view.findViewById(R.id.musicName);
			item.hasDownload=view.findViewById(R.id.hasDownload);
			item.fullSize=view.findViewById(R.id.fullSize);
			item.close=view.findViewById(R.id.close);
			item.statu=view.findViewById(R.id.downloadStatu);
			view.setTag(item);

			item.close.setOnClickListener(new OnClickListener() {//--点击取消下载
				public void onClick(View v) {
					connect.delete(music.getHash());
					dataList.remove(position);
					notifyDataSetChanged();
				}
			});
			item.statu.setOnClickListener(new OnClickListener() {//--状态图标点击事件
				public void onClick(View v) {//--点击切换图标
					Log.i("log","click");
					if(downloadHash.equals(music.getHash())){
						Log.i("log","pause");
						connect.pause(downloadHash);
						downloadHash="";
					}else if(downloadHash.equals("")) {
						Log.i("log","start");
						connect.start(music.getHash());
					}
				}
			});
			item.parent.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i("log","parent");
					item.statu.performClick();
				}
			});



		}else{
			item=(View_item)view.getTag();
		}
		item.musicName.setText(music.getMusicName());
		item.hasDownload.setText(format.format(music.getHasDownload()/1024.0/1024)+"MB/");//--切换为MB单位
		item.fullSize.setText(format.format(music.getFullSize()/1024.0/1024)+"MB");
		if(music.getHash().equals(downloadHash)){//--根据状态显示状态图标
			item.statu.setImageResource(R.drawable.play);
		}else{
			item.statu.setImageResource(R.drawable.pause);
		}

		return view;
	}

	public void setDownloadHash(String downloadHash) {
		this.downloadHash=downloadHash;
		notifyDataSetChanged();
	}

	public String getDownloadHash() {
		return downloadHash;
	}
}
