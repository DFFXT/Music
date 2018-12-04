package com.web.moudle.music.player;

import com.web.data.Music;
import com.web.data.MusicList;
import com.web.data.PlayerConfig;

import java.util.List;

public interface PlayInterface {
    void play();
    void load(int groupIndex,int childIndex,Music music,int maxTime);
    void pause();
    void currentTime(int group,int child,int time);
    void musicListChange(int group,List<MusicList<Music>> list);
    void playTypeChanged(PlayerConfig.PlayType playType);
    void musicOriginChanged(PlayerConfig.MusicOrigin origin);
}
