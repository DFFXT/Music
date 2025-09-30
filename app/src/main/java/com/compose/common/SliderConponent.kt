package com.compose.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.compose.page.ComposeMusicViewModel
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CustomSlider(modifier: Modifier = Modifier, value: Float = 0.5f, onValueChanged:((Float) -> Unit) = {}) {
    val colors = SliderDefaults.colors()
    var current by remember { mutableFloatStateOf(value) }
    var isDragging by remember { mutableStateOf(false) }
    if (!isDragging) {
        current = value
    }
    Slider(
        value = current,
        onValueChange = {
            isDragging = true
            current = it
        },
        onValueChangeFinished = {
            onValueChanged(current)
            isDragging = false
        },
        modifier = modifier.height(10.dp),
        thumb = { ss ->
            Box(modifier = Modifier.fillMaxHeight()) {
                Canvas(modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.Center)) {
                    drawCircle(colors.thumbColor, radius = min(size.width, size.height) / 2)
                }
            }
        },
        track = { ss ->
            val activeColor = colorScheme.error
            Box(modifier = Modifier.height(1.dp)) {
                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()) {
                    drawRect(Color.Gray,
                        topLeft = Offset(ss.value * size.width, 0f),
                        size = Size(size.width - ss.value * size.width, size.height))
                    drawRect(activeColor, size = Size(ss.value * size.width, size.height))
                }
            }

        }
    )
}