package com.web.config;

import com.web.moudle.lyrics.LyricsLine;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricsAnalysis {
    //[00:00.00]
    private ArrayList<LyricsLine> lyricsList = new ArrayList<>();
    private String lyrics;
    private static int timeLength = 10;

    public LyricsAnalysis(String lyrics) {
        this.lyrics = lyrics;
        Pattern pattern = Pattern.compile("\\[\\d\\d:\\d\\d.\\d{2,3}]");
        Matcher m = pattern.matcher(lyrics);
        int preStart = -1;
        int start;
        while (m.find()) {
            start = m.start();
            timeLength = m.end() - m.start();
            add(preStart, start);
            preStart = start;
        }
        if (preStart >= 0) {//**添加最后一行歌词或只有一行歌词
            add(preStart, lyrics.length());
        }
    }

    /**
     * 添加一行歌词 [00:00.00]xxxxxx
     *
     * @param preStart 开始处
     * @param end      结束处
     */
    private void add(int preStart, int end) {
        int minute = 0;
        int second = 0;
        int msec = 0;
        if (preStart >= 0) {
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
    }

    /**
     * 获取歌词包含时间信息
     *
     * @return ArrayList<Map   <   String   ,       String>>
     */
    public ArrayList<LyricsLine> getLyrics() {
        return lyricsList;
    }
}
