package com.compose.common

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.compose.page.ComposeMusicViewModel
import com.compose.util.getViewModel
import com.web.config.LyricsAnalysis
import kotlin.math.max
import kotlin.math.min

val testLrc = "[Verse]\n" +
        "[00:23.479]我怎么舍得看不见\n" +
        "[00:29.180]那一张清秀完美的脸\n" +
        "[00:34.104]雨点掉落下来 打湿整个屋檐\n" +
        "[00:39.927]你淋湿站在我左边\n" +
        "[Verse]\n" +
        "[00:46.906]你美的像幅泼墨画中的仙\n" +
        "[00:52.701]我靠近递你一张手绢\n" +
        "[00:57.441]你突然的笑了 道谢说得腼腆\n" +
        "[01:03.328]骤雨停了 你就这样越走越远\n" +
        "[Verse]\n" +
        "[01:10.177]青石板的马路边 那离别似空间\n" +
        "[01:16.092]勾起我不断对你的思念\n" +
        "[01:21.943]倘若雨势再蔓延 能再多看你几眼\n" +
        "[01:27.721]现唯借手绢吻你的脸\n" +
        "[Verse]\n" +
        "[01:33.649]泪水划过我唇边 笔墨挥洒宣纸砚\n" +
        "[01:39.486]刻画出对你无尽的思念\n" +
        "[01:45.268]如果还能在雨天遇见\n" +
        "[01:50.112]可否能邀画中的仙 赏花儿月圆\n" +
        "[Verse]\n" +
        "[02:19.808]你美的像幅泼墨画中的仙\n" +
        "[02:25.650]我靠近递你一张手绢\n" +
        "[02:30.387]你突然的笑了 道谢说得腼腆\n" +
        "[02:36.135]骤雨停了 你就这样越走越远\n" +
        "[Verse]\n" +
        "[02:43.146]青石板的马路边 那离别似空间\n" +
        "[02:49.027]勾起我不断对你的思念\n" +
        "[02:54.890]倘若雨势再蔓延 能再多看你几眼\n" +
        "[03:00.721]现唯借手绢吻你的脸\n" +
        "[Verse]\n" +
        "[03:06.503]泪水划过我唇边 笔墨挥洒宣纸砚\n" +
        "[03:12.430]刻画出对你无尽的思念\n" +
        "[03:18.306]如果还能在雨天遇见\n" +
        "[03:23.053]可否能邀画中的仙 赏花儿月圆\n" +
        "[Verse]\n" +
        "[03:40.239]哦 青石板的马路边 那离别似空间\n" +
        "[03:47.566]勾起我不断对你的思念\n" +
        "[03:53.310]倘若雨势再蔓延 能再多看你几眼\n" +
        "[03:59.241]现唯借手绢吻你的脸\n" +
        "[Verse]\n" +
        "[04:05.123]泪水划过我唇边 笔墨挥洒宣纸砚\n" +
        "[04:11.004]刻画出对你无尽的思念\n" +
        "[04:16.822]如果还能在雨天遇见\n" +
        "[04:21.566]可否能邀画中的仙 赏花儿月圆"

// https://api.lrc.cx/lyrics?title=画中仙
// https://docs.lrc.cx/docs/QuickStart
@Composable
@Preview
fun LyricsComponent() {
    val vm: ComposeMusicViewModel? = getViewModel()
    val lyricsAnalysis = remember { LyricsAnalysis(testLrc) }
    val textMeasurer = rememberTextMeasurer()
    // 当前时间正在播放的行
    val time = vm?.duration?.collectAsState() ?: remember {
        mutableLongStateOf(2300)
    }


    // offsetTop 范围：【size.height / 2f，size.height / 2f + 所有文字高度】
    val offsetTop = remember { mutableFloatStateOf(Float.NaN) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    var newOffset = offsetTop.floatValue + dragAmount.y
                    newOffset = min(newOffset, size.height / 2f)
                    newOffset = max(newOffset, size.height / 2f - lyricsAnalysis.totalHeight)
                    offsetTop.floatValue = newOffset
                }
            }
            .graphicsLayer{
                this.compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawBehind {
                // offsetTop 范围：【size.height / 2f，size.height / 2f + 所有文字高度】
                if (offsetTop.floatValue.isNaN()) {
                    offsetTop.floatValue = size.height / 2f
                }
                var top = offsetTop.floatValue
                var lineHeight = 0

                for (i in 0 until lyricsAnalysis.lyrics.size) {
                    val curr = lyricsAnalysis.lyrics[i]
                    val line = curr.line
                    //curr.width = 0
                    val result = textMeasurer.measure(text = line, style = TextStyle(fontSize = 20.sp))
                    //curr.width += result.size.width
                    curr.height = result.size.height
                    // 将当前行定位到中间
                    if (time.value > curr.time) {
                        top = top - curr.height
                    } else {
                        if (top != offsetTop.floatValue) {
                            top = top + curr.height
                        }
                        break
                    }
                }



                for (i in 0 until lyricsAnalysis.lyrics.size) {
                    val curr = lyricsAnalysis.lyrics[i]
                    val line = curr.line
                    val result = textMeasurer.measure(text = line, style = TextStyle(
                        color = Color.Black,
                        fontSize = 20.sp
                    ))
                    lineHeight = result.size.height
                    val left = size.width / 2f - result.size.width / 2f
                    curr.height = lineHeight
                    drawText(
                        textMeasurer,
                        text = line,
                        topLeft = Offset(left, top),
                        softWrap = false,
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    )
                    for (j in 0 until result.lineCount) {
                        top = top + lineHeight
                    }
                    // 不能超出画布，不然不显示
                    if (top >= size.height) {
                        break
                    }

                }
                drawRect(
                    Color.Red,
                    topLeft = Offset(0f, size.height / 2f),
                    size = Size(size.width, lineHeight.toFloat()),
                    blendMode = BlendMode.SrcAtop
                )
            }
    ) {
        Text(text = offsetTop.floatValue.toString())
    }

}