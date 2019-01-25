package com.web.subWeb;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class GetInfo{
	public JSONObject get(String hash){
		try{
			String ask="http://m.kugou.com/app/i/getSongInfo2.php?hash="+hash+"&cmd=playInfo";
			JSONObject json=new JSONObject(OpURL(ask));

			String path=json.getString("url");
			String krc=json.getString("hash");
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("url",path);
			jsonObject.put("krc",krc);
			jsonObject.put("img",json.getString("imgUrl"));
			return jsonObject;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public String OpURL(String path) throws Exception{//---以字符串的方式获取改地址的返回值
		URL url=new URL(path);
		URLConnection connection=url.openConnection();
		connection.connect();
		BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
		String line;
		StringBuilder builder=new StringBuilder();
		while((line=br.readLine())!=null){
			builder.append(line);
		}
		br.close();
		return builder.toString();
	}
	public String getKrc(String hash){//----获取歌词
		String ask="http://m.kugou.com/app/i/krc.php?cmd=100&hash="+hash+"&timelength=1";
		try {
			return OpURL(ask);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
}
