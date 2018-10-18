package com.web.music.model.lyrics.model;

public class LyricsLine{
	//**当前歌词
	private String line;
	//**歌词显示的时间
	private int time;
	//**一行歌词的长度
	private int width,height;
	private int top;

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}