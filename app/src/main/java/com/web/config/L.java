package com.web.config;

import java.util.ArrayList;

import com.web.web.R;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class L extends BaseAdapter{
	Context context;
	public ArrayList<FileListCollection> data=new ArrayList<FileListCollection>();
	public SparseArray<View> viewList=new SparseArray<View>();
	int layout;
	LayoutInflater layoutInflater;
	public L(Context context,ArrayList<FileListCollection> data,int layout){
		this.data=data;
		this.layout=layout;
		this.context=context;
		layoutInflater=LayoutInflater.from(context);
	}
	class V{
		public ImageView icon;
		public TextView name,size;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
		
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		V collection;
		if(viewList.get(position,null)==null){ 
			collection=new V();
			convertView=layoutInflater.inflate(layout, parent,false);
			collection.icon=(ImageView)convertView.findViewById(R.id.file_item_icon);
			collection.name=(TextView)convertView.findViewById(R.id.file_item_name);
			collection.size=(TextView)convertView.findViewById(R.id.file_item_size);
			putData(position, collection);
			convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
			viewList.put(position,convertView);
		}else{
			convertView=viewList.get(position);
		}
		return convertView;
	}
	void putData(int position,V collection){
		String type=(String)data.get(position).type;
		String fileSize="";
		int iconId=0;
		if(type.equals("file")){
			String name=data.get(position).name;
			iconId=R.drawable.defaultfile;
			fileSize=data.get(position).size;
			if(name.endsWith(".txt")){
				
			}else if(name.endsWith(".html")){
				iconId=R.drawable.html;
			}else if(name.endsWith(".zip")){
				iconId=R.drawable.zip;
			}else if(name.endsWith(".rar")){
				iconId=R.drawable.rar;
			}/*else if(name.endsWith(".mp3")){
				
			}else if(name.endsWith(".encrypt.js")){
				
			}else if(name.endsWith(".gif")){
				
			}else if(name.endsWith(".jpg")){
				
			}else if(name.endsWith(".png")){
				
			}else if(name.endsWith(".php")){
				
			}else if(name.endsWith(".css")){
				
			}*/
			
		}else if(type.equals("folder")){
			iconId=R.drawable.folder;
		}
		collection.icon.setBackgroundResource(iconId);
		collection.name.setText(data.get(position).name);
		collection.size.setText(fileSize);
	}
}

