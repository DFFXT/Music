package com.web.adpter;

import com.web.data.Music;
import com.web.data.MusicList;
import com.web.data.PlayerConfig;

import java.util.List;

public interface PlayInterface {
    void play(String musicName,String singerName,int maxTime);
    void load(String musicName,String singerName,int maxTime);
    void pause();
    void currentTime(int group,int child,int time);
    void musicListChange(List<MusicList<Music>> list);
    void playTypeChanged(PlayerConfig.PlayType playType);
    void musicOriginChanged(PlayerConfig.MusicOrigin origin);
}
