package com.web.web;

import java.lang.ref.WeakReference;

import org.json.JSONObject;

import com.web.config.DataBaseC;
import com.web.service.MusicPlay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 音乐播放器登陆功能
 * @author home
 *
 */
public class MusicLogin extends Activity implements OnClickListener{
	EditText countV,passwordV;
	Button logButton;
	final String IP="127.0.0.1";
	final int PORT=3306;
	final String DB="test";
	SHOW hander;
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.musiclogin);
		findView();
		setLitener();
		hander=new SHOW(MusicLogin.this);
	}
	/**
	 * 获取各个组件
	 */
	void findView(){
		countV=(EditText)findViewById(R.id.musiclog_count);
		passwordV=(EditText)findViewById(R.id.musiclog_pw);
		logButton=(Button)findViewById(R.id.musicLog_logButton);
	}
	/**
	 * 给组件设置监听器
	 */
	void setLitener(){
		logButton.setOnClickListener(this);
	}
	/**
	 * 实现的OnClickListener接口的方法
	 */
	public void onClick(View v){
		switch(v.getId()){
		case R.id.musicLog_logButton:{
			countLog();
			break;
		}
		}
	}
	/**
	 * 登陆操作
	 */
	void countLog(){
		final String count = countV.getText().toString();
		final String password = passwordV.getText().toString();
		if(count.length()<6||password.length()<6){//--账号密码长度不符
			show("长度必须不小于6");
			return;
		}
		new Thread(new Runnable() {
			public void run() {
				try {
					DataBaseC dataBaseC=new DataBaseC();
					JSONObject json=new JSONObject(dataBaseC.login(count, password));
					if(json.getBoolean("status")){//--登录成功
						saveCount(count,password);
						tellService();
						finish();
					}else if(json.getInt("error")==0){//--登录不成功
						hander.sendMessage(makeMsg("no such count", 0));
					}else if(json.getInt("error")==1){
						hander.sendMessage(makeMsg("password error", 0));
					}
				} catch (Exception e) {
					hander.sendMessage(makeMsg(e.toString(), 0));
				}
			}
		}).start();
		
	}
	/**
	 * 保存账号信息
	 * 明文存储不保密，需要加密
	 */
	void saveCount(String count,String pw){
		SharedPreferences p=getSharedPreferences("countTable",MODE_PRIVATE);
		Editor editor=p.edit();
		editor.putString("count", count);
		editor.putString("password", pw);
		editor.putBoolean("alive", true);
		editor.putLong("date", System.currentTimeMillis());
		editor.apply();
	}
	/**
	 * 登录成功，通知后台服务
	 */
	void tellService(){
		Intent intent=new Intent(this,MusicPlay.class);
		intent.setAction("login");
		startService(intent);
	}
	/**
	 * Toast提示
	 */
	void show(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	/**
	 * 快速生成Message
	 * @param str
	 * @param what
	 * @return
	 */
	Message makeMsg(String str,int what){
		Message msg=new Message();
		msg.what=what;
		msg.obj=str;
		return msg;
	}
}
class SHOW extends Handler{
	WeakReference<MusicLogin> reference;
	public SHOW(MusicLogin musicLogin) {
		reference=new WeakReference<MusicLogin>(musicLogin);
	}
	public void handleMessage(Message msg){
		MusicLogin musicLogin=reference.get();
		switch (msg.what) {
		case 0:
			musicLogin.show(msg.obj.toString());
			break;

		default:
			break;
		}
	}
}
