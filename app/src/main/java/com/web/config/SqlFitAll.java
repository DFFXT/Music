package com.web.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
public class SqlFitAll {//--试用所有sql操作
	public SQLiteDatabase sql;
	public Context context;
	public String DB,TB;
	public SqlFitAll(Context context,String DB,String TB){
		this.context=context;
		this.DB=DB;
		this.TB=TB;
	}
	public void connection(String CreatedColunms){//--连接
		sql=context.openOrCreateDatabase(DB, Context.MODE_APPEND, null);
		if(!CreatedColunms.equals(null)){
			sql.execSQL("create table if not exists "+TB+" ("+CreatedColunms+")");
		}
	}
	public ArrayList<Map<String, String>> getAll(String columns[]){//--获取所有数据
		ArrayList<Map<String, String>> data=new ArrayList<Map<String,String>>();
		Cursor cursor=sql.query(TB, columns, null, null, null,null, null);
		int count=cursor.getColumnCount();
		while(cursor.moveToNext()){
			Map<String, String> map=new HashMap<String, String>();
			for(int i=0;i<count;i++){
				map.put(cursor.getColumnName(i), cursor.getString(i));
			}
			data.add(map);
		}
		return data;
	}
	public void insert(String colums,String values[]){//--插入
		int len=values.length,i=0;
		String val="";
		for(;i<len;i++){
			val+=values[i]+",";
		}
		if(val.length()>0){//--去除最后的逗号
			val=val.substring(0,val.length()-1);
		}
		sql.execSQL("insert into "+TB+" ("+colums+")values("+val+")");
	}
	public void insert(String[] colums,String[] values){//--插入记录
		if(getRecords(colums, values).size()==0){//--不存在记录
			ContentValues contentValues=new ContentValues();
			int len=colums.length;
			String range="";
			for(int i=0;i<len;i++){
				contentValues.put(colums[i], values[i]);
				range+=colums[i]+"=? and ";
			}
			if(range.length()>0){
				range=range.substring(0,range.length()-4);
			}
			sql.insert(TB, range, contentValues);
		}
	}
	public ArrayList<Map<String, String>> getRecords(String[] colums,String[] values){//--按条件获取数据
		ArrayList<Map<String, String>> record=new ArrayList<Map<String,String>>();
		int len=colums.length;
		String selection="";
		for(int i=0;i<len;i++){
			selection+=colums[i]+"=? and ";
		}
		if(selection.length()>0){
			selection=selection.substring(0,selection.length()-4);
		}
		Cursor cursor=sql.query(TB,colums , selection, values, null, null, null);
		int count=cursor.getColumnCount();
		while(cursor.moveToNext()){
			Map<String, String> map=new HashMap<String, String>();
			for(int i=0;i<count;i++){
				map.put(cursor.getColumnName(i), cursor.getString(i));
			}
			record.add(map);
		}
		return record;
	}
	public void delList(String key,String value){
		sql.delete(TB, key+"=?", new String[]{value});
	}
	public void delTB(String TB){
		sql.execSQL("drop table if exists "+TB);
	}
	public void close(){
		sql.close();
	}
}
