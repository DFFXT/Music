package com.web.web;

import java.io.File;
import java.util.ArrayList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.web.subWeb.MusicPlayer_main_restruct;
import com.web.util.BaseActivity;

import dalvik.system.DexClassLoader;

@SuppressLint("InlinedApi")
public class Index extends BaseActivity implements OnClickListener,OnTouchListener{
	private String[] name;
	private int[] bgList;
	ArrayList<Button> butList=new ArrayList<Button>();
	public void onCreate(Bundle b){ 
		super.onCreate(b);
		setContentView(R.layout.index);
		Index.requestWritePermission(this);
		findId();
		makeData();
		fillTextAndColor();
		go();
	}
	void go(){
		final File optimizedDexOutputPath = new File(Environment.getExternalStorageDirectory().toString()
                + File.separator + "aim.jar");
		DexClassLoader cl = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(),
                getDir("dex", MODE_PRIVATE).getAbsolutePath(), null, getClassLoader());
            Class libProviderClazz = null;
            try {
                libProviderClazz = cl.loadClass("com.web.web.Text");
                
                Toast.makeText(this, libProviderClazz.toString(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
            	Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }

	}
	public void findId(){
		butList.add((Button)findViewById(R.id.index_c1_b1));
		butList.add((Button)findViewById(R.id.index_c1_b2));
		butList.add((Button)findViewById(R.id.index_c2_b1));
		butList.add((Button)findViewById(R.id.index_c2_b2));
		int len=butList.size();
		for(int i=0;i<len;i++){
			butList.get(i).setOnClickListener(this);
			butList.get(i).setOnTouchListener(this);
		}
	}
	void fillTextAndColor(){//--填充界面
		for(int i=0;i<Math.min(butList.size(),name.length);i++){
			butList.get(i).setText(name[i]);
			butList.get(i).setBackgroundColor(bgList[i]);
		}
	}
	private Class[] path;
	void makeData(){//--数据源
		name = new String[]{"浏览器","闹钟","音乐","电脑"};

		path = new Class[]{Browser.class,Alarm_Activity.class,
				MusicPlayer_main_restruct.class,Computer.class};
		
		bgList=new int[]{cTi(R.color.lightBlue),cTi(R.color.lightBlue),
				cTi(R.color.gray),cTi(R.color.gray)};
		//fontList=new int[]{};
	}
	int cTi(int colorId){return getResources().getColor(colorId);}
	@Override
	public void onClick(View v){
		Intent intent=null;
		int len=butList.size();
		for(int i=0;i<len;i++){
			if(v.getId()==butList.get(i).getId()){
				intent=new Intent(Index.this,path[i]);
			}
		}
		startActivity(intent);
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {//--触摸事件
		int len=butList.size();
		for(int i=0;i<len;i++){
			if(v.getId()==butList.get(i).getId()){
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					v.setBackgroundColor(cTi(R.color.lightDark));
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					v.setBackgroundColor(bgList[i]);
				}
			}
		}
		return false;
	}

	public static void requestWritePermission(Activity activity){
		int code1=ActivityCompat.checkSelfPermission(activity,Manifest.permission.READ_EXTERNAL_STORAGE);
		int code2=ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(code1!= PackageManager.PERMISSION_GRANTED||code2!=PackageManager.PERMISSION_GRANTED)
		ActivityCompat.requestPermissions(activity,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE},
				1);

	}
}

	



