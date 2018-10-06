package com.web.web;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.web.config.FileListCollection;
import com.web.config.GetFiles;
import com.web.config.L;
import com.web.config.SqlFitAll;
import com.web.subWeb.VideoPlay;
import com.web.util.BaseActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "InlinedApi"})
public class Computer extends BaseActivity implements OnClickListener{
	TextView computer_titleBar_text;
	ListView computer_lv;
	EditText computer_port;
	AutoCompleteTextView computer_ip;
	Button computer_conButton;
	LinearLayout computer_toolBox;
	RelativeLayout computer_toolBox_copy,computer_toolBox_cut,computer_toolBox_del,
		computer_toolBox_rename,computer_toolBox_more;//--复制，删除等控件
	JSONArray jsonArray;
	ArrayList<FileListCollection> data;//---显示的数据
	ArrayList<Map<String, String>> ipMap=new ArrayList<Map<String,String>>();
	L adapter;
	Socket socket;
	Thread thread;
	MyHander myHander;
	Comparator<ArrayList<Map<String, Object>>> comparator;
	Config config=new Config();
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.computer);
		myHander=new MyHander(this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		findId();
		setListenner();
		render();
	}
	void findId(){
		computer_titleBar_text=(TextView)findViewById(R.id.computer_titleBar_text);
		computer_ip=(AutoCompleteTextView)findViewById(R.id.computer_ip);
		computer_port=(EditText)findViewById(R.id.computer_port);
		computer_conButton=(Button)findViewById(R.id.computer_conButton);
		computer_lv=(ListView)findViewById(R.id.computer_lv);
		
		computer_toolBox=(LinearLayout)findViewById(R.id.computer_toolBox);
		computer_toolBox_copy=(RelativeLayout)findViewById(R.id.computer_toolBox_cpoy);
		computer_toolBox_cut=(RelativeLayout)findViewById(R.id.computer_toolBox_cut);
		computer_toolBox_del=(RelativeLayout)findViewById(R.id.computer_toolBox_del);
		computer_toolBox_rename=(RelativeLayout)findViewById(R.id.computer_toolBox_rename);
		computer_toolBox_more=(RelativeLayout)findViewById(R.id.computer_toolBox_more);
	}
	void render(){//--界面生成
		SqlFitAll sqlFitAll=new SqlFitAll(this, "DB", "ipcon");
		sqlFitAll.connection("ip char(15),port char(6)");
		ipMap=sqlFitAll.getAll(null);
		int i=0,j=ipMap.size();
		String ip[]=new String[j];
		for(;i<j;i++){
			ip[i]=ipMap.get(i).get("ip");
		}
		sqlFitAll.close();
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,  R.layout.music_item_list, R.id.musicName,
				ip);
		computer_ip.setAdapter(adapter);
		computer_ip.setDropDownVerticalOffset(10);
	}
	void renderList(){
		adapter=new L(this, data, R.layout.file_item);
		computer_lv.setAdapter(adapter);
	}
	void setListenner(){//--设置监听器
		computer_titleBar_text.setOnClickListener(this);
		computer_conButton.setOnClickListener(this);
		computer_ip.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int i=0,len=ipMap.size();
				String ip=computer_ip.getText().toString();
				for(;i<len;i++){
					if(ip.equals(ipMap.get(i).get("ip"))){
						computer_port.setText(ipMap.get(i).get("port"));
						break;
					}
				}
			}
		});
		computer_lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(config.longClicked){
					if(!config.selected.get(position,false)){
						view.setBackgroundColor(config.colorLBule);
						config.selected.put(position, true);
					}else{
						view.setBackgroundColor(config.colorWhite);
						config.selected.put(position, false);
					}
					return;
				}
				if(data.get(position).type.equals("folder")){
					config.visited.add( data.get(position).abpath);
					send("fileList",data.get(position).abpath);
				}else{//--打开文件
					if(!data.get(position).abpath.startsWith("D:"))return;
					for(int i=0;i<config.videoSuffix.length;i++){
						if(data.get(position).name.endsWith(config.videoSuffix[i])){
							Intent intent=new Intent(Computer.this,VideoPlay.class);
							intent.setAction("onlineVideo");
							intent.putExtra("ip", Config.ip);
							intent.putExtra("port", Config.port);
							intent.putExtra("path", data.get(position).abpath);
							startActivity(intent);
							return;
						}
					}
				}
				
			}
		});
		computer_lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				toggleToolBox("open");
				if(!config.longClicked){
					view.setBackgroundColor(config.colorLBule);
					config.longClicked=true;
					config.selected.put(position,true);
				}
				return true;
			}
			
		});
		computer_toolBox_copy.setOnClickListener(this);
		computer_toolBox_cut.setOnClickListener(this);
		computer_toolBox_del.setOnClickListener(this);
		computer_toolBox_rename.setOnClickListener(this);
		computer_toolBox_more.setOnClickListener(this);
	}
	public void recoverListColor(){//--恢复listview颜色
		int len=data.size();
		for(int i=0;i<len;i++){
			if(config.selected.get(i,false)){
				if(adapter.viewList.get(i,null)!=null)
					adapter.viewList.get(i).setBackgroundColor(config.colorWhite);
			}
		}
	}
	public void onClick(View v){
		JSONObject jsonObject=new JSONObject();
		try{
			switch(v.getId()){
			case R.id.computer_titleBar_text:{
				toggleInputBox();
			};break;
			case R.id.computer_conButton:{
				computer_ip.clearFocus();
				getData();
				render();//--有可能记录里面增加
			};break;
			case R.id.computer_toolBox_cpoy:{//---复制
				show("服务端未更新该功能！");
				if(config.longClicked==false)return;
				if(config.copySelected&&config.visited.size()!=0){//--粘贴
					jsonObject.put("fileName", fileSelectedName());
					jsonObject.put("operate","copy");
					jsonObject.put("newPath", config.visited.get(config.visited.size()-1));
					config.copySelected=false;
				}else{//--复制
					config.copySelected=true;
					config.longClicked=false;
				}
				
			}break;
			case R.id.computer_toolBox_cut:{//---剪切
				if(config.longClicked==false)return;
			}break;
			case R.id.computer_toolBox_del:{//---删除
				if(config.longClicked==false)return;
				jsonObject.put("fileName", fileSelectedName());
				jsonObject.put("operate", "delete");
				send("fileOperate", jsonObject.toString());
			}break;
			case R.id.computer_toolBox_rename:{//---重命名
				if(config.longClicked==false||fileSelectedName().length()==0)return;
				renameDialog("rename");
				
			}break;
			case R.id.computer_toolBox_more:{//---
				moreButtonPressed(this,v);
			}break;
			}
		}catch(Exception e){}
		
	}
	JSONArray fileSelectedName(){//--将选中的文件转换为list
		JSONArray selectedList=new JSONArray();
		int len=data.size();
		for(int i=0;i<len;i++){
			if(config.selected.get(i,false)){
				selectedList.put(data.get(i).abpath);
			}
		}
		return selectedList;
	}
	void toggleToolBox(String flag){//--toolbox的显示切换
		LayoutParams lp=(LayoutParams)computer_toolBox.getLayoutParams();
		if(lp.height==0&&!flag.equals("close")||flag.equals("open")){
			lp.height=LayoutParams.WRAP_CONTENT;
			TextView computer_copyOrPaste=(TextView)findViewById(R.id.computer_copyOrPaste);
			if(config.copySelected){//--显示粘贴
				computer_copyOrPaste.setText(R.string.computer_toolBox_paste);
			}else{//---显示复制
				computer_copyOrPaste.setText(R.string.computer_toolBox_copy);
			}
		}else{
			lp.height=0;
		}
		computer_toolBox.setLayoutParams(lp);
	}
	void toggleInputBox(){//--关闭输入框
		LinearLayout linearLayout=(LinearLayout)findViewById(R.id.computer_inputBox);
		LayoutParams params= (LayoutParams)linearLayout.getLayoutParams();
		RelativeLayout.LayoutParams butparams= (RelativeLayout.LayoutParams)computer_conButton.getLayoutParams();
		if(params.height!=0){//--关闭键盘、输入框
			params.height=0;
			butparams.height=0;
			butparams.topMargin=0;
			butparams.bottomMargin=0;
			closeKeybord();
		}else{
			params.height=LayoutParams.WRAP_CONTENT;
			butparams.height=LayoutParams.WRAP_CONTENT;
			butparams.topMargin=20;
			butparams.bottomMargin=20;
		}
		computer_conButton.setLayoutParams(butparams);
		linearLayout.setLayoutParams(params);
	}
	void closeKeybord(){//--关闭键盘
		InputMethodManager ipmm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		ipmm.hideSoftInputFromWindow(computer_ip.getWindowToken(), 0);
		ipmm.hideSoftInputFromWindow(computer_port.getWindowToken(), 0);
	}
	void moreButtonPressed(Context context,View v){//--子菜单
		Dialog dialog=new Dialog(context,R.style.DialogTransparentAround);
		View view=getLayoutInflater().inflate(R.layout.listview,(ViewGroup)v,false);
		ListView listView=(ListView)view.findViewById(R.id.listview_listview);
		String selections[]={"新文件夹","上传","下载","退出"};
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,R.layout.onetext,R.id.oneText_oneText,selections);
		listView.setAdapter(adapter);
		dialog.setContentView(view);
		Window window=dialog.getWindow();
		window.setGravity(Gravity.END|Gravity.BOTTOM);
		WindowManager.LayoutParams lp= window.getAttributes();
		lp.width=300;
		lp.y=120;
		window.setAttributes(lp);
		dialog.show();
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch(position){
				case 0:{//--新建文件夹
					renameDialog("createFolder");
				}break;
				case 1:{//--文件上传
					//弹出文件选择框
					fileChoose();
				}break;
				case 2:{//--下载
					if(fileSelectedName().length()==1){
						try {
							send("download", fileSelectedName().get(0).toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}break;
				case 3:finish();break;
				}
			}
		});
	}
	void renameDialog(final String usefor){//---重命名框
		final Dialog dialog=new Dialog(this,R.style.DialogTransparentAround);
		View v=dialog.getCurrentFocus();
		View view=getLayoutInflater().inflate(R.layout.computer_rename,( ViewGroup)v,false);
		dialog.setContentView(view);
		dialog.show();
		final EditText newNameInput=(EditText)view.findViewById(R.id.computer_renameInput);
		Button OK=(Button)view.findViewById(R.id.computer_renameOK);
		OK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try{
				if(usefor.equals("rename")){
					JSONObject jsonObject=new JSONObject();
					JSONArray jsonArray=new JSONArray();
					String fileName=newNameInput.getText().toString();
					if(!check(fileName)){
						show("存在非法字符");
						return;
					}
					jsonArray.put(fileName);
					jsonObject.put("fileName", fileSelectedName());
					jsonObject.put("operate", "rename");
					jsonObject.put("newName", jsonArray.toString());
					send("fileOperate", jsonObject.toString());
					
				}else if(usefor.equals("createFolder")){
					JSONObject jsonObject=new JSONObject();
					jsonObject.put("operate", "newFolder");
					String folderName=newNameInput.getText().toString();
					//--文件夹合法检测
					if(!check(folderName)){
						show("存在非法字符");
						return;
					}
					jsonObject.put("folderName",config.visited.get(config.visited.size()-1)+"/"+folderName );
					send("fileOperate", jsonObject.toString());
				}
				}catch(Exception e){
				}finally{
					dialog.hide();
				}
			}
		});
		Button cancel=(Button)view.findViewById(R.id.computer_renameCancel);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.hide();
			}
		});
	}
	boolean check(String str){//--字符串检查
		char invalidCode[]=new char[]{'/','?','&','\\',':','"','>','<','|'};
		for(int i=0;i<invalidCode.length;i++){
			if(str.indexOf(invalidCode[i])!=-1) return false;
		}
		return true;
	}
	void fileChoose(){///--文件选择器
		Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, 1);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data){//--文件选择回调
		if(resultCode==Activity.RESULT_OK){
			config.uploadFile=data.getData().getPath();
			int len=config.visited.size();
			if(len==0)return;
			send("upload", config.visited.get(len-1));
		}
	}
	void refresh(){//--刷新本界面
		String scheme=null,content="";
		int len=config.visited.size();
		if(len==0) scheme="fileRootList";
		else {
			scheme="fileList";
			content=config.visited.get(len-1);
		}
		send(scheme, content);
	}
	void getData(){//---获取连接数据
		final String host=computer_ip.getText().toString();
		final String port_=computer_port.getText().toString();
		if(port_.equals("")) return;
		final int port=Integer.parseInt(port_);
		if(port>65535) return ;
		Thread thread_con=new Thread(new Runnable() {
			public void run() {
				try {
					socket=new Socket(host,port);
					send("fileRootList", "");
					Config.ip=host;
					Config.port=port;
					myHander.sendEmptyMessage(2);
					SqlFitAll sqlFitAll=new SqlFitAll(Computer.this, "DB", "ipcon");
					sqlFitAll.connection("ip char(15),port char(6)");
					sqlFitAll.insert(new String[]{"ip","port"}, new String[]{host,port_});
					config.visited.clear();
				} catch (Exception e) {
					Message msg=new Message();
					msg.obj=e;
					msg.what=3;
					myHander.sendMessage(msg);
				} 
			}
		});
		thread_con.start();	
	}
	void downloadFile(DataInputStream dis) throws IOException{//---下载文件
		if(config.downloading){
			sendMessage("存在任务正在下载", 3);
			return;
		}
		config.downloading=true;
		FileOutputStream fos=new FileOutputStream(GetFiles.cachePath+dis.readUTF());
		long len=dis.readLong();
		int i=0,bufLen=1024,tmp=0;
		byte byt[]=new byte[bufLen];//--可以加速
		while(i<len){
			tmp=dis.read(byt);
			fos.write(byt,0,tmp);
			i+=tmp;
		}
		fos.flush();
		fos.close();
		config.downloading=false;
		sendMessage("下载完成", 3);
	}
	void uploadStart(DataOutputStream dos) throws IOException{//--文件上传开始
		File file2=new File(config.uploadFile);
		if(!file2.exists()){
			sendMessage("文件不存在", 3);
		}
		BufferedInputStream bis=new BufferedInputStream(new FileInputStream(file2));
		dos.writeUTF(file2.getName());
		dos.writeLong(file2.length());
		int len;
		byte byt[]=new byte[1024];
		while((len=bis.read(byt))>0){
			dos.write(byt,0,len);
		}
		bis.close();
		dos.flush();
	}
	
	void send(final String scheme,final String content){///---信息接收
		thread=new Thread(new Runnable() {
			public void run() {
				try{
					DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
					dos.writeUTF(scheme);
					dos.writeUTF(content);
					if(scheme.equals("upload")){//--上传文件
						uploadStart(dos);
					}
					dos.flush();
					DataInputStream dis=new DataInputStream(socket.getInputStream());
					String scheme=dis.readUTF();
					if(scheme.equals("fileRootList")){//---取根目录
						jsonArray=new JSONArray(dis.readUTF());
						data=jsonToList(jsonArray);
					}else if(scheme.equals("fileList")){//---取目录
						jsonArray=new JSONArray(dis.readUTF());
						data=jsonToList(jsonArray);
					}else if(scheme.equals("download")){//---下载文件
						downloadFile(dis);
						return;
					}else if(scheme.equals("msg")){//---服务器信息
						sendMessage(dis.readUTF(), 3);
						refresh();//--刷新界面
						return;
					}
					else{
						sendMessage("服务器返回了未知的信息头："+scheme, 3);
					}
					myHander.sendEmptyMessage(1);
				}catch(Exception e){
					sendMessage(e.toString(), 3);
				}
			}
		});thread.start();
	}

	void sendMessage(String obj,int what){
		Message msg=new Message();
		msg.obj=obj;
		msg.what=what;
		myHander.sendMessage(msg);
	}
	ArrayList<FileListCollection> jsonToList(JSONArray jsonArray){//--将json转换为Arraylist
		ArrayList<FileListCollection> data_new=new ArrayList<FileListCollection>();
		try{
			DecimalFormat format=new DecimalFormat("0.00");
			int len=jsonArray.length(),i=0;
			for(;i<len;i++){
				JSONObject j=jsonArray.getJSONObject(i);
				long size=j.getLong("size");
				String s,name=j.getString("name");
				if(size<1024){//--单位换算
					s=size+"B";
				}else if(size<1024*1024){
					s=format.format(size/1024.0)+"KB";
				}else if(size<1024*1024*1024){
					s=format.format(size/1024.0/1024)+"MB";
				}else{
					s=format.format(size/1024.0/1024/1024)+"GB";
				}
				int maxlen=22;
				if(name.getBytes().length>maxlen*2){
					name=new String(name.getBytes(),0,maxlen*2);
					if(name.substring(name.length()-1).equals("?")){
						name=name.substring(0,name.length()-1);
					}
					name=name+"...";
				}
				FileListCollection per=new FileListCollection(name,j.getString("abpath"),j.getString("type"),
						s, j.getInt("priority"), j.getLong("date"));
				data_new.add(per);
			}
			Collections.sort(data_new,new Comp());
		}catch(Exception e){
			Message msg=new Message();
			msg.what=3;
			msg.obj=e;
			myHander.sendMessage(msg);
		}
		return data_new;
	}
	void show(String str){//--show()
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	public boolean onKeyDown(int keycode,KeyEvent keyEvent){//--按键
		if(keycode==KeyEvent.KEYCODE_BACK){//--返回键处理
			if(config.longClicked) {//--清除选中
				config.longClicked=false;
				recoverListColor();
				config.selected=new SparseBooleanArray();
			}
			try {
				LayoutParams lp=(LayoutParams)computer_toolBox.getLayoutParams();
				if(lp.height!=0){
					toggleToolBox("close");
					return true;
				}else if(config.visited.size()>1){
					config.visited.remove(config.visited.size()-1);
					send("fileList", config.visited.get(config.visited.size()-1));
					return true;
				}else if(config.visited.size()==1){
					config.visited.remove(0);
					send("fileRootList", "");
					return true;
				}
				else{
					if(socket!=null)
						socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(keycode==KeyEvent.KEYCODE_MENU){
			if(socket!=null){
				toggleToolBox("");
			}
			return true;
		}
		return super.onKeyDown(keycode, keyEvent);
	}
}
class MyHander extends Handler{
	private WeakReference<Computer> act;
	MyHander(Computer activity){
		act=new WeakReference<Computer>(activity);
	}
	public void handleMessage(Message msg){
		Computer activity=act.get();
		if(activity!=null){
			// 1：取得对方回应；2：连接成功；3：连接失败
			switch(msg.what){
			case 1:activity.renderList();break;
			case 2:activity.toggleInputBox();break;
			case 3:activity.show(msg.obj+"");break;
			}
		}
	}
}
class Comp implements Comparator<FileListCollection>{
	public int compare(FileListCollection data1, FileListCollection data2) {
		int data1_p=(Integer)data1.priority;
		int data2_p=(Integer)data2.priority;
		return data1_p-data2_p;
	}
}
class Config{
	ArrayList<String> visited=new ArrayList<String>();
	boolean longClicked=false;
	int colorLBule=0x93bff1ef;
	int colorTransparent=0x00000000;
	int colorWhite=0xffffffff;
	SparseBooleanArray selected=new SparseBooleanArray();
	boolean copySelected=false;
	boolean downloading=false;
	String uploadFile=null;
	
	static String ip="";
	static int port=0;
	String videoSuffix[]={".mp4",".avi",".mkv",".rm",".wmv",".mov","3gp",".mp3"};
}

