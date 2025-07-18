package com.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.compose.preview.PreviewMusic
import com.web.data.Music


@Preview(showBackground = true)
@Composable
fun MusicItemComponent(@PreviewParameter(PreviewMusic::class) music: Music, click:() -> Unit = {}) {
    Column {


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = click)
                .padding(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f, false)) {
                Text(text = music.musicName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = music.singer, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(
                text = music.durationFormatted,
                maxLines = 1,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .height(1.dp)
        ) { }
    }
}