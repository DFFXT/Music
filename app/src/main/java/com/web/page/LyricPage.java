package com.web.page;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.web.config.GetFiles;
import com.web.config.LyricsAnalysis;
import com.web.config.Shortcut;
import com.web.data.Music;
import com.web.model.lyrics.model.LyricsLine;
import com.web.model.lyrics.ui.LyricsView;
import com.web.service.MusicPlay;
import com.web.util.StrUtil;
import com.web.web.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LyricPage {
    private View view;
    private LyricsView lyricsView;
    private MusicPlay.Connect connect;
    private ImageView lock;
    private boolean visibility=false;
    private List<LyricsLine> list=new ArrayList<>();
    public LyricPage( ViewGroup parent, MusicPlay.Connect connect){
        this.connect=connect;
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.music_lyrics_view,parent,false);
        lyricsView=view.findViewById(R.id.lyricListView);
        lyricsView.setLyrics(list);
        lock=lyricsView.findViewById(R.id.lock);
        lock.setTag(false);
        lock.setImageResource(R.drawable.unlock);
        lock.setOnClickListener((v)->{
            if((Boolean)lock.getTag()){
                lock.setTag(false);
                lock.setImageResource(R.drawable.unlock);
            }else {
                lock.setTag(true);
                lock.setImageResource(R.drawable.locked);
            }
            lyricsView.setDragEnable(!(Boolean) lock.getTag());
        });

    }

    public void setConnect(MusicPlay.Connect connect) {
        this.connect = connect;
        lyricsView.setSeekListener(seekTo -> {
            if (connect != null)
                connect.seekTo(seekTo * 100);
            return true;
        });
    }

    public void stop(){
        visibility=false;
    }
    public void show(){
        visibility=true;
    }



    public View getView() {
        return view;
    }


    public void loadLyrics(){//--设置歌词内容
        if(connect==null)return;
        Music music=connect.getPlayingMusic();
        if(music==null)return;
        list.clear();
        if(Shortcut.fileExsist(music.getLyricsPath())){//---存在歌词
            LyricsAnalysis lyricsAnalysis=new LyricsAnalysis(new GetFiles().readText(music.getLyricsPath()));
            list.addAll(lyricsAnalysis.getLyrics());
        }
        else {//**没找到歌词
            LyricsLine line=new LyricsLine();
            line.setTime(0);
            line.setLine(StrUtil.getString(R.string.lyrics_noLyrics));
            list.add(line);
        }
        lyricsView.refresh();
    }

    public void lyricsRun(int cu) {//----判定歌词时间线
        lyricsView.setCurrentTime(cu);
    }
}
