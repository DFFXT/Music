package com.compose.common

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compose.page.ComposeMusicViewModel
import com.compose.preview.PreviewMusic
import com.compose.util.LocalNavi
import com.compose.util.Navi
import com.music.m.R
import com.web.common.util.ResUtil
import com.web.data.Music
import com.web.moudle.music.player.other.PlayerConfig

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PlayerInfoComponent(
    @PreviewParameter(PreviewMusic::class) music: Music?,
    bitmap: Bitmap? = PlayerConfig.bitmap
) {
    val vm: ComposeMusicViewModel? = if (!LocalInspectionMode.current) viewModel() else null
    val controller = LocalNavi.current
    Column(modifier = Modifier.padding(10.dp).clickable{
        controller.navigate(Navi.MUSIC_DETAIL)
    }) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            val progress = vm?.progress?.collectAsStateWithLifecycle()
            Text(
                text = ResUtil.timeFormat(
                    "mm:ss",
                    ((music?.duration ?: 0) * (progress?.value ?: 0f)).toLong()
                )
            )
            Slider(
                value = progress?.value ?: 0f,
                onValueChange = {
                    vm?.action(ComposeMusicViewModel.Action.Seek(it))
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            )
            Text(text = ResUtil.timeFormat("mm:ss", (music?.duration ?: 0).toLong()))
        }
        Row(
            modifier = Modifier.wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = (bitmap
                    ?: ResUtil.getBitmapFromResoucs(
                        R.drawable.singer_default_icon,
                        LocalContext.current
                    )).asImageBitmap(),
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = music?.musicName ?: "---",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(text = music?.singer ?: "---", overflow = TextOverflow.Ellipsis, maxLines = 1)
            }
        }
    }

}