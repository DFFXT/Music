package com.web.config;

import java.util.List;
import java.util.Map;

import com.web.web.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	private List<Map<String, Object>> data;  
    private LayoutInflater layoutInflater; 
    public ListViewAdapter(Context context,List<Map<String,Object> > list){    
        this.data=list;
        this.layoutInflater=LayoutInflater.from(context);  
    }  
    public final class listviewitem{
		public TextView tv;
		public ImageView img;
    }
    	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		listviewitem lView;
	        if(convertView==null){  
	            lView =new listviewitem();
	            //获得组件，实例化组件   
	            convertView=layoutInflater.inflate(R.layout.item, parent, false);  
	            lView.tv=(TextView)convertView.findViewById(R.id.item);  
	            lView.img=(ImageView)convertView.findViewById(R.id.icon);
	            convertView.setTag(lView);  
	        }else{  
	        	 lView = (listviewitem)convertView.getTag();  
	        }  
	        //绑定数据  
	         
	        lView.tv.setText((String)data.get(position).get("main"));
	        if(data.get(position).get("type").equals("int"))
	        	lView.img.setBackgroundResource((Integer)data.get(position).get("img"));
	        else if(data.get(position).get("type").equals("bitmap"))
	        	lView.img.setImageBitmap((Bitmap) data.get(position).get("img"));
	        return convertView;  
	    }  
	}

