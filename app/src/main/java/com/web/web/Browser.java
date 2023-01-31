package com.web.web;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.music.m.R;
import com.web.config.WebConfig;
import com.web.subWeb.File_download_sure;
import com.web.subWeb.ProhibitWebSite;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class Browser extends Activity implements OnClickListener{
	private WebView web;
	private EditText searchInput;
	private ImageView reload_clear;
	private ImageButton goBack,goForword,item,Home,forbid,goDownload,refresh,exit,noImage;
	ArrayList<String> nameList=null,abList=null,sizeList=null;
	WebConfig webConfig;
	
	private Map<String, Object> config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.browser);
        init();

        webConfig=new WebConfig(this,web,searchInput);//-----一些设置
       
        Intent intent=getIntent();
        String url=intent.getDataString();
        if(url!=null){//--区分打开方式
        	web.loadUrl(url);
        }else{
    	  //web.loadUrl("https:dffxt.github.io/canvas/canvas.html"); 
    	  //web.loadUrl("http://542517.hjwhp.ghco.info");
    	 // web.loadUrl("http://172.18.114.51/");
        	web.loadUrl("http://m.baidu.com");
        }
    }
    public void init(){
    	searchInput=(EditText)findViewById(R.id.searchInput);
    	reload_clear=(ImageView)findViewById(R.id.reload_clear);
    	web=(WebView) findViewById(R.id.web);
    	noImage=(ImageButton)findViewById(R.id.noImg);
    	goBack=(ImageButton)findViewById(R.id.goGack);
    	goForword=(ImageButton)findViewById(R.id.goForward);
    	item=(ImageButton)findViewById(R.id.B_item);
    	Home=(ImageButton)findViewById(R.id.B_Home);
    	forbid=(ImageButton)findViewById(R.id.forbid);
    	goDownload=(ImageButton)findViewById(R.id.goDownload);
    	refresh=(ImageButton)findViewById(R.id.refresh);
    	exit=(ImageButton)findViewById(R.id.exit);
    	searchInput.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode==KeyEvent.KEYCODE_ENTER&&!searchInput.getText().equals("")){
					searchType(searchInput.getText().toString());	
					searchInput.clearFocus();
					InputMethodManager manager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					return true;
				}
				return false;
			}
		});
    	reload_clear.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				searchInput.setText("");
			}
		});
    	web.setDownloadListener(new DownloadListener() {//--下载操作
			public void onDownloadStart(String path, String client, String name,
					String type, long length) {//--网页文件下载
				String downloadName=null;
				int startP=name.indexOf("=");
				if(startP>=0){//--普通提取下载名
					downloadName=name.substring(startP+1);
					if(downloadName.indexOf("\"")>=0){
						downloadName=downloadName.substring(1,downloadName.length()-1);
					}
				}else{//--path提取下载名
					startP=path.lastIndexOf('/');
					int lastP=path.indexOf('?');
					if(lastP<0)lastP=path.length();
					downloadName=path.substring(startP+1,lastP);
				}
				 Intent intent_=new Intent(Browser.this,File_download_sure.class);
				 intent_.putExtra("url", path);
				 try{//---url解码
					 intent_.putExtra("name", URLDecoder.decode(downloadName,"utf-8"));
				 }catch(Exception e){}
				 intent_.putExtra("length", length);
			     startActivity(intent_);
			}
		});	
    	web.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent e) {
				toggle(false);
				webConfig.BackOrForward(v, e);
				return false;
			}
		});
    	web.setOnLongClickListener(new OnLongClickListener() {///--长按点击
			public boolean onLongClick(View v) {
				HitTestResult f=((WebView) v).getHitTestResult();
				show(f.getType()+"--"+f.getExtra());
				return false;
			}
		});
    	noImage.setOnClickListener(this);
    	goBack.setOnClickListener(this);
    	goForword.setOnClickListener(this);
    	item.setOnClickListener(this);
    	Home.setOnClickListener(this);
    	forbid.setOnClickListener(this);
    	forbid.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				Intent intent=new Intent(Browser.this,ProhibitWebSite.class);
				startActivity(intent);
				return true;
			}
		});
    	goDownload.setOnClickListener(this);
    	exit.setOnClickListener(this);
    	refresh.setOnClickListener(this);
    	
    	config=new HashMap<String, Object>();
    	config.put("ItemsBoxShow", false);
    	config.put("imgMode", true);//有图模式
    }
    
    

    public void searchType(String keyWord){//--判断输入格式
    	if(keyWord.indexOf('.')>0){
    		if(keyWord.indexOf("http://")>=0||keyWord.indexOf("https://")>=0){
    			web.loadUrl(keyWord);
    		}else{
    			web.loadUrl("http://"+keyWord);
    		}
    	}else {
    		web.loadUrl("https://m.baidu.com/s?wd="+keyWord);
    	}
    }
    public void toggle(){//--视图切换
    	toggle(true);
    }
    public void toggle(Boolean type){//--视图切换
    	LinearLayout subItemsBox=(LinearLayout)findViewById(R.id.subItemsBox);
		LinearLayout subItemsBox_1=(LinearLayout)findViewById(R.id.subItemsBox_1);
		ViewGroup.LayoutParams params=subItemsBox.getLayoutParams();//--此处必须为ViewGroup
		ViewGroup.LayoutParams params_1=subItemsBox_1.getLayoutParams();//--此处必须为ViewGroup
		if((Boolean) config.get("ItemsBoxShow")){
			params.height=0;
			params_1.height=0;
			config.put("ItemsBoxShow", false);
		}else{
			params.height=LinearLayout.LayoutParams.WRAP_CONTENT;
			params_1.height=LinearLayout.LayoutParams.WRAP_CONTENT;
			config.put("ItemsBoxShow", true);
		}
		if(!type){
			params.height=0;
			params_1.height=0;
			config.put("ItemsBoxShow", false);
		}
		subItemsBox.setLayoutParams(params);
		subItemsBox_1.setLayoutParams(params_1);
		
    }
    public void toggle_close(){
    	LinearLayout subItemsBox=(LinearLayout)findViewById(R.id.subItemsBox);
		LinearLayout subItemsBox_1=(LinearLayout)findViewById(R.id.subItemsBox_1);
		ViewGroup.LayoutParams params=subItemsBox.getLayoutParams();//--此处必须为ViewGroup
		ViewGroup.LayoutParams params_1=subItemsBox_1.getLayoutParams();//--此处必须为ViewGroup
		if((Boolean) config.get("ItemsBoxShow")){
			params.height=0;
			params_1.height=0;
			config.put("ItemsBoxShow", false);
		}else{
			params.height=LinearLayout.LayoutParams.WRAP_CONTENT;
			params_1.height=LinearLayout.LayoutParams.WRAP_CONTENT;
			config.put("ItemsBoxShow", true);
		}
		subItemsBox.setLayoutParams(params);
		subItemsBox_1.setLayoutParams(params_1);
    }
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if((keyCode==KeyEvent.KEYCODE_BACK)&&web.canGoBack()){
        	web.goBack();
        	searchInput.setText(web.getUrl());
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
    public void show(String str){
    	Toast.makeText(Browser.this, str, Toast.LENGTH_LONG).show();
    }
	@Override
	public void onClick(View v) {// TODO 自动生成的方法存根
		int Id=v.getId();
		if(Id==R.id.goGack){//--返回
			if(web.canGoBack()) web.goBack();
		}else if(Id==R.id.goForward){//--前进
			if(web.canGoForward()) web.goForward();
		}else if(Id==R.id.B_item){//--选项
			toggle();
			return;
		}else if(Id==R.id.noImg){//--有图无图模式切换
			if((Boolean) config.get("imgMode")){
				web.getSettings().setLoadsImagesAutomatically(false);
				config.put("imgMode", false);
				noImage.setImageResource(R.drawable.noimg);
			}else{
				web.getSettings().setLoadsImagesAutomatically(true);
				config.put("imgMode", true);
				noImage.setImageResource(R.drawable.showimg);
			}
		}else if(Id== R.id.B_Home){//--home键
			if(web.canGoBack()){
				web.loadUrl("https://m.baidu.com/");
				searchInput.setText(web.getUrl());
			}
		}else if(Id==R.id.forbid){//--拦截当前网址
			webConfig.insertARecord(web.getUrl());
		}else if(Id==R.id.goDownload){//--进入下载信息界面
			
		}else if(Id==R.id.refresh){//--刷新
			web.reload();
		}else if(Id==R.id.exit){//--退出
			finish();
		}
		toggle(false);
	}
    
}
