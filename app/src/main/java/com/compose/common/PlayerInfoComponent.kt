package com.compose.common

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compose.page.ComposeMusicViewModel
import com.compose.preview.PreviewMusic
import com.compose.util.LocalNavi
import com.compose.util.Navi
import com.google.android.material.slider.Slider
import com.music.m.R
import com.web.common.util.ResUtil
import com.web.data.Music
import com.web.moudle.music.player.other.PlayerConfig

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PlayerInfoComponent(
    @PreviewParameter(PreviewMusic::class) music: Music?,
    bitmap: Bitmap? = PlayerConfig.bitmap
) {
    val vm: ComposeMusicViewModel? = if (!LocalInspectionMode.current) viewModel() else null
    val controller = LocalNavi.current
    Column(
        modifier = Modifier
            .padding(10.dp)
            .clickable {
                controller.navigate(Navi.MUSIC_DETAIL, )
            }) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            val progress = vm?.progress?.collectAsStateWithLifecycle()
            Text(
                text = ResUtil.timeFormat(
                    "mm:ss",
                    ((music?.duration ?: 0) * (progress?.value ?: 0f)).toLong()
                )
            )

            CustomSlider(value = progress?.value ?: 0f, modifier = Modifier.weight(1f)
                .height(10.dp).padding(horizontal = 10.dp)) {
                vm?.action(ComposeMusicViewModel.Action.Seek(it))
            }
            Text(text = ResUtil.timeFormat("mm:ss", (music?.duration ?: 0).toLong()))
        }
        Row(
            modifier = Modifier.wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(60.dp),
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