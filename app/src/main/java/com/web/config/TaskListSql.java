package com.web.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TaskListSql {
	String dataBaseName="downloadList",tableName="list";
	Context context;
	SQLiteDatabase sql;
	GetFiles gFiles=new GetFiles();
	String[] columns=new String[]{"id","name","singer","hasDownload","fullSize","hash","statu"};
	public TaskListSql(Context context){
		this.context=context;
		sql=context.openOrCreateDatabase(dataBaseName, Context.MODE_APPEND, null);
	}
	public List<Map<String, String>> createTaskList(){//--从数据库生成taskList
		List<Map<String, String>> taskList=new ArrayList<Map<String,String>>();
		sql.execSQL("create table if not exists "+tableName+"(id INTEGER PRIMARY KEY,name char(20),singer char(20),hasDownload char(20),fullSize char(20),hash char(50),statu char(2))");
		Cursor cursor=sql.query(tableName, new String[]{"name,singer,hasDownload,fullSize,hash,statu"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			Map<String, String> map=new HashMap<String, String>();
			map.put("name", cursor.getString(0));
			map.put("singer", cursor.getString(1));
			map.put("hasDownload", cursor.getString(2));
			map.put("fullSize", cursor.getString(3));
			map.put("hash", cursor.getString(4));
			map.put("statu", cursor.getString(5));
			taskList.add(map);
		}
		cursor.close();
		return taskList;
	}
	public boolean storageTaskList(Map<String, String> map){//--存储一个任务
		sql.execSQL("create table if not exists "+tableName+"(id INTEGER PRIMARY KEY,name char(20),singer char(20),hasDownload char(20),fullSize char(20),hash char(50),statu char(2))");
		Cursor cursor=sql.query(tableName, new String[]{"hash"}, null, null, null, null, null);
		while(cursor.moveToNext()){//--判断是否存在
			if(map.get("hash").equals(cursor.getString(0)))return false;//--存在同一个hash，返回
		}
		ContentValues values=new ContentValues();
		values.put("name", map.get("name"));
		values.put("singer", map.get("singer"));
		values.put("hasDownload", 0);
		values.put("fullSize", map.get("fullSize"));
		values.put("hash", map.get("hash"));
		values.put("statu", map.get("statu"));
		sql.insert(tableName, null,values);
		return true;
	}
	public Map<String, String> select(String hash){//--读取一条信息
		Map<String, String> map=new HashMap<String, String>();
		try{
			Cursor cursor=sql.query(tableName,columns , "hash=?", new String[]{hash}, null, null, null);
			if(cursor.moveToNext()){
				for(int i=0;i<columns.length;i++){
					map.put(columns[i], cursor.getString(i));
				}
			}
			cursor.close();//--必须关闭，不然打开次数多了会内存泄漏
		}catch(Exception e){
			gFiles.write(GetFiles.rootPath+"log.txt", "sql select:"+e+"\r\n", true);
		}
		
		return map;
	}
	public void delTaskList(String hash){//--删除一条记录
		sql.execSQL("create table if not exists "+tableName+"(id INTEGER PRIMARY KEY,name char(20),singer char(20),hasDownload char(20),fullSize char(20),hash char(50),statu char(2))");
		sql.delete(tableName, "hash=?",new String[]{hash});
	}
	public void upData(String hash,String key,String value){//--更新某一个值
		try{
			ContentValues values=new ContentValues();
			values.put(key,value);
			sql.update(tableName, values, "hash=?",new String[]{hash});
		}catch(Exception e){
			gFiles.write(GetFiles.rootPath+"log.txt", "sql updata:"+e+"\r\n", true);
		}
		
	}
	public void delTable(){//--删除 表
		sql.execSQL("drop table if exists "+tableName);
	}
	public void close(){
		sql.close();
	}
}
