package com.compose.page

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.compose.common.LyricsComponent
import com.compose.common.TopBarComponent
import com.compose.util.LocalNavi
import com.compose.util.Navi
import com.music.m.R
import com.web.moudle.music.player.other.PlayerConfig

@Composable
@Preview
fun MusicDetailComponent() {
    val controller = LocalNavi.current

    Column {
        TopBarComponent(title = PlayerConfig.music?.musicName ?: "--", startClick = {
            controller.popBackStack()
        }, endIcon = R.drawable.setting, endClick = {
            controller.navigate(Navi.MUSIC_SETTING)
        })
        LyricsComponent()
    }
}