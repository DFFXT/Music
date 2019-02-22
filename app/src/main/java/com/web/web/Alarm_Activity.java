package com.web.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.core.app.NotificationCompat;

@SuppressLint("SimpleDateFormat") public class Alarm_Activity extends Activity{
	Intent intent;
	PendingIntent pendingIntent;
	AlarmManager alarmManager;
	private Calendar calendar;
	private TextView t1_day,t2_hours,t3_minutes,date_show;
	private Button add,minus,timeCancel,timeSure,exit;
	private int flag=0;
	private TextView ringsList;
	Date date=new Date(System.currentTimeMillis());
	String[] DATE=new String[3];
	
	SharedPreferences sp ;
	Editor editor;
	static final int a=0;
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.alarm);
		
		
		
		
		//------通知栏测试
		NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new NotificationCompat.Builder(Alarm_Activity.this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setTicker("showNormal")
        .setContentInfo("contentInfo")
        .setContentTitle("ContentTitle").setContentText("ContentText")
        .setDefaults(Notification.FLAG_AUTO_CANCEL)
        .build();
		manager.notify(11,notification);
		
		ringsList=(TextView)findViewById(R.id.ringsList);//----获取对象
		t1_day=(TextView)findViewById(R.id.Day);
		t2_hours=(TextView)findViewById(R.id.Hours);
		t3_minutes=(TextView)findViewById(R.id.Minutes);
		date_show=(TextView)findViewById(R.id.date_show);
		
		add=(Button)findViewById(R.id.add);
		minus=(Button)findViewById(R.id.minus);
		timeSure=(Button)findViewById(R.id.timeSure);
		timeCancel=(Button)findViewById(R.id.timeCancel);
		exit=(Button)findViewById(R.id.exit);
		
		SimpleDateFormat format=new SimpleDateFormat("dd");//---获取时间
		DATE[0]=format.format(date);
		format=new SimpleDateFormat("HH");
		DATE[1]=format.format(date);
		format=new SimpleDateFormat("mm");
		DATE[2]=format.format(date);
		
		sp = Alarm_Activity.this.getSharedPreferences("SP",Context.MODE_PRIVATE);
		editor=sp.edit();
		ringsList.setText(System.currentTimeMillis()>sp.getLong("0",0)?"null":sp.getString("1", "null"));//---初始化显示
		t1_day.setText(DATE[0]);
		t2_hours.setText(DATE[1]);
		t3_minutes.setText(DATE[2]);
		date_show.setText(DATE[0]+"-"+DATE[1]+":"+DATE[2]);
		
		Click click = new Click();//-----绑定事件
		t1_day.setOnClickListener(click);
		t2_hours.setOnClickListener(click);
		t3_minutes.setOnClickListener(click);
		add.setOnClickListener(click);
		minus.setOnClickListener(click);
		timeSure.setOnClickListener(click);
		timeCancel.setOnClickListener(click);
		exit.setOnClickListener(click);
		
		calendar=Calendar.getInstance();//---calendar初始化
		calendar.setTimeInMillis(System.currentTimeMillis());
		
		intent = new Intent(Alarm_Activity.this,Alarm.class);
		pendingIntent = PendingIntent.getBroadcast(Alarm_Activity.this, 0, intent, 0);
		alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
	}
	class Click implements OnClickListener{//--事件监听器
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.Day:colorC(v);flag=0;break;
				case R.id.Hours:colorC(v);flag=1;break;
				case R.id.Minutes:colorC(v);flag=2;break;
				case R.id.add:add_minus(1);break;
				case R.id.minus:add_minus(-1);break;
				case R.id.timeSure:setRing();break;
				case R.id.timeCancel:cancel();break;
				case R.id.exit:exit();break;
			}
		}
		public void colorC(View v){
			t1_day.setBackgroundColor(Color.BLACK);
			t2_hours.setBackgroundColor(Color.BLACK);
			t3_minutes.setBackgroundColor(Color.BLACK);
			v.setBackgroundColor(Color.WHITE);
		}
		public void add_minus(int type){
			DATE[flag]=""+(Integer.parseInt(DATE[flag])+type);
			switch(flag){//---时间的加减
				case 0:t1_day.setText(DATE[flag]);break;
				case 1:
						if(DATE[flag].equals("24")){
							DATE[flag]="0";
						}else if(DATE[flag].equals("-1")){
							DATE[flag]="23";
						}
						t2_hours.setText(DATE[flag]);break;
				case 2:DATE[flag]=DATE[flag].equals("60")?"0":DATE[flag];
						DATE[flag]=DATE[flag].equals("-1")?"59":DATE[flag];
						t3_minutes.setText(DATE[flag]);break;
			}
			date_show.setText(DATE[0]+"-"+DATE[1]+":"+DATE[2]);
		}
		public void setRing(){
			calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(DATE[0]));
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(DATE[1]));
			calendar.set(Calendar.MINUTE, Integer.parseInt(DATE[2]));
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0); 
			Long minus=calendar.getTimeInMillis()-System.currentTimeMillis();
			if(minus<=0){//----时间滞后时不设置闹钟
				Toast.makeText(Alarm_Activity.this, R.string.timeout, Toast.LENGTH_LONG).show();
				return;
			}
			long Hours=minus/1000/3600;
			long Minutes=(minus/1000-Hours*3600)/60;
			long sss=minus/1000-Hours*3600-Minutes*60;
			Toast.makeText(Alarm_Activity.this, ""+Hours+"小时"+Minutes+"分钟"+sss+"秒", Toast.LENGTH_LONG).show();
			alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
			
			editor.putLong("0", calendar.getTimeInMillis());
			editor.putString("1", DATE[0]+"-"+DATE[1]+":"+DATE[2]);
			editor.commit();
		}
		public void cancel(){
			alarmManager.cancel(pendingIntent);
			editor.clear();
			editor.commit();
		}
		public void exit(){
			System.exit(0);
		}
		
	}

}
