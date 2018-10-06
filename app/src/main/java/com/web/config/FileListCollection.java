package com.web.config;

public class FileListCollection {
	public String name,abpath,type,size;
	public long date;
	public int priority; 
	public FileListCollection(String name,String abpath,String type,String size,int priority,long date){
		this.name=name;
		this.abpath=abpath;
		this.type=type;
		this.priority=priority;
		this.date=date;
		this.size=size;
	}
}
