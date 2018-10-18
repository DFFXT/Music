package com.web.music.page;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.web.common.base.BaseFragment;
import com.web.config.GetFiles;
import com.web.config.LyricsAnalysis;
import com.web.config.Shortcut;
import com.web.data.Music;
import com.web.music.model.control.interf.IPage;
import com.web.music.model.lyrics.model.LyricsLine;
import com.web.music.model.lyrics.ui.LyricsView;
import com.web.service.MusicPlay;
import com.web.util.StrUtil;
import com.web.web.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LyricPage extends BaseFragment implements IPage {
    private LyricsView lyricsView;
    private MusicPlay.Connect connect;
    private ImageView lock;
    private List<LyricsLine> list=new ArrayList<>();
    private boolean inited=false;


    public void setConnect(@NonNull MusicPlay.Connect connect) {
        this.connect = connect;
        if(inited){
            lyricsView.setSeekListener(seekTo -> {
                connect.seekTo(seekTo * 100);
                return true;
            });
        }
    }





    public void loadLyrics(){//--设置歌词内容
        if(connect==null||!inited)return;
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
        if (inited)
            lyricsView.setCurrentTime(cu);
    }

    @Override
    public int getLayoutId() {
        return R.layout.music_lyrics_view;
    }

    @Override
    public void initView(@NotNull View rootView) {
        lyricsView=rootView.findViewById(R.id.lyricListView);
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
        inited=true;
        lyricsView.post(this::loadLyrics);

    }
}
