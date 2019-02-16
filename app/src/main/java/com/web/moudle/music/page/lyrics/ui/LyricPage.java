package com.web.moudle.music.page.lyrics.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.web.common.util.ResUtil;
import com.web.config.GetFiles;
import com.web.config.LyricsAnalysis;
import com.web.config.Shortcut;
import com.web.data.Music;
import com.web.moudle.music.page.BaseMusicPage;
import com.web.moudle.music.page.lyrics.model.LyricsLine;
import com.web.moudle.music.page.lyrics.ui.LyricsView;
import com.web.moudle.music.player.MusicPlay;
import com.web.web.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LyricPage extends BaseMusicPage {
    public final static String pageName="lyricPage";
    private LyricsView lyricsView;
    private MusicPlay.Connect connect;
    private ImageView lock;
    private List<LyricsLine> list=new ArrayList<>();
    private boolean inited=false;


    public void setConnect(@NonNull MusicPlay.Connect connect) {
        this.connect = connect;
        if(inited){
            lyricsView.setSeekListener(seekTo -> {
                connect.seekTo(seekTo);
                Log.i("log","---"+seekTo);
                return true;
            });
        }
    }

    @NotNull
    @Override
    public String getPageName() {
        return pageName;
    }

    @Override
    public void setTitle(@NotNull TextView textView) {
        textView.setText(ResUtil.getString(R.string.page_lyrics));
        textView.setCompoundDrawables(null, null, null, null);
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
            line.setLine(ResUtil.getString(R.string.lyrics_noLyrics));
            list.add(line);
        }
        lyricsView.setLyrics(list);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden&&connect!=null){
            lyricsView.setCurrentTimeImmediately(connect.getCurrentPosition());
        }
    }

    /**
     *
     * @param cu
     */
    public void lyricsRun(int cu) {//----判定歌词时间线
        if (inited)
            lyricsView.setCurrentTime(cu);
    }

    @Override
    public int getLayoutId() {
        return R.layout.music_lyrics_view;
    }

    @Override
    public void viewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void initView(@NotNull View rootView) {
        if(inited)return;
        lyricsView=rootView.findViewById(R.id.lyricListView);
        lyricsView.setTextColor(ResUtil.getColor(R.color.themeColor));
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
            lyricsView.setCanScroll(!(Boolean) lock.getTag());
        });
        inited=true;
        lyricsView.post(this::loadLyrics);

    }
}
