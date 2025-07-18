package com.compose.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.compose.common.MusicItemComponent
import com.compose.common.PlayerInfoComponent
import com.compose.common.TopBarComponent
import com.compose.util.getValue
import com.compose.util.getViewModel
import com.web.moudle.music.player.other.PlayerConfig


@Preview(showBackground = true)
@Composable
fun MusicMainComponent() {
    val vm: ComposeMusicViewModel? = getViewModel()

    Column {
        TopBarComponent()
        val musicList by vm?.musicList?.collectAsState()
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val indexData by vm?.index?.collectAsState()
            val charIndex = remember { mutableIntStateOf(0) }
            val lazyListState = rememberLazyListState()
            // 监听滚动位置
            LaunchedEffect(lazyListState) {
                snapshotFlow {
                    return@snapshotFlow lazyListState.firstVisibleItemIndex
                }
                    .collect { index ->
                        val char = musicList?.getOrNull(index)?.firstChar
                        val newIndex = indexData?.indexOf(char) ?: return@collect
                        if (newIndex >= 0 && newIndex != charIndex.intValue) {
                            charIndex.intValue = newIndex
                        }
                    }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                state = lazyListState
            ) {
                // 音乐列表
                itemsIndexed(
                    items = musicList ?: emptyList(),
                    key = { index, item -> item.hashCode() }) { index, item ->
                    MusicItemComponent(music = item, click = {
                        vm?.action(ComposeMusicViewModel.Action.Play(item))
                    })
                }
            }
            // 首字母列表
            LazyColumn {
                itemsIndexed(
                    items = indexData ?: emptyList(),
                    key = { index, item -> item.hashCode() }) { index, item ->
                    Text(text = item.toString(), color = if (index == charIndex.intValue) Color.Red else Color.Unspecified)
                }
            }
        }
        val music by vm?.music?.collectAsState()
        // 播放器信息
        PlayerInfoComponent(music = music, bitmap = PlayerConfig.bitmap)
    }
}