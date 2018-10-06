package com.web.config;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled") 
public class WebConfig {
	Context context;
	public WebView web;
	public EditText searchInput;
	public GetFiles gfFiles=new GetFiles();
	float[] point=new float[2];
	ArrayList<String> blackList=new ArrayList<String>();
 	public WebConfig(Context context,WebView v,EditText searchInput){
		this.context=context;
		web=v;
		this.searchInput=searchInput;
		WebSettings set=web.getSettings();
        set.setDefaultTextEncodingName("utf-8");
        set.setJavaScriptEnabled(true);
        set.setAppCacheEnabled(false);
        set.setAppCachePath(GetFiles.cachePath);
        set.setDomStorageEnabled(true); 
        set.setAllowFileAccess(true);
        set.setSupportZoom(true); 
        set.setDisplayZoomControls(false);
        set.setBuiltInZoomControls(true);
        set.setUseWideViewPort(true);
        
        web.setWebChromeClient(new chrome());
        web.setWebViewClient(new loadURL());
        getBlackList();
	}
 	public void BackOrForward(View v, MotionEvent event){//--左右滑动进行前进后退
 		if(event.getAction()==MotionEvent.ACTION_UP){
			if(point[0]-event.getX()<-100&&Math.abs(point[1]-event.getY())<50&&web.canGoBack()){
				web.goBack();
			}else if(point[0]-event.getX()>100&&Math.abs(point[1]-event.getY())<50&&web.canGoForward()){
				web.goForward();
			}
		}else if(event.getAction()==MotionEvent.ACTION_DOWN){
			point[0]=event.getX();
			point[1]=event.getY();
		}
 	}
	public void getBlackList(){//--get黑名单
    	SQLiteDatabase sql=context.openOrCreateDatabase("downloadList", Context.MODE_PRIVATE, null);
    	sql.execSQL("create table if not exists blackList (webSite char(200) PRIMARY KEY)");
    	Cursor cursor=sql.query("blackList", new String[]{"webSite"}, null, null, null, null, null);
    	blackList.clear();
    	while(cursor.moveToNext()){
    		blackList.add(cursor.getString(0));
    	}
    }
	public void insertARecord(String webSite){//--插入一个记录
		SQLiteDatabase sql=context.openOrCreateDatabase("downloadList", Context.MODE_PRIVATE, null);
    	sql.execSQL("create table if not exists blackList (webSite char(200) PRIMARY KEY)");
    	ContentValues values=new ContentValues();
    	values.put("webSite", webSite);
    	sql.replace("blackList", "webSite="+webSite,values);
    	getBlackList();//---刷新黑名单【可以直接在blackList里面插入[需要判断是否重复][用一个空间存储上一个url判断是否重复]】;
    	show("已加入拦截名单：\n"+webSite);
	}
	public void show(String str){
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}
	class chrome extends WebChromeClient{//--负责对页面事件进行处理
    	public boolean onJsAlert (WebView view, String url, String message, JsResult result){
    		//Toast.makeText(Music.this, message+","+url, Toast.LENGTH_LONG).show();
    		//result.confirm();//---处理自动结果
    		return false;//--返回false系统自己解决，true需要自己写
    	}
    }
	class loadURL extends WebViewClient{//---负责打开网页
    	@Override
    	public boolean shouldOverrideUrlLoading(WebView view,String url_){
    		for(int i=0;i<blackList.size();i++){
    			if(url_.equals(blackList.get(i))){
    				show("拦截网址,拒绝访问");
    				return true;
    			}
    		}
    		searchInput.setText(url_);//--显示url
    		return false;
    	}
    }
}
