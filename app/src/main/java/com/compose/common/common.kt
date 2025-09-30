package com.compose.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.compose.ui.theme.Purple80
import com.music.m.R


@Preview(showBackground = true)
@Composable
fun TopBarComponent(
    title: String = "",
    startIcon: Int = R.drawable.icon_back_black,
    startClick: () -> Unit = {},
    endIcon: Int = 0,
    endClick: () -> Unit = {},
    endTitle: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clickable(onClick = startClick)
        ) {
            if (startIcon != 0) {
                Image(
                    painter = painterResource(id = startIcon),
                    colorFilter = ColorFilter.tint(Color.White),
                    contentDescription = null,
                )
            }
        }

        Text(title, color = Color.White)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clickable(onClick = endClick)
        ) {
            if (endIcon != 0) {
                Image(
                    painter = painterResource(id = endIcon),
                    colorFilter = ColorFilter.tint(Color.White),
                    contentDescription = null,
                    modifier = Modifier.clickable(onClick = endClick)
                )
            }
            if (endTitle != null) {
                Text(endTitle, color = Color.White, modifier = Modifier.align(Alignment.Center))
            }
        }

    }

}