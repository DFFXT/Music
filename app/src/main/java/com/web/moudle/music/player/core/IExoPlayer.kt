package com.web.moudle.music.player.core

import com.google.android.exoplayer2.BasePlayer

interface IExoPlayer : IPlayer {
    fun getExoPlayer(): BasePlayer
}