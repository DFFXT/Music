package com.web.config;

import com.web.moudle.music.model.lyrics.model.LyricsLine;

import java.util.ArrayList;

public class LyricsAnalysis {
	//[00:00.00]
	private ArrayList<LyricsLine> lyrics=new ArrayList<>();
	public LyricsAnalysis(String lyrics) {
		int start=0;
		int offset;
		while(true){
			offset=lyrics.indexOf('[',start+10);
			int minute=Integer.parseInt(lyrics.substring(start+1, start+3));
			int second=Integer.parseInt(lyrics.substring(start+4, start+6));
			int msec=Integer.parseInt(lyrics.substring(start+7, start+8));
			LyricsLine line=new LyricsLine();
			//***事件 10毫秒为单位
			line.setTime(minute*600+second*10+msec);
			if(offset==-1){
				line.setLine(lyrics.substring(start+10));
				this.lyrics.add(line);
				break;
			}else{
				line.setLine(lyrics.substring(start+10,offset));
				this.lyrics.add(line);
				start=offset;
			}

		}
	}
	/**
	 * 获取歌词包含时间信息
	 * @return ArrayList<Map<String, String>>
	 */
	public ArrayList<LyricsLine> getLyrics(){
		return lyrics;
	}
}
