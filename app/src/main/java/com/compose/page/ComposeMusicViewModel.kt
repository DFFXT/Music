package com.compose.page

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.web.app.MyApplication
import com.web.common.base.PlayerObserver
import com.web.data.Music
import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.other.PlayerConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ComposeMusicViewModel: ViewModel() {
    val musicList: MutableStateFlow<List<Music>> = MutableStateFlow(emptyList())
    val progress: MutableStateFlow<Float> = MutableStateFlow(0f)
    val music: MutableStateFlow<Music?> = MutableStateFlow(null)
    val index = MutableStateFlow<List<Char>>(emptyList())
    private val playerObserver = object : PlayerObserver() {
        override fun onMusicListChange(list: List<Music>) {
            val indexList = ArrayList<Char>()
            for (m in list) {
                if (indexList.lastOrNull() != m.firstChar) {
                    indexList.add(m.firstChar)
                }
            }
            musicList.tryEmit(list)
            index.tryEmit(indexList)
        }

        override fun onCurrentTime(duration: Int, maxTime: Int) {
            progress.tryEmit(duration / maxTime.toFloat())
        }

        override fun onLoad(music: Music?, maxTime: Int) {
            this@ComposeMusicViewModel.music.tryEmit(music)
        }
    }
    private lateinit var control: IMusicControl
    init {
        NewPlayer.bind(MyApplication.context, playerObserver) {
            control = it
            it.getPlayerInfo(null)
        }
    }

    fun action(action: Action) {
        if (!::control.isInitialized) return
        when(action) {
            is Action.Play -> {
                control.play(action.music)
            }
            Action.Pause -> {
                control.pause()
            }
            is Action.Seek -> {
                val max = PlayerConfig.music?.duration ?: return
                control.seekTo((max * action.percent).toInt())
            }
        }
    }

    sealed class Action {
        data class Play(val music: Music) : Action()
        object Pause : Action()

        data class Seek(val percent: Float) : Action()
    }
}