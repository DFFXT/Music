package com.web.config;

import com.web.moudle.lyrics.bean.LyricsLine;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricsAnalysis {
    //[00:00.00]
    private ArrayList<LyricsLine> lyricsList = new ArrayList<>();
    private String lyrics;
    private static int timeLength = 10;
    private static Pattern pattern=Pattern.compile("\\[\\d\\d:\\d\\d.\\d{2,3}]");

    public LyricsAnalysis(String lyrics) {
        this.lyrics = lyrics;
        String[] lines = lyrics.split("\n");
        for (String line : lines) {
            Matcher m = pattern.matcher(line);
            if (m.find()) {
                int start = m.start();
                timeLength = m.end() - m.start();
                // add(line, start, start);
                int minute = 0;
                int second = 0;
                int msec = 0;
                try {
                    minute = Integer.parseInt(line.substring(start + 1, start + 3));
                    second = Integer.parseInt(line.substring(start + 4, start + 6));
                    msec = Integer.parseInt(line.substring(start + 7, start + 8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LyricsLine lyricsLine = new LyricsLine();
                //***事件 毫秒为单位
                lyricsLine.setTime((minute * 600 + second * 10 + msec) * 100);

                lyricsLine.setLine(line.substring(start + timeLength).trim());
                this.lyricsList.add(lyricsLine);
            } else {
                LyricsLine lyricsLine = new LyricsLine();
                if (lyricsList.isEmpty()) {
                    lyricsLine.setTime(0);
                } else {
                    lyricsLine.setTime(-1);
                }
                lyricsLine.setLine(line.trim());
                this.lyricsList.add(lyricsLine);
            }
        }
        for (int i = 0; i < lyricsList.size(); i++) {
            LyricsLine line = lyricsList.get(i);
            if (line.getTime() == -1) {
                if (i + 1 < lyricsList.size() - 1) {
                    line.setTime(lyricsList.get(i + 1).getTime());
                } else {
                    line.setTime(lyricsList.get(i - 1).getTime());
                }
            }
        }
    }

    /**
     * 添加一行歌词 [00:00.00]xxxxxx
     *
     * @param preStart 开始处
     * @param end      结束处
     */
    private void add(String lyrics, int preStart, int end) {
        int minute = 0;
        int second = 0;
        int msec = 0;
        if (preStart < 0) return;
        try {
            minute = Integer.parseInt(lyrics.substring(preStart + 1, preStart + 3));
            second = Integer.parseInt(lyrics.substring(preStart + 4, preStart + 6));
            msec = Integer.parseInt(lyrics.substring(preStart + 7, preStart + 8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LyricsLine line = new LyricsLine();
        //***事件 毫秒为单位
        line.setTime((minute * 600 + second * 10 + msec) * 100);

        line.setLine(lyrics.substring(preStart + timeLength, end).trim());
        this.lyricsList.add(line);
    }

    /**
     * 获取歌词包含时间信息
     *
     * @return ArrayList<Map   <   String   ,       String>>
     */
    public ArrayList<LyricsLine> getLyrics() {
        return lyricsList;
    }

    public int getTotalHeight() {
        int height = 0;
        for (LyricsLine line : lyricsList) {
            height += line.getHeight();
        }
        return height;
    }
}
