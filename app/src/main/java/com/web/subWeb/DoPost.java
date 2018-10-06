package com.web.subWeb;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class DoPost {
	public String  connect(String url,String params) throws IOException {
		URL path=new URL(url);
		URLConnection connection=path.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		
		PrintWriter pw=new PrintWriter(connection.getOutputStream());
		pw.write(params);
		pw.flush();
		
		
		InputStreamReader isr=new InputStreamReader(connection.getInputStream(),"GBK");
		char buffer[]=new char[1024];
		int offset;
		StringBuffer str=new StringBuffer();
		while((offset=isr.read(buffer))>0){
			str.append(buffer,0,offset);
		}
		
		
		isr.close();
		pw.close();
		return str.toString();
	}
}
