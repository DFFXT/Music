package com.web.moudle.music.player.other;

import androidx.annotation.Nullable;

import com.web.data.Music;
import com.web.data.MusicList;
import com.web.moudle.music.player.other.PlayerConfig;

import java.util.List;

public interface PlayInterface {
    default void onPlay(){}
    default void onLoad(@Nullable Music music, int maxTime){}
    default void onPause(){}
    default void onCurrentTime(int duration, int maxTime){}
    default void onMusicListChange(List<Music> list){}
    default void onPlayTypeChanged(PlayerConfig.PlayType playType){}
    default void onMusicOriginChanged(PlayerConfig.MusicOrigin origin){}
    default void onBufferingUpdate(int percent){}
}
