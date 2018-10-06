package com.web.config;

import java.io.IOException;

import com.web.subWeb.DoPost;

/**
 * 对歌单进行一系列的操作
 * @author home
 *
 */
public class DataBaseC {
	/**
	 * 主机信息
	 */
	private final String host="http://192.168.43.206:8080/";
	/**
	 * 登录地址
	 */
	private final String logPath=host+"Music/Log";
	/**
	 * 获取歌单地址
	 */
	private final String getMusicListPath=host+"Music/GetMusicList";
	/**
	 * 向歌单添加歌曲地址
	 */
	private final String addMusicToListPath=host+"Music/AddMusicToList";
	
	/**
	 * 登录
	 * @param count
	 * @param pw
	 * @return
	 * @throws IOException
	 */
	public String login(String count,String pw) throws IOException{
		DoPost doPost=new DoPost();
		return doPost.connect(logPath,"count="+count+"&password="+pw);
	}
	/**
	 * 添加歌曲到歌单
	 * @param hash 歌曲hash
	 * @param songname 歌曲名称
	 * @param singername 歌手名称
	 * @param count 账号信息
	 * @param pw 密码
	 * @throws IOException 
	 */
	public String AddMusicToList(String hash,String songname,String singername,String count,String pw) throws IOException{
		DoPost doPost=new DoPost();
		return doPost.connect(addMusicToListPath,"count="+count+"&password="+pw);
	}
	/**
	 * 获取歌单列表
	 * @param count 账号
	 * @param pw 密码
	 * @throws IOException 
	 */
	public String getMusicList(String count,String pw) throws IOException{
		DoPost doPost=new DoPost();
		return doPost.connect(getMusicListPath,"count="+count+"&password="+pw);
	}
}
